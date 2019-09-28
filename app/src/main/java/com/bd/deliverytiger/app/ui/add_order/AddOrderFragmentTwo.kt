package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.charge.WeightRangeWiseData
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.BundleFlag
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.Timber
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class AddOrderFragmentTwo : Fragment() {

    private lateinit var productNameET: EditText
    private lateinit var deliveryOnly: TextView
    private lateinit var deliveryCollection: TextView
    private lateinit var collectionAmountET: EditText
    private lateinit var spinnerWeight: AppCompatSpinner
    private lateinit var spinnerPackaging: AppCompatSpinner
    private lateinit var checkBoxBreakable: AppCompatCheckBox
    private lateinit var collectionAddressET: EditText
    private lateinit var checkTerms: AppCompatCheckBox
    private lateinit var deliveryTypeRV: RecyclerView

    private lateinit var placeOrderInterface: PlaceOrderInterface
    private lateinit var deliveryTypeAdapter: DeliveryTypeAdapter

    // API variable
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private val deliveryTypeList: MutableList<WeightRangeWiseData> = mutableListOf()
    private var breakableCharge: Int = 0
    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var packagingCharge: Double = 0.0
    private var deliveryCharge: Double = 0.0
    private lateinit var dataBundle :Bundle

    companion object {
        fun newInstance(dataBundle: Bundle): AddOrderFragmentTwo = AddOrderFragmentTwo().apply {
            this.dataBundle = dataBundle // previous fragments data
            // bundle getting flag are available in BundleFlag class
        }
        val tag = AddOrderFragmentTwo::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_two, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("প্যাকেজ ও কালেকশনের তথ্য")

        productNameET = view.findViewById(R.id.productName)
        deliveryOnly = view.findViewById(R.id.delivery_only_tv)
        deliveryCollection = view.findViewById(R.id.delivery_collection_tv)
        collectionAmountET = view.findViewById(R.id.collectionAmount)
        spinnerWeight = view.findViewById(R.id.spinner_weight_selection)
        spinnerPackaging = view.findViewById(R.id.spinner_packaging_selection)
        checkBoxBreakable = view.findViewById(R.id.check_breakable)
        collectionAddressET = view.findViewById(R.id.collectionAddress)
        checkTerms = view.findViewById(R.id.check_terms_condition)
        deliveryTypeRV = view.findViewById(R.id.delivery_type_selection_rV)


        placeOrderInterface = RetrofitSingleton.getInstance(context!!).create(PlaceOrderInterface::class.java)
        getBreakableCharge()
        getPackagingCharge()
        getDeliveryCharge()

        deliveryTypeAdapter = DeliveryTypeAdapter(context!!, deliveryTypeList)
        with(deliveryTypeRV){
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
            adapter = deliveryTypeAdapter
        }
        deliveryTypeAdapter.onItemClick = { position, model ->

            deliveryCharge = model.chargeAmount
        }

        deliveryCollection.setOnClickListener {
            collectionAmountET.visibility = View.VISIBLE
            deliveryCollection.background = ContextCompat.getDrawable(context!!, R.drawable.bg_add_order_edit_text)
            deliveryOnly.background = ContextCompat.getDrawable(context!!, R.drawable.bg_stroke_gray_corner)
        }

        deliveryOnly.setOnClickListener {
            collectionAmountET.visibility = View.GONE
            deliveryOnly.background = ContextCompat.getDrawable(context!!, R.drawable.bg_add_order_edit_text)
            deliveryCollection.background = ContextCompat.getDrawable(context!!, R.drawable.bg_stroke_gray_corner)

        }

    }

    private fun getBreakableCharge() {

        placeOrderInterface.getBreakableCharge().enqueue(object :
            Callback<GenericResponse<BreakableChargeData>> {
            override fun onFailure(call: Call<GenericResponse<BreakableChargeData>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<GenericResponse<BreakableChargeData>>,
                response: Response<GenericResponse<BreakableChargeData>>
            ) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model
                        breakableCharge = model.breakableCharge
                        codChargePercentage = model.codChargePercentage
                        codChargeMin = model.codChargeMin
                    }
                }
            }

        })

    }

    private fun getPackagingCharge() {

        placeOrderInterface.getPackagingCharge().enqueue(object : Callback<GenericResponse<List<PackagingData>>> {
            override fun onFailure(call: Call<GenericResponse<List<PackagingData>>>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<GenericResponse<List<PackagingData>>>,
                response: Response<GenericResponse<List<PackagingData>>>
            ) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model
                        packagingDataList.clear()
                        packagingDataList.addAll(model)

                        val packageNameList: MutableList<String> = mutableListOf()
                        for (model1 in packagingDataList){
                            packageNameList.add(model1.packagingName)
                        }

                        val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, packageNameList)
                        spinnerPackaging.adapter = packagingAdapter
                        spinnerPackaging.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                val model2 = packagingDataList[p2]
                                packagingCharge = model2.packagingCharge
                            }

                        }
                    }
                }
            }
        })

    }

    private fun getDeliveryCharge() {

        placeOrderInterface.getDeliveryCharge(DeliveryChargeRequest(14,10026)).enqueue(object : Callback<GenericResponse<List<DeliveryChargeResponse>>> {
            override fun onFailure(
                call: Call<GenericResponse<List<DeliveryChargeResponse>>>,
                t: Throwable
            ) {

            }

            override fun onResponse(
                call: Call<GenericResponse<List<DeliveryChargeResponse>>>,
                response: Response<GenericResponse<List<DeliveryChargeResponse>>>
            ) {
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model

                        val weightList: MutableList<String> = mutableListOf()
                        for (model1 in model){
                            weightList.add(model1.weight)
                        }

                        val weightAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, weightList)
                        spinnerWeight.adapter = weightAdapter
                        spinnerWeight.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(
                                p0: AdapterView<*>?,
                                p1: View?,
                                p2: Int,
                                p3: Long
                            ) {
                                val model2 = model[p2]

                                deliveryTypeList.clear()
                                deliveryTypeList.addAll(model2.weightRangeWiseData)
                                deliveryTypeAdapter.notifyDataSetChanged()

                            }
                        }
                    }
                }
            }

        })
    }

    /*if (response.isSuccessful && response.body() != null && isAdded) {
        if (response.body()!!.model != null) {
            val model = response.body()!!.model
        }
    }*/

}
