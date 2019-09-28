package com.bd.deliverytiger.app.ui.cod_collection


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.CODCollectionInterface
import com.bd.deliverytiger.app.api.`interface`.DistrictInterface
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.Timber

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
    private lateinit var codCollectionInterface: CODCollectionInterface
    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_codcollection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("COD Collection")
        rvCODCollection = view.findViewById(R.id.rvCODCollection)

        codCollectionInterface = RetrofitSingleton.getInstance(context!!).create(CODCollectionInterface::class.java)

        linearLayoutManager = LinearLayoutManager(context)
        codCollectionAdapter = CODCollectionAdapter(context!!)
        rvCODCollection.apply {
            layoutManager = linearLayoutManager
            adapter = codCollectionAdapter
        }

        rvCODCollection.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutPosition = linearLayoutManager.findLastVisibleItemPosition()
                Timber.e("layoutPosition",layoutPosition.toString()+" "+totalLoadedData+" "+isLoading+" "+totalCount)
                if(layoutPosition >= totalLoadedData && !isLoading && layoutPosition < totalCount) {
                    getAllCODCollection(0,0)
                    Timber.e("layoutPosition loadMoreCalled ",layoutPosition.toString()+" "+totalLoadedData+" "+isLoading+" "+totalCount)
                }
            }
        })

    }


    private fun getAllCODCollection(index: Int,count: Int){
        //codCollectionInterface
    }


}
