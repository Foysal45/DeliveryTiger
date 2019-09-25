package com.bd.deliverytiger.app.ui.add_order


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity

/**
 * A simple [Fragment] subclass.
 */
class AddOrderFragmentOne : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(): AddOrderFragmentOne {
            val fragment = AddOrderFragmentOne()
            return fragment
        }
        val tag = AddOrderFragmentOne::class.java.name
    }

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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        consLayGoNextPage.setOnClickListener {
           if(validate()){

           }
        }
    }

    private fun validate(): Boolean {
        var go = true
        return go
    }


}
