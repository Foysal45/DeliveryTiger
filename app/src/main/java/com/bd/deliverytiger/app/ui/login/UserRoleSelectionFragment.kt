package com.bd.deliverytiger.app.ui.login
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.order_tracking.OrderTrackingFragment
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber


/**
 * A simple [Fragment] subclass.
 */
class UserRoleSelectionFragment : Fragment() {

    private val TAG = "UserRoleSelectionFragment"

    private lateinit var layoutUserMerchant: LinearLayout
    private lateinit var layoutUserCustomer: LinearLayout

    companion object {
        fun newInstance(): UserRoleSelectionFragment = UserRoleSelectionFragment().apply {

        }
        val tag: String = UserRoleSelectionFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_role_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutUserCustomer = view.findViewById(R.id.layout_user_customer)
        layoutUserMerchant = view.findViewById(R.id.layout_user_merchant)

        layoutUserMerchant.setOnClickListener {
            Timber.d("UserRoleSelection", "layoutUserMerchant clicked")
            goToUserRoleMerchant()
        }

        layoutUserCustomer.setOnClickListener {
            Timber.d("UserRoleSelection", "layoutUserCustomer clicked")
            goToOrderTrackingFragment()
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
}
