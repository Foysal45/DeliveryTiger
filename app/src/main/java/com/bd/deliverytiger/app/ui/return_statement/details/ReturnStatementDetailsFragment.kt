package com.bd.deliverytiger.app.ui.return_statement.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ReturnStatementDetailsFragment(): Fragment() {

    companion object {
        fun newInstance(): ReturnStatementDetailsFragment = ReturnStatementDetailsFragment().apply {  }
        val tag: String = ReturnStatementDetailsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

}