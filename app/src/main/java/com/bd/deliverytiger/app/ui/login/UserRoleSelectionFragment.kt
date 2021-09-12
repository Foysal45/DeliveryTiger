package com.bd.deliverytiger.app.ui.login
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentUserRoleSelectionBinding
import com.bd.deliverytiger.app.log.UserLogger
import com.bd.deliverytiger.app.ui.charge_calculator.DeliveryChargeCalculatorFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.callHelplineNumber
import org.koin.android.ext.android.inject

class UserRoleSelectionFragment : Fragment() {

    private var binding: FragmentUserRoleSelectionBinding? = null
    private val viewModel: AuthViewModel by inject()


    companion object {
        fun newInstance(): UserRoleSelectionFragment = UserRoleSelectionFragment()
        val tag: String = UserRoleSelectionFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentUserRoleSelectionBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        showDeliveryChargeCalculator()

        binding?.merchantBtn?.setOnClickListener {
            goToUserRoleMerchant()
        }
        binding?.customerBtn?.setOnClickListener {
            goToOrderTrackingFragment()
            UserLogger.logGenie("Customer_Login")
        }

        //Hidden as requirement
        /*viewModel.fetchHelpLineNumbers().observe(viewLifecycleOwner, Observer { model->
            if (model.helpLine1.isNullOrEmpty()){
                binding?.helpLineContactLayout?.visibility = View.GONE
            }else{
                binding?.helpLineContactLayout?.visibility = View.VISIBLE
                binding?.helpLineNumber?.text = DigitConverter.toBanglaDigit(model.helpLine1)
                binding?.helpLineNumber?.setOnClickListener{
                    callHelplineNumber(model.helpLine1!!)
                }
            }
        })*/
    }

    private fun showDeliveryChargeCalculator() {
        val bundle = bundleOf(
            "isHideTitle" to true
        )
        val tag = DeliveryChargeCalculatorFragment.tag
        val fragment = DeliveryChargeCalculatorFragment.newInstance()
        fragment.arguments = bundle
        binding?.container?.let { _ ->
            childFragmentManager.beginTransaction().replace(R.id.container, fragment, tag).commit()
        }
    }

    private fun goToUserRoleMerchant(){

        if (SessionManager.isLogin) {
            val intentMerchantLogin = Intent(activity, HomeActivity::class.java)
            activity?.startActivity(intentMerchantLogin)
            activity?.finish()
        } else {
            addLoginFragment()
        }
    }

    private fun goToOrderTrackingFragment(){

        val bundle = bundleOf("containerType" to "login")
        val fragment: OrderTrackingFragment = OrderTrackingFragment.newInstance()
        fragment.arguments = bundle
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, "OrderTrackingFragment")
        ft?.addToBackStack("OrderTrackingFragment")
        ft?.commit()
    }

    private fun addLoginFragment(){

        val fragment = LoginFragment.newInstance(false)
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, LoginFragment.tag)
        ft?.addToBackStack(LoginFragment.tag)
        ft?.commit()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
