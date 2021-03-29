package com.bd.deliverytiger.app.ui.delivery_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.delivery_return_count.DeliveryDetailsRequest
import com.bd.deliverytiger.app.databinding.FragmentDeliveryDetailsBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import org.koin.android.ext.android.inject

class DeliveryDetailsFragment : Fragment() {

    private var binding : FragmentDeliveryDetailsBinding? = null
    private  var dataAdapter: DeliveryDetailsAdapter = DeliveryDetailsAdapter()
    private val viewModel: DeliveryDetailsViewModel by inject()

    private lateinit var dataRequestBody: DeliveryDetailsRequest
    var title = ""

    companion object{
        fun newInstance(dataRequestBody: DeliveryDetailsRequest): DeliveryDetailsFragment = DeliveryDetailsFragment().apply {
            this.dataRequestBody = dataRequestBody
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

        title = if (dataRequestBody.type == "return"){
            "রিটার্ন"
        }else{
            "ডেলিভারি"
        }

        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }
        fetchDetailsData()
    }

    private fun fetchDetailsData() {

        viewModel.fetchAllDataList(dataRequestBody).observe(viewLifecycleOwner, Observer {  list->
            dataAdapter.initLoad(list)
            if (list.isNullOrEmpty()){
                binding?.emptyView?.visibility = View.VISIBLE
            }else{
                binding?.emptyView?.visibility = View.GONE
            }

        })
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle(title)
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}