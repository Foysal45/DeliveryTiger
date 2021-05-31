package com.bd.deliverytiger.app.ui.live.product_gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.api.model.live.live_product_insert.ProductGalleryData
import com.bd.deliverytiger.app.api.model.live.my_products_lists.MyProductsRequest
import com.bd.deliverytiger.app.databinding.FragmentAllProductListBinding
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.bd.deliverytiger.app.utils.hideKeyboard
import com.bd.deliverytiger.app.utils.toast
import org.koin.android.ext.android.inject

class AllProductListFragment : Fragment() {
    private var binding: FragmentAllProductListBinding? = null
    private val viewModel: AllProductListViewModel by inject()
    private  var dataAdapter: AllProductListAdapter = AllProductListAdapter()

    // Paging params
    private var isLoading = false
    private var totalProduct = 0
    private val visibleThreshold = 5

    private var liveId: Int = 0

    companion object {
        @JvmStatic
        fun newInstance(): AllProductListFragment = AllProductListFragment()

        @JvmField
        val tag: String = AllProductListFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return FragmentAllProductListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        liveId = arguments?.getInt("liveId", 0) ?: 0

        binding?.recyclerview?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                adapter = dataAdapter
                //addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state ->
            isLoading = false
            if (state.isInitLoad) {
                dataAdapter.initLoad(state.dataList)
                totalProduct = state.totalCount
                if (state.dataList.isNotEmpty()) {
                    binding?.emptyView?.isVisible = false
                } else {
                    binding?.emptyView?.isVisible = true
                }
            } else {
                dataAdapter.pagingLoad(state.dataList)
                if (state.dataList.isEmpty()) {
                    isLoading = true
                }
            }

        })

        fetchProductsList(0)

        dataAdapter.onItemClicked = { model, position ->
            if (liveId > 0) {
                dataAdapter.toggleSelection(model, position)
            }
        }

        if (liveId > 0) {
            binding?.uploadBtn?.isVisible = true
        } else {
            binding?.uploadBtn?.isVisible = false
        }
        binding?.uploadBtn?.setOnClickListener {
            insertProduct()
        }

        binding?.recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val lastVisibleItem = (recyclerView.layoutManager as GridLayoutManager).findLastVisibleItemPosition()
                    //Timber.d("onScrolled: \nItemCount: $currentItemCount  <= lastVisible: $lastVisibleItem ${!isLoading}")
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold) {
                        isLoading = true
                        fetchProductsList(currentItemCount)
                    }
                }
            }
        })

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

    private fun fetchProductsList(index: Int){
        var userId = SessionManager.courierUserId
        val requestBody = MyProductsRequest(index,20, userId)
        viewModel.fetchLiveProducts(requestBody)
    }

    private fun insertProduct() {

        val selectedProductList = dataAdapter.getSeletedItemModelList()
        if (selectedProductList.isEmpty()) {
            context?.toast("প্রোডাক্ট সিলেক্ট করুন")
            return
        }

        binding?.progressBar1?.isVisible = true
        val requestBody: MutableList<ProductGalleryData> = mutableListOf()
        selectedProductList.forEach { model ->
            requestBody.add(ProductGalleryData(liveId, model.Id))
        }
        dataAdapter.clearSelections()
        viewModel.insertProduct(requestBody).observe(viewLifecycleOwner, Observer { flag ->
            if (flag) {
                binding?.progressBar1?.isVisible = false
                context?.toast("প্রোডাক্ট অ্যাড হয়েছে")
            }
        })
    }


    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}