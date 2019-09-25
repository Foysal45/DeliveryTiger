package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.Validator.editTextEnableOrDisable
import com.bd.deliverytiger.app.utils.Validator.hideSoftKeyBoard
import com.bd.deliverytiger.app.utils.Validator.showShortToast
import kotlinx.android.synthetic.main.fragment_sign_up.*

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

    private lateinit var tvDetails: TextView
    private lateinit var tvAddOrderTotalOrder: TextView
    private lateinit var ivAddOrderArrow: ImageView
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
    private var district = 10
    private var thana = 10
    private var ariaPostOffice = 10
    private var customersAddress = ""
    private var additionalNote = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_order_fragment_one, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as HomeActivity).setToolbarTitle("কাস্টমারের তথ্য")
        tvDetails = view.findViewById(R.id.tvDetails)
        tvAddOrderTotalOrder = view.findViewById(R.id.tvAddOrderTotalOrder)
        ivAddOrderArrow = view.findViewById(R.id.ivAddOrderArrow)
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

    private fun iniViewClicked(){
        consLayGoNextPage.setOnClickListener(this)
        tvDetails.setOnClickListener(this)
        tvAddOrderTotalOrder.setOnClickListener(this)
        etDistrict.setOnClickListener(this)
        etThana.setOnClickListener(this)
        etAriaPostOffice.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0){
            consLayGoNextPage -> {
                if(validate()){
                    addOrderFragmentTwo()
                }
            }
            tvDetails ->{

            }
            // same action tvDetails n tvAddOrderTotalOrder
            tvAddOrderTotalOrder ->{

            }
            etDistrict ->{

            }
            etThana ->{

            }
            etAriaPostOffice ->{

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
        } else if(alternativeMobileNo.isEmpty()){
            go = false
            showShortToast(context!!, getString(R.string.write_alt_phone_number))
            etAlternativeMobileNo.requestFocus()
        }
        else if(district == 0){
            go = false
            showShortToast(context!!, getString(R.string.select_dist))
        }
        else if(thana == 0){
            go = false
            showShortToast(context!!, getString(R.string.select_thana))
        }
        else if(ariaPostOffice == 0){
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
        val fragment = AddOrderFragmentTwo.newInstance()
        val ft: FragmentTransaction? = (context as FragmentActivity?)?.supportFragmentManager?.beginTransaction()
        ft?.add(R.id.mainActivityContainer, fragment, AddOrderFragmentTwo.tag)
        ft?.addToBackStack(AddOrderFragmentTwo.tag)
        ft?.commit()
    }

}
