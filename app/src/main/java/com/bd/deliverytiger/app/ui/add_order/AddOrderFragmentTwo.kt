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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.BreakableChargeData
import com.bd.deliverytiger.app.api.model.packaging.PackagingData
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
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

    // API variable
    private val packagingDataList: MutableList<PackagingData> = mutableListOf()
    private var breakableCharge: Int = 0
    private var codChargePercentage: Double = 0.0
    private var codChargeMin: Int = 0
    private var packagingCharge: Double = 0.0

    companion object {
        fun newInstance(): AddOrderFragmentTwo = AddOrderFragmentTwo().apply {
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

    /*if (response.isSuccessful && response.body() != null && isAdded) {
        if (response.body()!!.model != null) {
            val model = response.body()!!.model
        }
    }*/

}
