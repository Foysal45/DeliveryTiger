package com.bd.deliverytiger.app.ui.delivery_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.databinding.FragmentDeliveryDetailsBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class DeliveryDetailsFragment : Fragment() {

    private var binding : FragmentDeliveryDetailsBinding? = null
    private val viewModel: DeliveryDetailsViewModel by inject()

    private  var dataAdapter: DeliveryDetailsAdapter = DeliveryDetailsAdapter()
    private var dataRequestBody: DeliveryDetailsRequest? = null
    private var title = ""
    private var total = 0

    companion object{
        fun newInstance(dataRequestBody: DeliveryDetailsRequest?, totalCount: Int): DeliveryDetailsFragment = DeliveryDetailsFragment().apply {
            this.dataRequestBody = dataRequestBody
            this.total = totalCount
        }
        val tag: String = DeliveryDetailsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDeliveryDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: Bundle? = arguments
        bundle?.let {
            dataRequestBody = it.getParcelable("dataRequestBody")?: DeliveryDetailsRequest()
            total = it.getInt("totalCount")
        }

        title = if (dataRequestBody?.type == "return"){
            "রিটার্নে আছে (${DigitConverter.toBanglaDigit(total)} টি)"
        }else{
            "ডেলিভারি হয়েছে (${DigitConverter.toBanglaDigit(total)} টি)"
        }

        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }

        fetchDetailsData()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    binding?.progressBar?.isVisible = state.isShow
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(title)
    }

    private fun fetchDetailsData() {
        dataRequestBody?.let { request ->
            viewModel.fetchAllDataList(request).observe(viewLifecycleOwner, Observer {  list->
                dataAdapter.initLoad(list)
                binding?.emptyView?.isVisible = list.isNullOrEmpty()
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}