package com.bd.deliverytiger.app.ui.cod_collection


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bd.deliverytiger.app.R

/**
 * A simple [Fragment] subclass.
 */
class CODCollectionFragment : Fragment() {

    companion object{
        fun newInstance(): CODCollectionFragment {
            val fragment = CODCollectionFragment()
            return fragment
        }
        val tag = CODCollectionFragment::class.java.name
    }

    private lateinit var rvCODCollection: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var codCollectionAdapter: CODCollectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codcollection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvCODCollection = view.findViewById(R.id.rvCODCollection)

        linearLayoutManager = LinearLayoutManager(context)
        codCollectionAdapter = CODCollectionAdapter(context!!)
        rvCODCollection.apply {
            layoutManager = linearLayoutManager
            adapter = codCollectionAdapter
        }
    }


}
