package com.bd.deliverytiger.app.ui.return_statement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentReturnStatementBinding
import com.bd.deliverytiger.app.utils.SessionManager
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.return_statement.details.ReturnStatementDetailsFragment
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import java.util.*


class ReturnStatementFragment(): Fragment() {

    private var binding:FragmentReturnStatementBinding? = null
    private val viewModel: ReturnStatementViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var dataAdapter:ReturnStatementAdapter

    companion object {
        fun newInstance(): ReturnStatementFragment = ReturnStatementFragment().apply {  }
        val tag: String = ReturnStatementFragment::class.java.name
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(getString(R.string.return_statement))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentReturnStatementBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        dataAdapter = ReturnStatementAdapter()
        with(binding?.recyclerview!!){
            setHasFixedSize(true)
            layoutManager =LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }

        dataAdapter.onItemClick = { model ->
            goToReturnDetails(model)
        }

        fetchReturnDataMonthWise()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
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

    private fun fetchReturnDataMonthWise() {
        val courierUserId = SessionManager.courierUserId
        //todo: remove 6229 and add courierUserId
        viewModel.fetchReturnCount(6229, 0, 20 ).observe(viewLifecycleOwner, Observer { list ->
            dataAdapter.initLoad(list)
            if (list.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
            }
        })
    }

    private fun goToReturnDetails(model: ReturnStatementData) {

        val fragment = ReturnStatementDetailsFragment.newInstance(model)
        val tag = ReturnStatementDetailsFragment.tag

        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, tag)
        ft?.addToBackStack(tag)
        ft?.commit()
    }


}