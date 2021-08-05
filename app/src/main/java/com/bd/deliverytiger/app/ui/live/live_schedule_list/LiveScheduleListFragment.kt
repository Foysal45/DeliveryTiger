package com.bd.deliverytiger.app.ui.live.live_schedule_list

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.live.auth.AuthRequestBody
import com.bd.deliverytiger.app.api.model.live.live_schedule_list.MyLiveSchedule
import com.bd.deliverytiger.app.databinding.FragmentLiveScheduleListBinding
import com.bd.deliverytiger.app.enums.LiveType
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.live.home.LiveHomeActivity
import com.bd.deliverytiger.app.ui.live.live_schedule.LiveScheduleActivity
import com.bd.deliverytiger.app.utils.*
import com.google.android.material.tabs.TabLayout
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiveScheduleListFragment(): Fragment() {

    private var binding: FragmentLiveScheduleListBinding? = null
    private val viewModel: LiveScheduleListViewModel by inject()

    private val requestCodeCamera = 123
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var liveSchedule: MyLiveSchedule = MyLiveSchedule()
    private var liveDate: String = ""
    private var liveStartTime: String = ""
    private var liveEndTime: String = ""
    private var liveStartDateTime: String = ""
    private var liveEndDateTime: String = ""

    private var instantLive: Boolean = false
    private var liveId: Int = 0
    private var liveType: LiveType = LiveType.ALL

    private var isLoading = false
    private val visibleThreshold = 6

    private var isReplayList: Boolean = false

    var userId = SessionManager.channelId

    companion object {
        fun newInstance(): LiveScheduleListFragment = LiveScheduleListFragment()
        val tag: String = LiveScheduleListFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding?.root ?: FragmentLiveScheduleListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //TODO make it Dynamic
        //userId = 328702
        //SessionManager.channelId = 0
        customerExistsCheck()

        liveType = LiveType.REPLAY

        findNavController().currentDestination?.label = "আমার লাইভ"
        (activity as LiveHomeActivity).updateToolbarTitle("আমার লাইভ")

        val dataAdapter = LiveScheduleListAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.onShareClicked = { model, position ->
            /*val bundle = bundleOf(
                "model" to model
            )
            findNavController().navigate(R.id.nav_live_share, bundle)*/
          /*  val dialog1 = LiveShareFragment.newInstance(model, false)
            dialog1.show(childFragmentManager, LiveShareFragment.tag)*/
        }
        dataAdapter.onProductAddClicked = { model, position ->

            if (model.liveStatus == "upcoming" || model.liveStatus == "replay") {

                val tag = LiveScheduleProductAddBottomSheet.tag
                val dialog = LiveScheduleProductAddBottomSheet.newInstance()
                dialog.show(childFragmentManager, tag)
                dialog.onActionClicked = { flag ->
                    dialog.dismiss()
                    when (flag) {
                        1 -> {
                            // GOTO product add
                            val bundle = bundleOf(
                                "liveId" to model.id,
                                "suggestedPrice" to model.suggestedPrice
                            )
                            findNavController().navigate(R.id.nav_live_product_add, bundle)
                        }
                        2 -> {
                            // GOTO product add from previous
                            val bundle = bundleOf(
                                "liveId" to model.id
                            )
                            //Todo: Remove Cmnt 3
                            //findNavController().navigate(R.id.nav_product_lists, bundle)
                        }
                        3 -> {
                            // GOTO product add from Deal Management
                            val bundle = bundleOf(
                                "liveId" to model.id
                            )
                            findNavController().navigate(R.id.nav_live_deal_management, bundle)
                        }
                    }

                }

            } else if (model.liveStatus == "live") {
                // GOTO to direct upload
                quickUpload(model)
            }
        }
        dataAdapter.onLiveStartClicked = { model, position ->
            checkScheduleValidity(model)
        }
        dataAdapter.onProductDetailsClicked = { model ->
            if ((model.totalOrderCount>0) && (model.liveStatus == "replay")) {
                val bundle = bundleOf(
                    "liveId" to model.id
                )
                //Todo: Remove Cmnt 5
                // findNavController().navigate(R.id.nav_live_order_list, bundle)
            } else {
                context?.toast("কোনো অর্ডার পাওয়া যায়নি")
            }
        }
        dataAdapter.onClick = { model, position ->
            liveSchedule = model
            val tag = LiveScheduleOptionBottomSheet.tag
            val dialog = LiveScheduleOptionBottomSheet.newInstance()
            dialog.show(childFragmentManager, tag)
            dialog.onActionClicked = { flag ->
                dialog.dismiss()
                when(flag) {
                    // Product list
                    3 -> {
                        //goToLiveStream()
                        goToLiveProduct(model)
                    }
                    // Go Live
                    1 -> {
                        checkScheduleValidity(model)
                    }
                    // Quick product insert
                    2 -> {
                        goToProductInsert(model)
                    }
                    // Live product insert
                    4 -> {
                        val bundle = bundleOf(
                            "liveId" to model.id,
                            "suggestedPrice" to model.suggestedPrice
                        )
                        //findNavController().navigate(R.id.nav_live_product_add, bundle)
                    }
                    // Share
                    5 -> {
                        /*val bundle = bundleOf(
                            "model" to model
                        )*/
                        //findNavController().navigate(R.id.nav_live_share, bundle)
                    }
                }
            }
        }


        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            if (state.isInitLoad) {
                Timber.d("PagingDebug pagingState list with true ${state.dataList.size}")
                dataAdapter.initLoad(state.dataList)
                dataAdapter.filter(liveType)
                if (instantLive && liveId > 0) {
                    val index = state.dataList.indexOfFirst { it.id == liveId }
                    if (index != -1) {
                        val model = state.dataList[index]
                        checkScheduleValidity(model)
                    }
                }
                if (state.dataList.isEmpty()) {
                    binding?.emptyView?.visibility = View.VISIBLE
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }
            } else {
                Timber.d("PagingDebug pagingState list with false ${state.dataList.size}")
                if (state.dataList.isEmpty()) {
                    isLoading = true
                } else {
                    dataAdapter.lazyLoadWithFilter(state.dataList, liveType)
                }
                Timber.d("dataAdapter.lazyLoad called")
            }
        })

        //viewModel.fetchUserSchedule(userId, "customer", 0, 20)

        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    /*val lastVisibleItemPositions = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    val lastVisibleItem = when {
                        lastVisibleItemPositions.last() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.last()
                        }
                        lastVisibleItemPositions.first() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.first()
                        }
                        else -> {
                            0
                        }
                    }*/

                    //Timber.d("onScrolled: \nItemCount: $currentItemCount  <= lastVisible: $lastVisibleItem ${!isLoading}")
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold) {
                        isLoading = true

                        if (userId != 0) {
                            if (liveType == LiveType.REPLAY) {
                                viewModel.fetchUserScheduleReplay(userId, "customer", currentItemCount, 20)
                            } else {
                                viewModel.fetchUserSchedule(userId, "customer", currentItemCount, 20)
                            }
                        }

                    }
                }
            }
        })

        fetchBanner()

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab?) {
                var isEmpty = false


                when (tab?.position) {
                    //Changed (0 -> 3) (3 -> 0)
                    3 -> {
                        liveType = LiveType.ALL
                        if (isReplayList) {
                            isReplayList = false
                            //viewModel.fetchUserSchedule(userId, "customer", 0, 20)
                        } else {
                            isEmpty = dataAdapter.filter(liveType)
                        }
                    }
                    1 -> {
                        liveType = LiveType.LIVE
                        if (isReplayList) {
                            isReplayList = false
                            viewModel.fetchUserSchedule(userId, "customer", 0, 20)
                        } else {
                            isEmpty = dataAdapter.filter(liveType)
                        }
                    }
                    2 -> {
                        liveType = LiveType.UPCOMING
                        if (isReplayList) {
                            isReplayList = false
                            viewModel.fetchUserSchedule(userId, "customer", 0, 20)
                        } else {
                            isEmpty = dataAdapter.filter(liveType)
                        }
                    }
                    0 -> {
                        liveType = LiveType.REPLAY
                        isEmpty = false
                        if (userId != 0) {
                            viewModel.fetchUserScheduleReplay(userId, "customer", 0, 20)
                        }
                        isReplayList = true
                    }
                }

                if (isEmpty) {
                    binding?.emptyView?.visibility = View.VISIBLE
                    if (tab?.position == 0 || tab?.position == 2) {
                        binding?.emptyView?.text = getString(R.string.empty_msg1)
                    } else {
                        binding?.emptyView?.text = getString(R.string.empty_msg)
                    }
                } else {
                    binding?.emptyView?.visibility = View.GONE
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    binding?.swipeRefreshLayout?.isRefreshing = state.isShow
                }
            }
        })

        binding?.liveScheduleLayout?.setOnClickListener {
            //ToDo: remove after test
            //liveSchedule = MyLiveSchedule(784, suggestedPrice = "100,200")
            //goToLiveStream()
            //quickUpload(liveSchedule)
            //return@setOnClickListener
            Intent(requireContext(), LiveScheduleActivity::class.java).also {
                //startActivityForResult(it,2)
                scheduleRequest.launch(it)
            }
        }

        binding?.instantLiveLayout?.setOnClickListener {
            Intent(requireContext(), LiveScheduleActivity::class.java).apply {
                putExtra("instantLive", true)
            }.also {
                //startActivityForResult(it,2)
                scheduleRequest.launch(it)
            }
        }

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            if (liveType == LiveType.REPLAY) {
                if (userId != 0) {
                    viewModel.fetchUserScheduleReplay(userId, "customer", 0, 20)
                }
            } else {
                viewModel.fetchUserSchedule(userId, "customer", 0, 20)
            }
        }

    }

    private val scheduleRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            instantLive = result.data?.getBooleanExtra("instantLive", false) ?: false
            liveId = result.data?.getIntExtra("liveId", 0) ?: 0
            viewModel.fetchUserSchedule(userId, "customer", 0, 20)
            Timber.d("scheduleRequest $instantLive $liveId")
        }
    }

    private fun checkScheduleValidity(model: MyLiveSchedule) {
        liveSchedule = model
        try {
            liveDate = model.liveDate?.split("T")?.first() ?: ""
            liveStartTime = model.fromTime ?: ""
            liveEndTime = model.toTime ?: ""
            liveStartDateTime = "$liveDate $liveStartTime" // 2020-12-04 10:00:00
            liveEndDateTime = "$liveDate $liveEndTime"

            Timber.d("LiveStartCheck 1 $liveDate, $liveStartTime, $liveEndTime, $liveStartDateTime, $liveEndDateTime")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val liveStartStamp: Date? = sdf.parse(liveStartDateTime)
            val liveEndStamp: Date? = sdf.parse(liveEndDateTime)
            val currentTimeStamp: Date = Date()

            Timber.d("LiveStartCheck 2 $liveStartStamp, $liveEndStamp")

            if (currentTimeStamp.before(liveStartStamp) || currentTimeStamp.after(liveEndStamp)) {
                alert("নির্দেশনা", "আপনার লাইভ শিডিউল ${DigitConverter.relativeWeekday(liveStartStamp!!)}").show()
            } else {
                goToLiveStream()
            }

        } catch (e: Exception) {
            context?.toast("কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন")
        }
    }

    private fun isPermissions(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            val cameraPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            val storagePermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val permission3 = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)

            return if (cameraPermission != PackageManager.PERMISSION_GRANTED || storagePermission != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED) {

                val cameraPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)
                val storagePermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                val audioPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.RECORD_AUDIO)
                if (cameraPermissionRationale || storagePermissionRationale || audioPermissionRationale) {
                    requestPermissions(permissions, requestCodeCamera)
                } else {
                    requestPermissions( permissions, requestCodeCamera)
                }
                false
            } else {
                true
            }
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCamera) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    goToLiveStream()
                } else {
                    alert("Permission Required", "App required Camera, Audio & Storage permission to function properly. Please grand permission.", true, "Give Permission", "Cancel") {
                        if (it == AlertDialog.BUTTON_POSITIVE) {
                            requestPermissions(permissions, requestCodeCamera)
                        }
                    }.show()
                }
            }
        }
    }

    private fun goToLiveStream() {
        Timber.d("PermissionCheck Function")
        if (isPermissions()) {
            Timber.d("PermissionCheck Granted")
            UserLogger.logGenie("LiveList_Live")
        /*    Intent(requireContext(), LiveActivity::class.java).apply {
                putExtra("liveId", liveSchedule.id)
                putExtra("rtmpUrl", if (liveSchedule.liveChannelUrl.input.isNullOrEmpty()) "" else liveSchedule.liveChannelUrl.input )
                putExtra("liveTitle", liveSchedule.liveTitle)
                putExtra("channelId", liveSchedule.liveChannelUrl.id)
                putExtra("liveDate", liveDate)
                putExtra("liveStartTime", liveStartTime)
                putExtra("liveEndTime", liveEndTime)
                putExtra("liveEndDateTime", liveEndDateTime)
                putExtra("facebook", liveSchedule.facebook)
                putExtra("facebookUrl", liveSchedule.facebookUrl)
                putExtra("facebookStream", liveSchedule.facebookStream)
                putExtra("youtube", liveSchedule.youtube)
                putExtra("youtubeUrl", liveSchedule.youtubeUrl)
                putExtra("youtubeStream", liveSchedule.youtubeStream)
                putExtra("instantLive", instantLive)

            }.also {
                startActivity(it)
                instantLive = false
            }*/
        }
    }

    private fun goToProductInsert(model: MyLiveSchedule) {

        //TODO fix
        /*val bundle = bundleOf(
            "liveId" to model.id,
            "suggestedPrice" to model.suggestedPrice
        )
        findNavController().navigate(R.id.nav_live_product_add, bundle)

        Intent(requireContext(), QuickLiveProductInsertActivity::class.java).apply {
            putExtra("liveId", model.id)
            putExtra("suggestedPrice", model.suggestedPrice)
        }.also {
            startActivity(it)
        }*/
    }

    private fun goToLiveProduct(model: MyLiveSchedule) {
        val bundle = bundleOf(
            "liveId" to model.id
        )
        //Todo: Remove Cmnt 4
        //findNavController().navigate(R.id.nav_live_schedule_product_list, bundle)

    }

    private fun quickUpload(model: MyLiveSchedule) {

        /*Intent(requireContext(), QuickLiveProductInsertActivity::class.java).apply {
            putExtra("liveId", model.id)
            putExtra("suggestedPrice", model.suggestedPrice)
        }.also {
            startActivity(it)
        }*/
    }

    private fun fetchBanner() {
        binding?.bannerImage?.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        val handler = Handler(Looper.getMainLooper())


        handler.postDelayed({
            //viewModel.fetchUserSchedule(userId, "customer", 0, 20)
            if (userId != 0) {
                viewModel.fetchUserScheduleReplay(userId, "customer", 0, 20)
            }
        }, 1000)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun customerExistsCheck() {
        //TODO make it dynamic
        var customerMobile = SessionManager.mobile
        //customerMobile = "01676100969"

        Timber.d("requestBody 1 ${SessionManager.channelId}")
        if (SessionManager.channelId == 0) {
            val requestBody = AuthRequestBody("", customerMobile)
            viewModel.customerAuthenticationCheck(requestBody).observe(viewLifecycleOwner, Observer {
                userId = SessionManager.channelId
                if (userId != 0) {
                    viewModel.fetchUserScheduleReplay(userId, "customer", 0, 20)
                }
            })
        }

    }

}