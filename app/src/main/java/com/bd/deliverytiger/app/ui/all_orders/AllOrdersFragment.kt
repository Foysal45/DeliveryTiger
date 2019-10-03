package com.bd.deliverytiger.app.ui.all_orders


import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.AllOrderInterface
import com.bd.deliverytiger.app.api.`interface`.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CODReqBody
import com.bd.deliverytiger.app.api.model.cod_collection.CODResponse
import com.bd.deliverytiger.app.api.model.cod_collection.CourierOrderViewModel
import com.bd.deliverytiger.app.api.model.order.UpdateOrderReqBody
import com.bd.deliverytiger.app.api.model.order.UpdateOrderResponse
import com.bd.deliverytiger.app.ui.filter.FilterFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class AllOrdersFragment : Fragment() {

    companion object {
        fun newInstance(): AllOrdersFragment {
            val fragment = AllOrdersFragment()
            return fragment
        }

        val tag = AllOrdersFragment::class.java.name
    }

    private lateinit var rvAllOrder: RecyclerView
    private lateinit var tvTotalOrder: TextView
    private lateinit var allOrderFilterLay: LinearLayout

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var allOrdersAdapter: AllOrdersAdapter
    private lateinit var allOrderInterface: AllOrderInterface
    private lateinit var ivEmpty: ImageView
    private lateinit var topLay: LinearLayout

    private lateinit var allOrderProgressBar: ProgressBar
    private var isLoading = false
    private var totalLoadedData = 0
    private var layoutPosition = 0
    private var totalCount = 0
    private var courierOrderViewModelList: ArrayList<CourierOrderViewModel?>? = null
    private var fromDate = "01-01-01"
    private var toDate = "01-01-01"
    private var status = -1
    private var isMoreDataAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_orders, container, false)
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("সব অর্ডার")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("সব অর্ডার")
        rvAllOrder = view.findViewById(R.id.rvAllOrder)
        allOrderProgressBar = view.findViewById(R.id.allOrderProgressBar)
        tvTotalOrder = view.findViewById(R.id.tvTotalOrder)
        allOrderFilterLay = view.findViewById(R.id.allOrderFilterLay)
        ivEmpty = view.findViewById(R.id.ivEmpty)
        topLay = view.findViewById(R.id.topLay)

        allOrderInterface =
            RetrofitSingleton.getInstance(context!!).create(AllOrderInterface::class.java)

        courierOrderViewModelList = ArrayList()
        linearLayoutManager = LinearLayoutManager(context)

        allOrdersAdapter = AllOrdersAdapter(context!!, courierOrderViewModelList)
        rvAllOrder.apply {
            layoutManager = linearLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    rvAllOrder.getContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = allOrdersAdapter
        }


        getAllOrders(0, 20)

        rvAllOrder.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                layoutPosition = linearLayoutManager.findLastVisibleItemPosition()
                Timber.e(
                    "layoutPosition",
                    layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount + " " + isMoreDataAvailable
                )
                if (dy > 0) {
                    if (layoutPosition >= (totalLoadedData - 2) && !isLoading && layoutPosition < totalCount && isMoreDataAvailable) {
                        getAllOrders(totalLoadedData, 20)
                        Timber.e(
                            "layoutPosition loadMoreCalled ",
                            layoutPosition.toString() + " " + totalLoadedData + " " + isLoading + " " + totalCount
                        )
                    }
                }
            }
        })

        allOrdersAdapter.onOrderItemClick = { position ->
            addOrderTrackFragment(courierOrderViewModelList!![position]?.courierOrdersId.toString())
        }

        allOrdersAdapter.onEditItemClick = { position ->
            val orderUpdateReqBody = UpdateOrderReqBody(courierOrderViewModelList!![position]?.id,courierOrderViewModelList!![position]?.customerName,courierOrderViewModelList!![position]?.courierAddressContactInfo?.mobile,courierOrderViewModelList!![position]?.courierAddressContactInfo?.otherMobile,courierOrderViewModelList!![position]?.courierAddressContactInfo?.address,courierOrderViewModelList!![position]?.userInfo?.collectAddress,courierOrderViewModelList!![position]?.courierOrderInfo?.collectionName)
            editOrder(courierOrderViewModelList!![position]?.courierOrdersId.toString(),orderUpdateReqBody, position)
        }

        allOrderFilterLay.setOnClickListener {
            goToFilter()
        }
    }

    private fun getAllOrders(index: Int, count: Int) {

        isLoading = true
        allOrderProgressBar.visibility = View.VISIBLE
        val reqModel = CODReqBody(
            status, ArrayList(), fromDate, toDate, SessionManager.courierUserId,
            "", "", "", index, count
        )  // text model

        Timber.e("getAllOrdersReq", reqModel.toString())

        allOrderInterface.getAllOrder(reqModel)
            .enqueue(object : Callback<GenericResponse<CODResponse>> {
                override fun onFailure(
                    call: Call<GenericResponse<CODResponse>>,
                    t: Throwable
                ) {
                    Timber.e("getAllOrdersResponse", " f " + t.toString())
                    if (allOrderProgressBar.visibility == View.VISIBLE) {
                        allOrderProgressBar.visibility = View.GONE
                    }
                }

                override fun onResponse(
                    call: Call<GenericResponse<CODResponse>>,
                    response: Response<GenericResponse<CODResponse>>
                ) {
                    if (allOrderProgressBar.visibility == View.VISIBLE) {
                        allOrderProgressBar.visibility = View.GONE
                    }
                    isLoading = false
                    if (response.isSuccessful && response.body() != null && response.body()!!.model != null) {
                        Timber.e(
                            "getAllOrdersResponse",
                            " s " + response.body()!!.model.courierOrderViewModel
                        )
                        courierOrderViewModelList?.addAll(response.body()!!.model.courierOrderViewModel!!)
                        totalLoadedData = courierOrderViewModelList!!.size

                        allOrdersAdapter.notifyDataSetChanged()
                        isMoreDataAvailable =
                            response.body()!!.model.courierOrderViewModel!!.size >= count - 2
                        Timber.e("getAllOrdersResponse", " s " + response.body().toString())

                        if (index < 20) {
                            totalCount = response.body()!!.model.totalCount!!.toInt()
                            tvTotalOrder.text = "মোট অর্ডার: ${DigitConverter.toBanglaDigit(totalCount)} টি"
                        }

                        if(totalLoadedData == 0){
                            topLay.visibility = View.GONE
                            ivEmpty.visibility = View.VISIBLE
                        } else {
                            topLay.visibility = View.VISIBLE
                            ivEmpty.visibility = View.GONE
                        }

                    } else {
                        Timber.e("getAllOrdersResponse", " s null")
                    }
                }
            })

    }

    private fun addOrderTrackFragment(orderId: String) {
        val fragment = OrderTrackingFragment.newInstance(orderId)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
        ft?.commit()
    }

    private fun goToFilter() {

        activity?.let {
            (activity as HomeActivity).openRightDrawer()
        }

        val fragment = FilterFragment.newInstance(fromDate, toDate, status)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.container_drawer, fragment, FilterFragment.tag)
        // ft?.addToBackStack(FilterFragment.tag)
        ft?.commit()

        fragment.setFilterListener(object : FilterFragment.FilterListener {
            override fun selectedDate(fromDate1: String, toDate1: String, status1: Int) {
                fromDate = fromDate1
                toDate = toDate1
                status = status1

                courierOrderViewModelList?.clear()
                allOrdersAdapter.notifyDataSetChanged()
                getAllOrders(0, 20)

                activity?.onBackPressed()
            }
        })
    }

    private fun editOrder(orderId: String,updateOrderReqBody: UpdateOrderReqBody, indexPosition: Int) {
        val dialogBuilder = AlertDialog.Builder(context)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.custom_order_alert_lay, null)
        dialogBuilder.setView(dialogView)
        val etAlertAddOrderMobileNo: TextView =
            dialogView.findViewById(R.id.etAlertAddOrderMobileNo)
        val etAlertAlternativeMobileNo: TextView =
            dialogView.findViewById(R.id.etAlertAlternativeMobileNo)
        val etAlertCustomersAddress: TextView =
            dialogView.findViewById(R.id.etAlertCustomersAddress)
        val btnAlertSubmit: Button = dialogView.findViewById(R.id.btnAlertSubmit)


        etAlertAddOrderMobileNo.setText(updateOrderReqBody.mobile)
        etAlertAlternativeMobileNo.setText(updateOrderReqBody.otherMobile)
        etAlertCustomersAddress.setText(updateOrderReqBody.address)

        val dialog = dialogBuilder.create()
        dialog.show()

        btnAlertSubmit.setOnClickListener {
             if (etAlertAddOrderMobileNo.text.toString().isEmpty()) {
                VariousTask.showShortToast(context, getString(R.string.write_phone_number))
                etAlertAddOrderMobileNo.requestFocus()
            } else if (!Validator.isValidMobileNumber(etAlertAddOrderMobileNo.text.toString()) || etAlertAddOrderMobileNo.text.toString().length < 11) {
                VariousTask.showShortToast(
                    context,
                    getString(R.string.write_proper_phone_number_recharge)
                )
                etAlertAddOrderMobileNo.requestFocus()
            } else if (etAlertCustomersAddress.text.toString().isEmpty()) {
                VariousTask.showShortToast(context!!, getString(R.string.write_yr_address))
                etAlertCustomersAddress.requestFocus()
            } else {
                 updateOrderReqBody.address = etAlertCustomersAddress.text.toString()
                 updateOrderReqBody.mobile = etAlertAddOrderMobileNo.text.toString()
                 updateOrderReqBody.otherMobile = etAlertAlternativeMobileNo.text.toString()
                 updateOrderApiCall(orderId,updateOrderReqBody,indexPosition)
                dialog.dismiss()
            }
        }


    }

    private fun updateOrderApiCall(orderId: String,updateOrderReqBody: UpdateOrderReqBody, indexPos: Int){
        val placeOrderInterface = RetrofitSingleton.getInstance(context!!).create(PlaceOrderInterface::class.java)
        placeOrderInterface.placeOrderUpdate(orderId,updateOrderReqBody).enqueue(object :Callback<GenericResponse<UpdateOrderResponse>>{
            override fun onFailure(call: Call<GenericResponse<UpdateOrderResponse>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<GenericResponse<UpdateOrderResponse>>,
                response: Response<GenericResponse<UpdateOrderResponse>>
            ) {
               if(response.isSuccessful && response.body() != null){
                   courierOrderViewModelList?.get(indexPos)?.courierAddressContactInfo?.apply {
                       mobile = updateOrderReqBody.mobile
                       otherMobile = updateOrderReqBody.otherMobile
                       address = updateOrderReqBody.address
                   }
                   allOrdersAdapter.notifyItemChanged(indexPos)
                   VariousTask.showShortToast(context,getString(R.string.update_success))
               } else {
                   VariousTask.showShortToast(context,getString(R.string.error_msg))
               }
            }

        })
    }

}
