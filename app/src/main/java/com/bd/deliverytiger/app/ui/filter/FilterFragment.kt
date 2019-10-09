package com.bd.deliverytiger.app.ui.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatSpinner
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.OtherApiInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.status.StatusGroupModel
import com.bd.deliverytiger.app.api.model.status.StatusModel
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class FilterFragment : Fragment() {

    private lateinit var fromDateTV: TextView
    private lateinit var toDateTV: TextView
    private lateinit var statusSpinner: AppCompatSpinner
    private lateinit var applyBtn: MaterialButton

    private lateinit var otherApiInterface: OtherApiInterface

    private var gotFromDate: String = "01-01-01"
    private var gotToDate: String = "01-01-01"
    private var statusId = -1
    private var statusGroup = ""
    private var isFromDateSelected = false
    private var isToDateSelected = false

    companion object{
        fun newInstance(fromDate: String = "01-01-01", toDate: String = "01-01-01", status: Int = -1, statusGroup: String = ""): FilterFragment = FilterFragment().apply {
            this.gotFromDate = fromDate
            this.gotToDate = toDate
            this.statusId = status
            this.statusGroup = statusGroup
        }
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

        if (gotFromDate != "01-01-01"){
            fromDateTV.text = gotFromDate
        }
        if (gotToDate != "01-01-01"){
            toDateTV.text = gotToDate
        }

        otherApiInterface = RetrofitSingleton.getInstance(context!!).create(OtherApiInterface::class.java)
        //loadOrderStatus()
        loadStatusGroup()

        fromDateTV.setOnClickListener {

            val date = if (gotToDate != "01-01-01") gotToDate else ""
            val fromDate = DatePickerDialogCustom.newInstance(1, date)
            activity?.supportFragmentManager?.let {
                    it1 -> fromDate.show(it1, "")
            }
            fromDate.setDate(object : DatePickerDialogCustom.PassDateInterface2 {
                override fun gotDate2(date: String, flag: Int) {
                    gotFromDate = date
                    fromDateTV.text = gotFromDate
                    isFromDateSelected = true
                }

            })
        }

        toDateTV.setOnClickListener {

            val date = if (gotFromDate != "01-01-01") gotFromDate else ""
            val toDate = DatePickerDialogCustom.newInstance(2, date)
            activity?.supportFragmentManager?.let { it1 -> toDate.show(it1, "") }
            toDate.setDate(object : DatePickerDialogCustom.PassDateInterface2 {
                override fun gotDate2(date: String, flag: Int) {
                    gotToDate = date
                    toDateTV.text = gotToDate
                    isToDateSelected = true
                }

            })
        }

        applyBtn.setOnClickListener {

            if (!isFromDateSelected && !isToDateSelected){
                listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup)
            } else {
                if (isFromDateSelected && !isToDateSelected) {
                    Toast.makeText(activity, "শেষের তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isToDateSelected && !isFromDateSelected) {
                    Toast.makeText(activity, "শুরুর তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isFromDateSelected && isToDateSelected) {
                    listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup)
                }
            }
        }
    }

    private fun loadOrderStatus(){
        otherApiInterface.loadCourierOrderStatus().enqueue(object : Callback<GenericResponse<MutableList<StatusModel>>> {
            override fun onFailure(call: Call<GenericResponse<MutableList<StatusModel>>>, t: Throwable) {

            }

            override fun onResponse(call: Call<GenericResponse<MutableList<StatusModel>>>, response: Response<GenericResponse<MutableList<StatusModel>>>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null && response.body()!!.model.isNotEmpty()) {

                        val statusList: MutableList<String> = mutableListOf()
                        var preSelectedIndex = 0
                        for ((index,model) in response.body()!!.model.withIndex()) {
                            statusList.add(model.statusNameBng ?: "")
                            if (model.statusId == statusId) {
                                preSelectedIndex = index
                            }
                        }
                        val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, statusList)
                        statusSpinner.adapter = packagingAdapter
                        statusSpinner.setSelection(preSelectedIndex)


                        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                val model2 = response.body()!!.model[p2]
                                statusId = model2.statusId
                            }

                        }
                    }
                }
            }
        })
    }

    private fun loadStatusGroup() {

        otherApiInterface.loadStatusGroup().enqueue(object : Callback<GenericResponse<MutableList<StatusGroupModel>>> {
            override fun onFailure(call: Call<GenericResponse<MutableList<StatusGroupModel>>>, t: Throwable) {

            }

            override fun onResponse(call: Call<GenericResponse<MutableList<StatusGroupModel>>>, response: Response<GenericResponse<MutableList<StatusGroupModel>>>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null && response.body()!!.model.isNotEmpty()) {

                        val statusList: MutableList<String> = mutableListOf()
                        var preSelectedIndex = 0
                        for ((index,model) in response.body()!!.model.withIndex()) {
                            if (model.dashboardStatusGroup.isNotEmpty()){
                                statusList.add(model.dashboardStatusGroup)
                                if (model.dashboardStatusGroup == statusGroup) {
                                    preSelectedIndex = statusList.size - 1
                                }
                            }
                        }
                        val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, statusList)
                        statusSpinner.adapter = packagingAdapter
                        statusSpinner.setSelection(preSelectedIndex)


                        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                //val model2 = response.body()!!.model[p2]
                                statusGroup = statusList[p2]
                            }

                        }
                    }
                }
            }
        })
    }

    private var listener: FilterListener? = null

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    interface FilterListener {
        fun selectedDate(fromDate1: String, toDate1: String, status1: Int, statusGroup: String)
    }
}
