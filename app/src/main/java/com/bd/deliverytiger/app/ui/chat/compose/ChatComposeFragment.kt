package com.bd.deliverytiger.app.ui.chat.compose

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.chat.ChatData
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.HistoryData
import com.bd.deliverytiger.app.api.model.fcm.FCMDataModel
import com.bd.deliverytiger.app.api.model.fcm.FCMNotification
import com.bd.deliverytiger.app.api.model.fcm.FCMRequest
import com.bd.deliverytiger.app.databinding.FragmentChatComposeBinding
import com.bd.deliverytiger.app.ui.chat.ChatActivity
import com.bd.deliverytiger.app.utils.FileUtils
import com.bd.deliverytiger.app.utils.toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import org.koin.android.ext.android.inject
import com.bd.deliverytiger.app.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatComposeFragment : Fragment() {

    private var binding: FragmentChatComposeBinding? = null

    private lateinit var firebaseApp: FirebaseApp
    private lateinit var firebaseDatabase: FirebaseFirestore
    private lateinit var roomCollection: CollectionReference
    private lateinit var historySenderCollection: CollectionReference
    private lateinit var historyReceiverCollection: CollectionReference
    private lateinit var userReceiverCollection: CollectionReference
    private lateinit var userSenderCollection: CollectionReference
    private var realTimeListenerRegistration: ListenerRegistration? = null
    private lateinit var imageStorageRef: StorageReference

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dataAdapter: ChatAdapter
    private var sender: ChatUserData? = null
    private var receiver: ChatUserData? = null
    private var documentName: String? = null
    private var firebaseStorageUrl: String? = null
    private var firebaseWebApiKey: String? = null
    private var role: String? = null
    private var chatNode: String = ""
    private var chatData: ChatData? = null
    private var filePath: String = ""
    private var fileUri: Uri = Uri.EMPTY
    private val sdf = SimpleDateFormat("MMMM dd, yy - hh:mm a", Locale.US)
    private val options = RequestOptions().placeholder(R.drawable.ic_placeholder).error(R.drawable.ic_placeholder)
    private var lastMsg: String = ""
    private var isLoading: Boolean = false
    private var receiverFcmToken: String = ""
    private var receiverCurrentRoomId: String = ""
    private var roomId: String = ""

    private val viewModel: ChatComposeViewModel by inject()

    companion object {
        fun newInstance(firebaseApp: FirebaseApp, bundle: Bundle) = ChatComposeFragment().apply {
            this.firebaseApp = firebaseApp
            this.arguments = bundle
        }

        val tag: String = ChatComposeFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding?.root ?: FragmentChatComposeBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initViews()
        initChat()
        fetchChatData()
        seenStatusUpdate()
        fetchReceiverData()
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as ChatActivity).setToolbar(receiver?.name ?: "Chat")
        }
    }

    private fun initData() {

        val bundle = arguments
        documentName = bundle?.getString("documentName")
        firebaseStorageUrl = bundle?.getString("firebaseStorageUrl")
        firebaseWebApiKey = bundle?.getString("firebaseWebApiKey")
        sender = bundle?.getParcelable("sender")
        receiver = bundle?.getParcelable("receiver")
        role = sender?.role ?: throw Exception("Invalid sender role")
    }

    private fun initViews() {
        binding?.progressBar?.isVisible = true
        dataAdapter = ChatAdapter(sender!!.id)
        linearLayoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding?.recyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = dataAdapter
            }
        }

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    //Log.d("onScrolled","${!isLoading} $currentItemCount <= ${firstVisibleItem + visibleThreshold}")
                    if (!isLoading && firstVisibleItem < 5 /*&& currentItemCount > queryLimit - 1 && currentItemCount < totalCount*/) {
                        isLoading = true
                        fetchMoreChatData(20)
                    }
                }
            }
        })

        dataAdapter.onItemClicked = { model ->
            when (model.type) {
                "msg" -> {
                    onChatClicked(model)
                }
                "img" -> {
                    pictureDialog(model.url)
                }
            }
        }

        binding?.sendBtn?.setOnClickListener {
            generateChatMessage(1)
        }

        binding?.fileCloseBtn?.setOnClickListener {
            filePath = ""
            binding?.filePreviewLayout?.isVisible = false
            binding?.imagePreview?.let { view ->
                Glide.with(requireContext()).clear(view)
            }
        }

        binding?.addBtn?.setOnClickListener {
            if (isCheckPermission()) {
                pickUpImage()
            }
        }

        binding?.fileSendBtn?.setOnClickListener {
            sendFile()
        }
    }

    private fun initChat() {

        val path = "chat/$documentName/room/p2p"
        roomId = createRoom(sender, receiver) ?: throw Exception("Invalid room id") //783-1
        chatNode = "$path/$roomId"
        firebaseDatabase = Firebase.firestore //FirebaseFirestore.getInstance(firebaseApp)
        roomCollection = firebaseDatabase.collection(chatNode)

        val historySenderNode = "chat/$documentName/history/$role/${sender?.id}"
        historySenderCollection = firebaseDatabase.collection(historySenderNode)
        val historyReceiverNode = "chat/$documentName/history/${receiver!!.role}/${receiver?.id}"
        historyReceiverCollection = firebaseDatabase.collection(historyReceiverNode)

        val userReceiverNode = "chat/$documentName/user-${receiver?.role}"
        userReceiverCollection = firebaseDatabase.collection(userReceiverNode)
        val userSenderNode = "chat/$documentName/user-${sender?.role}"
        userSenderCollection = firebaseDatabase.collection(userSenderNode)

        val firebaseStorage = Firebase.storage //FirebaseStorage.getInstance(firebaseApp, firebaseStorageUrl!!)
        val storageRef = firebaseStorage.reference

        imageStorageRef = storageRef.child("$documentName/${role}/${sender?.id}")
    }

    private fun createRoom(sender: ChatUserData?, receiver: ChatUserData?): String? {

        val senderId = sender?.id?.toIntOrNull() ?: return null
        val receiverId = receiver?.id?.toIntOrNull() ?: return null

        return if (senderId >= receiverId) {
            "$senderId-$receiverId"
        } else {
            "$receiverId-$senderId"
        }
    }

    private fun fetchChatData() {
        //fetch init data
        roomCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                binding?.progressBar?.isVisible = false
                if (documents.isEmpty) return@addOnSuccessListener
                val chatList = documents.toObjects(ChatData::class.java)
                chatList.reverse()
                dataAdapter.initLoad(chatList)
                initRealTimeListener()
                Log.d("chatDebug", "fetchChatData chatList $chatList")
            }.addOnFailureListener {
                binding?.progressBar?.isVisible = false
            }
    }

    private fun fetchMoreChatData(limit: Long = 20) {
        //fetch init data
        binding?.progressBar?.isVisible = true
        val lastModel = dataAdapter.firstItem()
        roomCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .startAfter(lastModel.date)
            .limit(limit)
            .get()
            .addOnSuccessListener { documents ->
                isLoading = false
                binding?.progressBar?.isVisible = false
                if (documents.isEmpty) return@addOnSuccessListener
                val chatList = documents.toObjects(ChatData::class.java)
                chatList.reverse()
                dataAdapter.pagingLoadReverse(chatList)
                Log.d("chatDebug", "fetchMoreChatData chatList $chatList")
            }.addOnFailureListener {
                isLoading = false
                binding?.progressBar?.isVisible = false
            }
    }

    private fun initRealTimeListener() {
        //listen for real time change
        realTimeListenerRegistration = roomCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener(realTimeUpdateLister)
    }

    private val realTimeUpdateLister = EventListener<QuerySnapshot> { snapshot, error ->
        if (error != null) return@EventListener
        if (snapshot != null && !snapshot.isEmpty) {
            for (doc in snapshot.documentChanges) {
                if (doc.type == DocumentChange.Type.ADDED) {
                    val chatList = doc.document.toObject(ChatData::class.java)
                    dataAdapter.addNewData(chatList)
                    lastMsg = if (chatList.message.isNotEmpty()) chatList.message else "Send a image"
                    chatData?.run {
                        message = lastMsg
                    }
                    smoothScrollToNewMsg()
                }
            }
            Log.d("chatDebug", "realTimeUpdateLister snapshot ${snapshot.documents}")
        }
    }

    private fun smoothScrollToNewMsg() {
        val currentItemCount = dataAdapter.itemCount
        val visibleItemPosition = linearLayoutManager.findLastVisibleItemPosition()
        Log.d("debugSmoothScroll", "$visibleItemPosition $currentItemCount")
        val diff = (currentItemCount - visibleItemPosition)
        if (diff < 3) {
            binding?.recyclerView?.smoothScrollToPosition(currentItemCount)
        }
    }

    private fun generateChatMessage(type: Int) {

        val message = binding?.messageET?.text?.toString()?.trim() ?: ""
        if (message.isEmpty()) return
        var model = ChatData()
        when (type) {
            // data msg
            1 -> {
                model = ChatData(
                    sender!!.id, message, "", "msg", sender!!.imageUrl, Date().time
                )
                sendChatMessage(model)
            }
            // image msg
            2 -> {

                val fileName = Date().time
                val imageKeyRef = imageStorageRef.child("$fileName.jpg")
                //val uri = FileProvider.getUriForFile(requireContext(), requireContext().packageName + ".my.package.name.provider", File(filePath!!))
                val uri = FileProvider.getUriForFile(requireContext(), "com.github.anupdey99.chatkitlib.provider", File(filePath))
                imageKeyRef.putFile(uri).addOnCompleteListener { task ->
                    binding?.progressBar1?.isVisible = false
                    binding?.fileSendBtn?.isEnabled = true
                    binding?.filePreviewLayout?.visibility = View.GONE
                    if (task.isSuccessful) {
                        imageKeyRef.downloadUrl.addOnSuccessListener { uri ->
                            val downloadUrl = uri.toString()
                            Log.d("chatDebug", "Image $downloadUrl")
                            model = ChatData(
                                sender!!.id, message, downloadUrl, "img", sender!!.imageUrl, Date().time
                            )
                            sendChatMessage(model)
                        }
                    } else {
                        Log.d("chatDebug", "putFile error ${task.exception}")
                    }
                }
            }
        }

    }

    private fun timeStamp(date: Date): String {
        return sdf.format(date)
    }

    private fun sendChatMessage(model: ChatData) {

        chatData = model
        roomCollection.add(model)
        sendPushNotification(model)
        binding?.messageET?.text?.clear()
    }

    private fun updateChatHistory(model: ChatData?) {
        model ?: return
        val historySenderModel = HistoryData(
            receiver?.id ?: "0",
            receiver?.name ?: "",
            receiver?.role ?: "",
            receiver?.imageUrl ?: "",
            sender?.id ?: "",
            model.message,
            model.url,
            model.type,
            model.date,
            0
        )
        val historyReceiverModel = HistoryData(
            sender?.id ?: "0",
            sender?.name ?: "",
            sender?.role ?: "",
            sender?.imageUrl ?: "",
            receiver?.id ?: "",
            model.message,
            model.url,
            model.type,
            model.date,
            0
        )
        historySenderCollection
            .document("${receiver?.id}")
            .set(historySenderModel)
        historyReceiverCollection
            .document("${sender?.id}")
            .set(historyReceiverModel)
    }

    private fun seenStatusUpdate() {
        historySenderCollection
            .document("${receiver?.id}")
            .update("seenStatus", 1)
    }

    private fun fetchReceiverData() {
        userReceiverCollection
            .document("${receiver?.id}")
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val receiverData = document.toObject(ChatUserData::class.java)
                    receiverFcmToken = receiverData?.fcmToken ?: ""
                    receiverCurrentRoomId = receiverData?.currentRoomId ?: ""
                }
            }
    }

    private fun updateCurrentRoomId(currentRoomId: String) {
        userSenderCollection
            .document("${sender?.id}")
            .update("currentRoomId", currentRoomId)
    }

    private fun sendPushNotification(model: ChatData) {
        val token = receiverFcmToken
        if (token.isEmpty() || firebaseWebApiKey.isNullOrEmpty()) return
        if (receiverCurrentRoomId == roomId) return
        val title = sender?.name ?: "Sender"
        val body = if (model.message.isNotEmpty()) model.message else "Send a image"
        val notificationModel = FCMNotification(
            title, body
        )
        val dataModel = FCMDataModel(
            title, body, model.date.toString(), sender?.name ?: "", documentName ?: "chat"
        )
        val requestBody = FCMRequest(token, notificationModel, dataModel)
        viewModel.sendPushNotifications(firebaseWebApiKey!!, requestBody)
    }

    override fun onStart() {
        super.onStart()
        realTimeListenerRegistration?.let {
            initRealTimeListener()
        }
        updateCurrentRoomId(roomId)
    }

    override fun onStop() {
        super.onStop()
        realTimeListenerRegistration?.remove()
        updateChatHistory(chatData)
        updateCurrentRoomId("")
    }

    private fun pickUpImage() {
        ImagePicker.with(this)
            .compress(200)
            .createIntent { intent ->
                startForImageResult.launch(intent)
            }
    }

    private val startForImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            Log.d("chatDebug", "fileUri $uri")
            if (uri == null) {
                Log.d("chatDebug", "fileUri $uri")
                context?.toast("File not selected")
                return@registerForActivityResult
            }
            fileUri = uri

            val fileUtils = FileUtils(requireContext())
            filePath = fileUtils.getPath(uri) ?: (fileUri.path ?: "")
            Log.d("chatDebug", "filePath $filePath")

            //val validUri = FileProvider.getUriForFile(requireContext(), "com.github.anupdey99.chatkitlib.provider", File(uri.path))
            //Log.d("chatDebug", "validUri $validUri")


            binding?.filePreviewLayout?.isVisible = true
            binding?.imagePreview?.let { view ->
                Glide.with(requireContext())
                    .load(filePath)
                    .apply(options)
                    .into(view)
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            context?.toast(ImagePicker.getError(data))
        }
    }

    private fun sendFile() {
        if (filePath.isNotEmpty()) {
            binding?.progressBar1?.isVisible = true
            binding?.fileSendBtn?.isEnabled = false
            generateChatMessage(2)
        }
    }

    private fun pictureDialog(url: String) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_product_overview, null)
        builder.setView(view)
        val title: TextView = view.findViewById(R.id.title)
        val productImage: ImageView = view.findViewById(R.id.image)
        val close: ImageView = view.findViewById(R.id.close)

        Glide.with(productImage)
            .load(url)
            //.apply(RequestOptions().placeholder(R.drawable.ic_logo_ad1))
            .into(productImage)

        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun onChatClicked(model: ChatData) {

    }

    private fun isCheckPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
        val permission2 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return when {
            permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED -> {
                true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) || shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
            else -> {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                false
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        var isCameraGranted: Boolean = false
        var isStorageGranted: Boolean = false
        permissions.entries.forEach { permission ->
            if (permission.key == Manifest.permission.CAMERA) {
                isCameraGranted = permission.value
            }
            if (permission.key == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                isStorageGranted = permission.value
            }
        }
        if (isCameraGranted || isStorageGranted) {
            pickUpImage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}