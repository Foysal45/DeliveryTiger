package com.bd.deliverytiger.app.ui.order_tracking
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bd.deliverytiger.app.R

/**
 * A simple [Fragment] subclass.
 */
class OrderTrackingFragment : Fragment() {

    companion object{
        fun newInstance(): OrderTrackingFragment {
            val fragment = OrderTrackingFragment()
            return fragment
        }
        val tag = OrderTrackingFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_tracking, container, false)
    }


}
