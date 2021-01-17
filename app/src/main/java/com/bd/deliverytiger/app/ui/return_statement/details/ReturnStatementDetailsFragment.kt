package com.bd.deliverytiger.app.ui.return_statement.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bd.deliverytiger.app.api.model.return_statement.ReturnStatementData
import com.bd.deliverytiger.app.databinding.FragmentReturnStatementDetailsBinding
import java.text.SimpleDateFormat
import java.util.*

class ReturnStatementDetailsFragment(): Fragment() {

    private var binding: FragmentReturnStatementDetailsBinding? = null

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private lateinit var dataAdapter: ReturnStatementDetailsAdapter

    private var returnStatementData: ReturnStatementData = ReturnStatementData()

    companion object {
        @JvmStatic
        fun newInstance(returnStatementData: ReturnStatementData): ReturnStatementDetailsFragment = ReturnStatementDetailsFragment().apply {
            this.returnStatementData = returnStatementData
        }

        @JvmField
        val tag: String = ReturnStatementDetailsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  FragmentReturnStatementDetailsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = ReturnStatementDetailsAdapter()
        dataAdapter.initLoad(returnStatementData.orders)
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }


    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}