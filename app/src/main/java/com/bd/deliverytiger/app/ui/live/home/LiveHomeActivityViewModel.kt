package com.bd.deliverytiger.app.ui.live.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bd.deliverytiger.app.api.model.PagingModel
import com.bd.deliverytiger.app.api.model.live.brand.BrandData
import com.bd.deliverytiger.app.api.model.live.brand.BrandRequest
import com.bd.deliverytiger.app.api.model.live.catalog.CatalogData
import com.bd.deliverytiger.app.api.model.live.catalog.CatalogRequest
import com.bd.deliverytiger.app.api.model.live.category.CategoryData
import com.bd.deliverytiger.app.api.model.live.category.CategorySelectionData
import com.bd.deliverytiger.app.api.model.live.products.ProductData
import com.bd.deliverytiger.app.repository.AppRepository
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.ViewState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class LiveHomeActivityViewModel(private val repository: AppRepository) : ViewModel() {

    val viewState = MutableLiveData<ViewState>(ViewState.NONE)

    private val _categoryParentList: MutableLiveData<List<CategoryData>> = MutableLiveData()
    val categoryParentList: LiveData<List<CategoryData>> get() = _categoryParentList
    private val _categoryChildList: MutableLiveData<List<CategoryData>> = MutableLiveData()
    val categoryChildList: LiveData<List<CategoryData>> get() = _categoryChildList
    private var lastIndexCategory: Int = -1

    val createdProduct: MutableLiveData<ProductData?> = MutableLiveData()

    private val _catalogList: MutableLiveData<List<CatalogData>> = MutableLiveData()
    private val dataListCatalog: MutableList<CatalogData> = mutableListOf()
    val catalogList: LiveData<List<CatalogData>> get() = _catalogList
    private var lastIndex1: Int = -1

    private val _brandList: MutableLiveData<List<BrandData>> = MutableLiveData()
    private val dataListBrand: MutableList<BrandData> = mutableListOf()
    val brandList: LiveData<List<BrandData>> get() = _brandList
    private var lastIndexBrand: Int = -1


    private val _pagingState: MutableLiveData<PagingModel<List<ProductData>>> = MutableLiveData()
    val pagingState: LiveData<PagingModel<List<ProductData>>>
        get() = _pagingState

    val categorySelected : MutableLiveData<CategorySelectionData> = MutableLiveData(CategorySelectionData())

    fun getProductList(categoryId: Int, subCategoryId: Int, subSubCategoryId: Int, routingName: String, index: Int = 0, reset: Boolean = false) {

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.getProductList(categoryId, subCategoryId, subSubCategoryId, routingName, index, 20)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val productList = response.body.data!!.productList
                        val totalCount = response.body.data!!.totalCount
                        //Timber.d("productList: $productList")
                        if (index == 0) {
                            _pagingState.value = PagingModel(true, totalCount, 0,0.0, 0.0, productList)
                        } else {
                            _pagingState.value = PagingModel(false, totalCount, 0,0.0, 0.0, productList)
                        }

                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }


    }

    fun getAllCategory(categoryId: Int = 0) {

        if (categoryId == 0) {
            if (categoryId != lastIndexCategory) {
                lastIndexCategory = categoryId
            } else {
                return
            }
        }

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.getAllCategory(categoryId, 0)
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        if (categoryId == 0) {
                            _categoryParentList.value = response.body.data!!
                        } else {
                            _categoryChildList.value = response.body.data!!
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
    }

    fun getCatalogList(index: Int): LiveData<List<CatalogData>> {

        val responseData: MutableLiveData<List<CatalogData>> = MutableLiveData()
        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {

            val response = repository.getVideoList(CatalogRequest(SessionManager.courierUserId.toInt(), index))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val dataList = response.body.data
                        if (dataList != null) {
                            responseData.value = dataList!!
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
        return responseData
    }

    fun fetchBrandList(index: Int = 0, searchKey: String = "", clear: Boolean = false) {

        /*if (clear) {
            lastIndexBrand = -1
        }
        if (index > lastIndexBrand) {
            lastIndexBrand = index
        } else {
            return
        }*/

        viewState.value = ViewState.ProgressState(true)
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository.getBrandListWithSearch(BrandRequest(searchKey, index))
            withContext(Dispatchers.Main) {
                viewState.value = ViewState.ProgressState(false)
                when (response) {
                    is NetworkResponse.Success -> {
                        val dataList = response.body.data
                        if (!dataList.isNullOrEmpty()) {
                            if (index == 0) {
                                dataListBrand.clear()
                            }
                            dataListBrand.addAll(dataList)
                            _brandList.value = dataListBrand
                            Timber.d("fetchBrandList ${dataList.size}")
                        }
                    }
                    is NetworkResponse.ServerError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আমাদের সার্ভার কানেকশনে সমস্যা হচ্ছে, কিছুক্ষণ পর আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.NetworkError -> {
                        val message = "দুঃখিত, এই মুহূর্তে আপনার ইন্টারনেট কানেকশনে সমস্যা হচ্ছে"
                        viewState.value = ViewState.ShowMessage(message)
                    }
                    is NetworkResponse.UnknownError -> {
                        val message = "কোথাও কোনো সমস্যা হচ্ছে, আবার চেষ্টা করুন"
                        viewState.value = ViewState.ShowMessage(message)
                        Timber.d(response.error)
                    }
                }
            }
        }
    }



}