package com.bd.deliverytiger.app.ui.collection_history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentCollectionHistoryBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class CollectionHistoryFragment : Fragment() {

    private var binding: FragmentCollectionHistoryBinding? = null
    private val viewModel: CollectionHistoryViewModel by inject()
    private lateinit var dataAdapter: CollectionHistoryAdapter

    companion object {
        fun newInstance(): CollectionHistoryFragment = CollectionHistoryFragment()
        val tag: String = CollectionHistoryFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.collection_history))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCollectionHistoryBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = CollectionHistoryAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        viewModel.fetchCollectionHistory(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            dataAdapter.initLoad(list)
            if (list.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
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
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })

    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}