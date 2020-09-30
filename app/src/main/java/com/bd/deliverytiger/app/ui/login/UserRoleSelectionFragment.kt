package com.bd.deliverytiger.app.ui.login
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentUserRoleSelectionBinding
import com.bd.deliverytiger.app.ui.charge_calculator.DeliveryChargeCalculatorFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.SessionManager

class UserRoleSelectionFragment : Fragment() {

    private var binding: FragmentUserRoleSelectionBinding? = null

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
        }
    }

    private fun showDeliveryChargeCalculator() {
        val tag = DeliveryChargeCalculatorFragment.tag
        val fragment = DeliveryChargeCalculatorFragment.newInstance()
        binding?.container?.let { container ->
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

        val fragment: OrderTrackingFragment = OrderTrackingFragment.newInstance("")
        val ft: FragmentTransaction? = activity?.supportFragmentManager?.beginTransaction()
        ft?.replace(R.id.loginActivityContainer, fragment, OrderTrackingFragment.tag)
        ft?.addToBackStack(OrderTrackingFragment.tag)
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
