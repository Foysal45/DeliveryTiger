package com.bd.deliverytiger.app.ui.notification


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentNotificationBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class NotificationFragment : Fragment() {

    private var binding: FragmentNotificationBinding? = null
    private val viewModel: NotificationViewModel by inject()

    private val dataAdapter = NotificationAdapter()

    companion object{
        fun newInstance(): NotificationFragment = NotificationFragment().apply {  }
        val tag: String = NotificationFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentNotificationBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.recyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
                adapter = dataAdapter
            }
        }

        dataAdapter.onItemClicked = { model, _ ->
            activity?.let {
                (it as HomeActivity).closeDrawer()
            }

            val bundle = bundleOf(
                "fcmData" to model
            )
            val fragment = NotificationPreviewFragment.newInstance()
            fragment.arguments = bundle
            val tag = NotificationPreviewFragment.fragmentTag
            addFragment(fragment, tag)
        }

        dataAdapter.onItemDelete = { model, position ->
            dataAdapter.removeByIndex(position)
            viewModel.deleteNotificationById(model.uid)
        }

        binding?.deleteAllBtn?.setOnClickListener {
            dataAdapter.clear()
            viewModel.deleteAllNotification()
            binding?.emptyView?.isVisible = dataAdapter.itemCount == 0
        }

        binding?.swipeRefresh?.setOnRefreshListener {
            fetchNotificationData()
        }

        fetchNotificationData()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    //binding?.progressBar?.isVisible = state.isShow
                    binding?.swipeRefresh?.isRefreshing = state.isShow
                }
            }
        })

    }

    private fun fetchNotificationData() {

        viewModel.notificationList.observe(viewLifecycleOwner, Observer { list ->
            binding?.swipeRefresh?.isRefreshing = false
            dataAdapter.initLoad(list)
            binding?.emptyView?.isVisible = list.isEmpty()
        })

        /*viewModel.getAllNotification().observe(viewLifecycleOwner, Observer { list ->
            binding?.swipeRefresh?.isRefreshing = false
            dataAdapter.initLoad(list)
            binding?.emptyView?.isVisible = list.isEmpty()
        })*/
    }

    private fun addFragment(fragment: Fragment, tag: String) {
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commitAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}
