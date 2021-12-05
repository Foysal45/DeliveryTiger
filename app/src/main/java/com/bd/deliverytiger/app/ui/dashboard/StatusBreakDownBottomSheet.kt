package com.bd.deliverytiger.app.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.BuildConfig
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.chat.ChatUserData
import com.bd.deliverytiger.app.api.model.chat.FirebaseCredential
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.databinding.FragmentStatusBreakDownBinding
import com.bd.deliverytiger.app.ui.chat.ChatConfigure
import com.bd.deliverytiger.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

class StatusBreakDownBottomSheet(): BottomSheetDialogFragment() {

    private var binding: FragmentStatusBreakDownBinding? = null
    var onItemClicked: ((model: DashboardData, position: Int) -> Unit)? = null
    var onMapClick: ((model: DashboardData, position: Int) -> Unit)? = null

    private var dataList: MutableList<DashboardData> = mutableListOf()
    private var title: String? = ""
    private var flag = 0

    companion object {
        fun newInstance(title: String, dataList: MutableList<DashboardData>, flag: Int): StatusBreakDownBottomSheet = StatusBreakDownBottomSheet().apply {
            this.title = title
            this.dataList = dataList
            this.flag = flag
        }
        val tag: String = StatusBreakDownBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //val metrics = resources.displayMetrics

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                //state = BottomSheetBehavior.STATE_COLLAPSED
                skipCollapsed = false
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentStatusBreakDownBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.titleTV?.text = title

        if (flag == 3){
            binding?.chatLayout?.visibility = View.VISIBLE
        } else{
            binding?.chatLayout?.visibility = View.GONE
        }

        val dataAdapter = ReturnAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }
        dataAdapter.initData(dataList)
        dataAdapter.onItemClick = { position, model ->
            onItemClicked?.invoke(model, position)
        }
        dataAdapter.onMapClick = { position, model ->
            onMapClick?.invoke(model, position)
        }

        binding?.chatLayout?.setOnClickListener {
            goToChatActivity()
        }

    }

    private fun goToChatActivity() {
        val firebaseCredential = FirebaseCredential(
            firebaseWebApiKey = BuildConfig.FirebaseWebApiKey
        )
        val senderData = ChatUserData(SessionManager.courierUserId.toString(), SessionManager.companyName, SessionManager.mobile,
            imageUrl = "https://static.ajkerdeal.com/delivery_tiger/profile/${SessionManager.courierUserId}.jpg",
            role = "dt",
            fcmToken = SessionManager.firebaseToken
        )
        val receiverData = ChatUserData("1443", "Return Team", "01300000000",
            imageUrl = "https://static.ajkerdeal.com/images/admin_users/dt/938.jpg",
            role = "retention"
        )
        ChatConfigure(
            "dt-retention",
            senderData,
            firebaseCredential = firebaseCredential,
            receiver = receiverData
        ).config(requireContext())
    }


}