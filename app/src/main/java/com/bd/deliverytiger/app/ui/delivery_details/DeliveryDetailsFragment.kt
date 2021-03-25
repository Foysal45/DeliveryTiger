package com.bd.deliverytiger.app.ui.delivery_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.databinding.FragmentDeliveryDetailsBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import org.koin.android.ext.android.inject

class DeliveryDetailsFragment : Fragment() {

    private var binding : FragmentDeliveryDetailsBinding? = null
    private  var dataAdapter: DeliveryDetailsAdapter = DeliveryDetailsAdapter()
    private val viewModel: DeliveryDetailsViewModel by inject()

    companion object{
        fun newInstance(): DeliveryDetailsFragment = DeliveryDetailsFragment().apply {  }
        val tag: String = DeliveryDetailsFragment::class.java.name
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentDeliveryDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
        }
        fetchComplain()
    }

    private fun fetchComplain() {

        viewModel.fetchAllDataList(SessionManager.courierUserId, 0).observe(viewLifecycleOwner, Observer {  list->
            dataAdapter.initLoad(list)
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("ডেলিভারি")
    }

    override fun onDestroy() {
        binding = null
        super.onDestroy()
    }
}