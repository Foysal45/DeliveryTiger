package com.bd.deliverytiger.app.ui.all_orders


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.dashboard.DashboardData
import com.bd.deliverytiger.app.ui.dashboard.DashboardAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * A simple [Fragment] subclass.
 */
class PaymentStatusSheet : BottomSheetDialogFragment() {

    private lateinit var paymentRV: RecyclerView

    private var collectionAmount = 0
    private var paymentInProcessing = 0
    private var paymentPaid = 0
    private var paymentReady = 0

    companion object{
        fun newInstance(collectionAmount: Int, paymentInProcessing: Int, paymentPaid: Int, paymentReady: Int): PaymentStatusSheet = PaymentStatusSheet().apply {
            this.collectionAmount = collectionAmount
            this.paymentInProcessing = paymentInProcessing
            this.paymentPaid = paymentPaid
            this.paymentReady = paymentReady
        }
        val tag = PaymentStatusSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_status_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        paymentRV = view.findViewById(R.id.payment_status_sheet_rv)

        val list: MutableList<DashboardData> = mutableListOf()
        list.add(DashboardData(0,"কালেকশন এমাউন্ট", collectionAmount, 0, "positive", 0, ""))


        val dashboardAdapter = DashboardAdapter(context!!, list)
        with(paymentRV) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = dashboardAdapter
        }

    }



}
