package com.bd.deliverytiger.app.ui.add_order


import android.app.AlertDialog.Builder
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.DistrictInterface
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.ThanaOrAriaAdapter
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.BundleFlag
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.VariousTask.hideSoftKeyBoard
import com.bd.deliverytiger.app.utils.VariousTask.showShortToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * A simple [Fragment] subclass.
 */
class AddOrderFragmentOne : Fragment(), View.OnClickListener {

    companion object{
        fun newInstance(): AddOrderFragmentOne {
            val fragment = AddOrderFragmentOne()
            return fragment
        }
        val tag = AddOrderFragmentOne::class.java.name
    }


    private lateinit var paymentDetailsLayout: LinearLayout
    private lateinit var etCustomerName: EditText
    private lateinit var etAddOrderMobileNo: EditText
    private lateinit var etAlternativeMobileNo: EditText
    private lateinit var etDistrict: EditText
    private lateinit var etThana: EditText
    private lateinit var etAriaPostOffice: EditText
    private lateinit var etCustomersAddress: EditText
    private lateinit var etAdditionalNote: EditText
    private lateinit var consLayGoNextPage: ConstraintLayout

    private var customerName = ""
    private var mobileNo = ""
    private var alternativeMobileNo = ""
    private var district = 0
    private var thana = 0
    private var ariaPostOffice = 0
    private var customersAddress = ""
    private var additionalNote = ""
    private val districtList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private val thanaOrAriaList: ArrayList<ThanaPayLoad> = ArrayList()
    private var isAriaAvailable = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        paymentDetailsLayout = view.findViewById(R.id.payment_details)
        etCustomerName = view.findViewById(R.id.etCustomerName)
        etAddOrderMobileNo = view.findViewById(R.id.etAddOrderMobileNo)
        etAlternativeMobileNo = view.findViewById(R.id.etAlternativeMobileNo)
        etDistrict = view.findViewById(R.id.etDistrict)
        etThana = view.findViewById(R.id.etThana)
        etAriaPostOffice = view.findViewById(R.id.etAriaPostOffice)
        etCustomersAddress = view.findViewById(R.id.etCustomersAddress)
        etAdditionalNote = view.findViewById(R.id.etAdditionalNote)
        consLayGoNextPage = view.findViewById(R.id.consLayGoNextPage)

        iniViewClicked()
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("নতুন অর্ডার")
    }

    private fun iniViewClicked(){
        consLayGoNextPage.setOnClickListener(this)
        etDistrict.setOnClickListener(this)
        etThana.setOnClickListener(this)
        etAriaPostOffice.setOnClickListener(this)
        paymentDetailsLayout.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0){
            consLayGoNextPage -> {
                if(validate()){
                    addOrderFragmentTwo()
                }
            }
            // same action tvDetails n tvAddOrderTotalOrder
            etDistrict ->{
                if (districtList.isEmpty()) {
                    getDistrictThanaOrAria(0,1)
                } else {
                    goToDistrict()
                }
            }
            etThana ->{
                if (district != 0) {
                    getDistrictThanaOrAria(district,2)
                } else {
                    showShortToast(context!!, getString(R.string.select_dist))
                }
            }
            etAriaPostOffice ->{
                if (isAriaAvailable) {
                    if (thana != 0) {
                        getDistrictThanaOrAria(thana,3)
                    } else {
                        showShortToast(context!!, getString(R.string.select_thana))
                    }
                } else {
                    showShortToast(context!!, getString(R.string.no_aria))
                }
            }
            paymentDetailsLayout -> {

                val detailsSheet = DetailsBottomSheet.newInstance(Bundle())
                detailsSheet.show(childFragmentManager, DetailsBottomSheet.tag)
            }

        }
    }


    private fun validate(): Boolean {
        var go = true
        getAllViewData()
        if(customerName.isEmpty()){
            showShortToast(context, getString(R.string.write_yr_name))
            go = false
            etCustomerName.requestFocus()
        }
        else if (mobileNo.isEmpty()) {
            showShortToast(context, getString(R.string.write_phone_number))
            go = false
            etAddOrderMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(mobileNo) || mobileNo.length < 11) {
            showShortToast(context, getString(R.string.write_proper_phone_number_recharge))
            go = false
            etAddOrderMobileNo.requestFocus()
        }/* else if(alternativeMobileNo.isEmpty()){
            go = false
            showShortToast(context!!, getString(R.string.write_alt_phone_number))
            etAlternativeMobileNo.requestFocus()
        }*/
        else if(district == 0){
            go = false
            showShortToast(context!!, getString(R.string.select_dist))
        }
        else if(thana == 0){
            go = false
            showShortToast(context!!, getString(R.string.select_thana))
        }
        else if(isAriaAvailable && ariaPostOffice == 0){
            go = false
            showShortToast(context!!, getString(R.string.select_aria))
        }
        else if(customersAddress.isEmpty()){
            go = false
            showShortToast(context!!, getString(R.string.write_yr_address))
            etCustomersAddress.requestFocus()
        }
        hideSoftKeyBoard(activity!!)
        return go
    }

    private fun getAllViewData(){
        customerName = etCustomerName.text.toString()
        mobileNo = etAddOrderMobileNo.text.toString()
        alternativeMobileNo = etAlternativeMobileNo.text.toString()
        customersAddress = etCustomersAddress.text.toString()
        additionalNote = etAdditionalNote.text.toString()
    }

    private fun addOrderFragmentTwo(){
        val fragment = AddOrderFragmentTwo.newInstance(getBundle())
        val ft: FragmentTransaction? = (context as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AddOrderFragmentTwo.tag)
        ft?.addToBackStack(AddOrderFragmentTwo.tag)
        ft?.commit()
    }

    private fun getDistrictThanaOrAria(id: Int, track: Int){
        hideSoftKeyBoard(activity!!)
        //track = 1 district , track = 2 thana, track = 3 aria
        val pd = ProgressDialog(context)
        pd.setMessage("Loading")
        pd.show()

        val getDistrictThanaOrAria = RetrofitSingleton.getInstance(context!!).create(DistrictInterface::class.java)
        getDistrictThanaOrAria.getAllDistrictFromApi(id).enqueue(object : Callback<DeliveryChargePayLoad>{
            override fun onFailure(call: Call<DeliveryChargePayLoad>, t: Throwable) {
                Timber.e("districtThanaOrAria_f-",t.toString())
                pd.dismiss()
            }

            override fun onResponse(
                call: Call<DeliveryChargePayLoad>,
                response: Response<DeliveryChargePayLoad>
            ) {
                pd.dismiss()
               if(response.isSuccessful && response.body() != null && response.body()!!.data!!.districtInfo != null){
                   Timber.e("districtThanaOrAria_s-",response.body().toString())

                   if (track == 1) {
                       districtList.addAll(response.body()!!.data!!.districtInfo!!)
                       goToDistrict()
                   } else if(track == 2){
                       thanaOrAriaList.clear()
                       thanaOrAriaList.addAll(response.body()!!.data!!.districtInfo!![0].thanaHome!!)
                       if (thanaOrAriaList.isNotEmpty()) {
                           customAlertDialog(thanaOrAriaList, 1)
                       }
                   }else if(track == 3){
                       thanaOrAriaList.clear()
                       thanaOrAriaList.addAll(response.body()!!.data!!.districtInfo!![0].thanaHome!!)
                       if (thanaOrAriaList.isNotEmpty()) {
                           customAlertDialog(thanaOrAriaList, 2)
                       }
                   }
               }
            }

        })
    }

    private fun goToDistrict() {

        val distFrag = DistrictSelectFragment.newInstance(mContext, districtList)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.setOnClick(object : DistrictSelectFragment.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                Timber.e("etDistrictSearch 6 - ",name + " " + clickedID.toString())
                etDistrict.setText(name)
                district = clickedID

                thana = 0
                ariaPostOffice = 0
                etAriaPostOffice.setText("")
                etThana.setText("")
            }
        })
    }

    private fun customAlertDialog(thanaOrAriaList: ArrayList<ThanaPayLoad>, track: Int){
        hideSoftKeyBoard(activity!!)
        val dialogBuilder = Builder(mContext)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.custom_alert_lay, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        val textViewHead: TextView = dialogView.findViewById(R.id.headerDistrictOrThana)
        val ivDistClose: ImageView = dialogView.findViewById(R.id.ivDistClose)
        val rvListOfThanaOrAria: RecyclerView = dialogView.findViewById(R.id.rvListOfThanaOrAria)

        if(track == 1) textViewHead.text = "থানা নির্বাচন করুন" else textViewHead.text = "এরিয়া/পোস্ট অফিস নির্বাচন করুন"

        val dialog = dialogBuilder.create()
        dialog.show()
        val thanaOrAriaAdapter = ThanaOrAriaAdapter(mContext,thanaOrAriaList)
        rvListOfThanaOrAria.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = thanaOrAriaAdapter
        }

        ivDistClose.setOnClickListener {
            dialog.dismiss()
        }

        thanaOrAriaAdapter.setOnClick(object :ThanaOrAriaAdapter.OnClickedListener{
            override fun onClick(pos: Int) {
                dialog.dismiss()
                isAriaAvailable = thanaOrAriaList[pos].hasArea == 1
                Timber.e("isAriaAvailable",isAriaAvailable.toString() +" "+track)
                if (track == 1) {
                    etThana.setText(thanaOrAriaList[pos].thanaBng)
                    thana = thanaOrAriaList[pos].thanaId
                    ariaPostOffice = 0
                    etAriaPostOffice.setText("")
                } else {
                    if (thanaOrAriaList[pos].postalCode != null) {
                        if (thanaOrAriaList[pos].postalCode!!.isNotEmpty()) {
                            ariaPostOffice = thanaOrAriaList[pos].postalCode?.toInt()!!
                            etAriaPostOffice.setText(thanaOrAriaList[pos].thanaBng+" ("+thanaOrAriaList[pos].postalCode+")")
                        } else {
                            ariaPostOffice = 0
                            isAriaAvailable = false
                        }
                    }
                }
            }

        })


    }

    private fun getBundle(): Bundle{
        val bundle = Bundle()
        bundle.putString(BundleFlag.CUSTOMER_NAME,customerName)
        bundle.putString(BundleFlag.MOBILE_NUMBER,mobileNo)
        bundle.putString(BundleFlag.ALT_MOBILE_NUMBER,alternativeMobileNo)
        bundle.putInt(BundleFlag.DISTRICT_ID,district)
        bundle.putInt(BundleFlag.THANA_ID,thana)
        bundle.putInt(BundleFlag.ARIA_ID,ariaPostOffice)
        bundle.putString(BundleFlag.CUSTOMERS_ADDRESS,customersAddress)
        bundle.putString(BundleFlag.ADDITIONAML_NOTE,additionalNote)

        return bundle
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }
    private lateinit var mContext:Context

}
