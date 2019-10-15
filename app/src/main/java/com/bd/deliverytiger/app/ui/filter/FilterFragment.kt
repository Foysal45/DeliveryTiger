package com.bd.deliverytiger.app.ui.filter


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.EditText
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
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.Validator
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class FilterFragment : Fragment() {

    private lateinit var searchET: EditText
    private lateinit var clearFilter: TextView
    private lateinit var fromDateTV: TextView
    private lateinit var toDateTV: TextView
    private lateinit var statusSpinner: AppCompatSpinner
    private lateinit var applyBtn: MaterialButton

    private lateinit var otherApiInterface: OtherApiInterface

    private var gotFromDate: String = "01-01-01"
    private var gotToDate: String = "01-01-01"
    private var statusId = -1
    private var statusGroup = "-1"
    private var filterType = 0
    private var isFromDateSelected = false
    private var isToDateSelected = false
    private val statusList: MutableList<String> = mutableListOf()
    private var searchKey = ""
    private var searchType = 0

    companion object{
        fun newInstance(fromDate: String = "01-01-01", toDate: String = "01-01-01", status: Int = -1, statusGroup: String = "-1", filterType: Int = 0): FilterFragment = FilterFragment().apply {
            this.gotFromDate = fromDate
            this.gotToDate = toDate
            this.statusId = status
            this.statusGroup = statusGroup
            this.filterType = filterType
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

        searchET = view.findViewById(R.id.filter_search_key)
        clearFilter = view.findViewById(R.id.filter_clear_tv)
        fromDateTV = view.findViewById(R.id.filter_date_from)
        toDateTV = view.findViewById(R.id.filter_date_to)
        statusSpinner = view.findViewById(R.id.filter_status_spinner)
        applyBtn = view.findViewById(R.id.filter_apply)

        if (gotFromDate != "01-01-01"){
            val formattedDate = DigitConverter.toBanglaDate(gotFromDate, "yyyy-MM-dd")
            fromDateTV.text = formattedDate
        }
        if (gotToDate != "01-01-01"){
            val formattedDate = DigitConverter.toBanglaDate(gotToDate, "yyyy-MM-dd")
            toDateTV.text = formattedDate
        }

        otherApiInterface = RetrofitSingleton.getInstance(context!!).create(OtherApiInterface::class.java)
        if (filterType == 0){
            loadStatusGroup()
        } else{
            loadOrderStatus()
        }


        fromDateTV.setOnClickListener {

            val date = if (gotToDate != "01-01-01") gotToDate else ""
            val fromDate = DatePickerDialogCustom.newInstance(1, date)
            activity?.supportFragmentManager?.let {
                    it1 -> fromDate.show(it1, "")
            }
            fromDate.setDate(object : DatePickerDialogCustom.PassDateInterface2 {
                override fun gotDate2(date: String, flag: Int) {
                    gotFromDate = date
                    val formattedDate = DigitConverter.toBanglaDate(gotFromDate, "yyyy-MM-dd")
                    fromDateTV.text = formattedDate
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
                    val formattedDate = DigitConverter.toBanglaDate(gotToDate, "yyyy-MM-dd")
                    toDateTV.text = formattedDate
                    isToDateSelected = true
                }

            })
        }

        applyBtn.setOnClickListener {

            searchKey = searchET.text.toString()
            if (searchKey.isNotEmpty()){
                if (Validator.isValidMobileNumber(searchKey)){
                    searchType = 1
                } else if (searchKey.contains("DT-", ignoreCase = true)){
                    searchType = 2
                } else {
                    searchType = 3
                }
            }

            if (!isFromDateSelected && !isToDateSelected){
                listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup, searchKey, searchType)
            } else {
                if (isFromDateSelected && !isToDateSelected) {
                    Toast.makeText(activity, "শেষের তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isToDateSelected && !isFromDateSelected) {
                    Toast.makeText(activity, "শুরুর তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isFromDateSelected && isToDateSelected) {
                    listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup, searchKey, searchType)
                }
            }

        }

        clearFilter.setOnClickListener {
            fromDateTV.text = ""
            toDateTV.text = ""
            statusSpinner.setSelection(0)
            gotFromDate = "01-01-01"
            gotToDate = "01-01-01"
            statusId = -1
            statusGroup = "-1"
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

        statusList.clear()
        statusList.add("সকল স্ট্যাটাস")
        val statusAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, statusList)
        statusSpinner.adapter = statusAdapter

        otherApiInterface.loadStatusGroup().enqueue(object : Callback<GenericResponse<MutableList<StatusGroupModel>>> {
            override fun onFailure(call: Call<GenericResponse<MutableList<StatusGroupModel>>>, t: Throwable) {

            }

            override fun onResponse(call: Call<GenericResponse<MutableList<StatusGroupModel>>>, response: Response<GenericResponse<MutableList<StatusGroupModel>>>) {
                if (response.isSuccessful && response.body() != null){
                    if (response.body()!!.model != null && response.body()!!.model.isNotEmpty()) {

                        statusList.clear()
                        statusList.add("সকল স্ট্যাটাস")
                        var preSelectedIndex = 0
                        for ((index,model) in response.body()!!.model.withIndex()) {
                            if (model.dashboardStatusGroup.isNotEmpty()){
                                statusList.add(model.dashboardStatusGroup)
                                if (model.dashboardStatusGroup == statusGroup) {
                                    preSelectedIndex = statusList.size - 1
                                }
                            }
                        }
                        //val packagingAdapter = CustomSpinnerAdapter(context!!, R.layout.item_view_spinner_item, statusList)
                        //statusSpinner.adapter = packagingAdapter
                        statusAdapter.notifyDataSetChanged()
                        statusSpinner.setSelection(preSelectedIndex)


                        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(p0: AdapterView<*>?) {

                            }

                            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                                //val model2 = response.body()!!.model[p2]
                                if (p2 == 0){
                                    statusGroup = "-1"
                                } else {
                                    statusGroup = statusList[p2]
                                }
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
        fun selectedDate(fromDate1: String, toDate1: String, status1: Int, statusGroup: String, searchKey: String, searchType: Int)
    }
}
