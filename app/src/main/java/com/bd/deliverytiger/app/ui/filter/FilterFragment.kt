package com.bd.deliverytiger.app.ui.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.google.android.material.button.MaterialButton

/**
 * A simple [Fragment] subclass.
 */
class FilterFragment : Fragment() {

    private lateinit var fromDateTV: TextView
    private lateinit var toDateTV: TextView
    private lateinit var statusSpinner: AppCompatSpinner
    private lateinit var applyBtn: MaterialButton

    private var gotFromDate: String = ""
    private var gotToDate: String = ""

    companion object{
        fun newInstance(): FilterFragment = FilterFragment().apply {  }
        val tag = FilterFragment::class.java.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fromDateTV = view.findViewById(R.id.filter_date_from)
        toDateTV = view.findViewById(R.id.filter_date_to)
        statusSpinner = view.findViewById(R.id.filter_status_spinner)
        applyBtn = view.findViewById(R.id.filter_apply)

        fromDateTV.setOnClickListener {

            val fromDate = DatePickerDialogCustom.newInstance(1, gotToDate)
            activity?.supportFragmentManager?.let {
                    it1 -> fromDate.show(it1, "")
            }
            fromDate.setDate(object : DatePickerDialogCustom.PassDateInterface2 {
                override fun gotDate2(date: String, flag: Int) {
                    gotFromDate = date
                    fromDateTV.text = gotFromDate
                }

            })
        }

        toDateTV.setOnClickListener {

            val toDate = DatePickerDialogCustom.newInstance(2, gotFromDate)
            activity?.supportFragmentManager?.let { it1 -> toDate.show(it1, "") }
            toDate.setDate(object : DatePickerDialogCustom.PassDateInterface2 {
                override fun gotDate2(date: String, flag: Int) {
                    gotToDate = date
                    toDateTV.text = gotToDate
                }

            })
        }

        applyBtn.setOnClickListener {

            if (gotFromDate.isEmpty() && gotToDate.isEmpty()) {
                Toast.makeText(context, "শুরু তারিখ এবং শেষ তারিখ নির্বাচন করুন", Toast.LENGTH_SHORT).show()
            } else if (gotFromDate.isEmpty()) {
                Toast.makeText(activity, "শুরুর তারিখ দিন", Toast.LENGTH_SHORT).show()
            } else if (gotToDate.isEmpty()) {
                Toast.makeText(activity, "শেষের তারিখ দিন", Toast.LENGTH_SHORT).show()
            } else {

                listener?.selectedDate(gotFromDate, gotToDate, statusSpinner.selectedItemPosition)

            }

        }
    }

    private var listener: FilterListener? = null

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    interface FilterListener {
        fun selectedDate(fromDate: String, toDate: String, status: Int)
    }
}
