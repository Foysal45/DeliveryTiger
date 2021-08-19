package com.bd.deliverytiger.app.ui.return_statement.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.databinding.FragmentReturnStatementDetailsBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class ReturnStatementDetailsFragment(): Fragment() {

    private var binding: FragmentReturnStatementDetailsBinding? = null

    //private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private lateinit var dataAdapter: ReturnStatementDetailsAdapter

    private var returnStatementData: ReturnStatementData = ReturnStatementData()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  FragmentReturnStatementDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle: Bundle? = arguments
        bundle?.let {
            returnStatementData = it.getParcelable("returnStatementData")?: ReturnStatementData()
        }

        Timber.d("FragmentReturnStatementDetails ${returnStatementData.orders}")
        dataAdapter = ReturnStatementDetailsAdapter()
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }
        dataAdapter.initLoad(returnStatementData.orders)

    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}