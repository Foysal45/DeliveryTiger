package com.bd.deliverytiger.app.ui.chat.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.HistoryData
import com.bd.deliverytiger.app.databinding.FragmentChatHistoryBinding
import com.bd.deliverytiger.app.ui.chat.ChatActivity
import com.bd.deliverytiger.app.ui.chat.compose.ChatComposeFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.bd.deliverytiger.app.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class ChatHistoryFragment: Fragment() {

    private var binding: FragmentChatHistoryBinding? = null

    private lateinit var firebaseApp: FirebaseApp
    private lateinit var firebaseDatabase: FirebaseFirestore
    private lateinit var historyCollection: CollectionReference
    private lateinit var userSenderCollection: CollectionReference
    private var realTimeListenerRegistration: ListenerRegistration? = null


    private var sender: ChatUserData? = null
    private var receiver: ChatUserData? = null
    private var documentName: String? = null
    private var firebaseStorageUrl: String? = null
    private var firebaseWebApiKey: String? = null
    private var role: String? = null
    private var isLoading: Boolean = false

    private lateinit var dataAdapter: ChatHistoryAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        fun newInstance(firebaseApp: FirebaseApp, bundle: Bundle) = ChatHistoryFragment().apply {
            this.firebaseApp = firebaseApp
            this.arguments = bundle
        }

        val tag: String = ChatHistoryFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentChatHistoryBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
        initChat()
        fetchHistoryData()
        updateSenderData()
    }

    override fun onResume() {
        super.onResume()
        activity?.let {
            (it as ChatActivity).setToolbar("Chat History")
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

    private fun initView() {
        dataAdapter = ChatHistoryAdapter()
        linearLayoutManager = LinearLayoutManager(requireContext())
        binding?.recyclerView?.run {
            setHasFixedSize(true)
            layoutManager = linearLayoutManager
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onItemClicked = { model ->
            goToChatCompose(model)
        }

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy < 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                    //Timber.d("onScrolled","${!isLoading} $currentItemCount <= ${firstVisibleItem + visibleThreshold}")
                    if (!isLoading && firstVisibleItem < 5 /*&& currentItemCount > queryLimit - 1 && currentItemCount < totalCount*/) {
                        isLoading = true
                        fetchMoreHistoryData(20)
                    }
                }
            }
        })
    }

    private fun initChat() {
        val historyNode = "chat/$documentName/history/$role/${sender?.id}"
        firebaseDatabase = Firebase.firestore //FirebaseFirestore.getInstance(firebaseApp)
        historyCollection = firebaseDatabase.collection(historyNode)

        val userSenderNode = "chat/$documentName/user-$role"
        userSenderCollection = firebaseDatabase.collection(userSenderNode)
    }

    private fun fetchHistoryData() {
        historyCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(20)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) return@addOnSuccessListener
                val chatHistoryList = documents.toObjects(HistoryData::class.java)
                dataAdapter.initLoad(chatHistoryList)
                initRealTimeListener()
            }
    }

    private fun fetchMoreHistoryData(limit: Long = 20) {
        //fetch init data
        binding?.progressBar?.isVisible = true
        val lastModel = dataAdapter.lastItem()
        historyCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .startAfter(lastModel.date)
            .limit(limit)
            .get()
            .addOnSuccessListener { documents ->
                isLoading = false
                binding?.progressBar?.isVisible = false
                if (documents.isEmpty) return@addOnSuccessListener
                val chatHistoryList = documents.toObjects(HistoryData::class.java)
                dataAdapter.pagingLoad(chatHistoryList)
                Timber.tag("chatDebug").d("fetchMoreChatData chatHistoryList $chatHistoryList")
            }.addOnFailureListener {
                isLoading = false
                binding?.progressBar?.isVisible = false
            }
    }

    private fun initRealTimeListener() {
        //listen for real time change
        realTimeListenerRegistration = historyCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener(realTimeUpdateLister)
    }

    private val realTimeUpdateLister = EventListener<QuerySnapshot> { snapshot, error ->
        if (error != null) return@EventListener
        if (snapshot != null && !snapshot.isEmpty) {
            for (doc in snapshot.documentChanges) {
                if (doc.type == DocumentChange.Type.ADDED) {
                    val chatList = doc.document.toObject(HistoryData::class.java)
                    dataAdapter.addNewData(chatList)
                    smoothScrollToNewMsg()
                } else if (doc.type == DocumentChange.Type.MODIFIED) {
                    val chatList = doc.document.toObject(HistoryData::class.java)
                    dataAdapter.addUniqueHistory(chatList)
                    smoothScrollToNewMsg()
                }
            }
        }
    }

    private fun smoothScrollToNewMsg() {
        //val currentItemCount = dataAdapter.itemCount
        val visibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
        Timber.tag("chatDebug").d( "$visibleItemPosition")
        if (visibleItemPosition < 4) {
            binding?.recyclerView?.smoothScrollToPosition(0)
        }
    }

    private fun updateSenderData() {
        userSenderCollection
            .document("${sender?.id}")
            .set(sender!!)
    }

    private fun goToChatCompose(model: HistoryData) {

        val bundle = bundleOf(
            "documentName" to documentName,
            "firebaseStorageUrl" to firebaseStorageUrl,
            "firebaseWebApiKey" to firebaseWebApiKey,
            "sender" to sender,
            "receiver" to ChatUserData(
                model.receiverId,
                model.receiverName,
                role = model.receiverRole
            )
        )

        val fragment = ChatComposeFragment.newInstance(firebaseApp, bundle)
        val tag = ChatComposeFragment.tag
        activity?.supportFragmentManager?.beginTransaction()?.run {
            add(R.id.container, fragment, tag)
            addToBackStack(tag)
            commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}