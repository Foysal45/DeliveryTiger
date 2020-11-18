package com.bd.deliverytiger.app.ui.filter


import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.endpoint.OtherApiInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.status.StatusGroupModel
import com.bd.deliverytiger.app.utils.CustomSpinnerAdapter
import com.bd.deliverytiger.app.utils.DigitConverter
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.VariousTask
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FilterFragment : Fragment() {

    private lateinit var searchET: EditText
    private lateinit var clearFilter: TextView

    private lateinit var dateFilterLayout: ConstraintLayout
    private lateinit var fromDateTV: TextView
    private lateinit var toDateTV: TextView

    private lateinit var statusFilterLayout: LinearLayout
    private lateinit var statusTV: TextView
    private lateinit var statusLayout: ConstraintLayout
    private lateinit var statusSpinner: AppCompatSpinner

    private lateinit var orderFilterLayout: LinearLayout
    private lateinit var filterOrderSpinner: AppCompatSpinner

    private lateinit var applyBtn: MaterialButton

    private lateinit var otherApiInterface: OtherApiInterface

    private var gotFromDate: String = "2001-01-01"
    private var gotToDate: String = "2001-01-01"
    private var statusId = -1
    private var statusGroup = "-1"
    private var filterType = 0
    private var isFromDateSelected = false
    private var isToDateSelected = false
    private val statusList: MutableList<String> = mutableListOf()
    private var searchKey = ""
    private var searchType = 0
    private var orderType: String = ""

    companion object{
        fun newInstance(fromDate: String = "2001-01-01", toDate: String = "2001-01-01", status: Int = -1, statusGroup: String = "-1", searchKey: String = "", filterType: Int = 0): FilterFragment = FilterFragment().apply {
            this.gotFromDate = fromDate
            this.gotToDate = toDate
            this.statusId = status
            this.statusGroup = statusGroup
            this.filterType = filterType
            this.searchKey = searchKey
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

        dateFilterLayout = view.findViewById(R.id.dateFilterLayout)
        fromDateTV = view.findViewById(R.id.filter_date_from)
        toDateTV = view.findViewById(R.id.filter_date_to)

        statusFilterLayout = view.findViewById(R.id.statusFilterLayout)
        statusTV = view.findViewById(R.id.title4)
        statusLayout = view.findViewById(R.id.filter_status_layout)
        statusSpinner = view.findViewById(R.id.filter_status_spinner)

        orderFilterLayout = view.findViewById(R.id.orderFilterLayout)
        filterOrderSpinner = view.findViewById(R.id.filterOrderSpinner)

        applyBtn = view.findViewById(R.id.filter_apply)

        if (gotFromDate != "2001-01-01"){
            val formattedDate = DigitConverter.toBanglaDate(gotFromDate, "yyyy-MM-dd")
            fromDateTV.text = formattedDate
            isFromDateSelected = true
        }
        if (gotToDate != "2001-01-01"){
            val formattedDate = DigitConverter.toBanglaDate(gotToDate, "yyyy-MM-dd")
            toDateTV.text = formattedDate
            isToDateSelected = true
        }

        if (searchKey.isNotEmpty()){
            searchET.setText(searchKey)
        }

        otherApiInterface = RetrofitSingleton.getInstance(requireContext()).create(OtherApiInterface::class.java)
        if (filterType == 1){
            searchET.isFocusableInTouchMode = true
            searchET.requestFocus()
            Handler().postDelayed({
                VariousTask.showKeyboard(activity)
            }, 300L)
            loadStatusGroup()
        } else if (filterType == 2) {
            statusFilterLayout.visibility = View.GONE
        } else if(filterType == 3) {
            dateFilterLayout.visibility = View.GONE
            orderFilterLayout.visibility = View.VISIBLE
            initOrderTypeFilter()
            loadStatusGroup()
        } else {
            loadStatusGroup()
        }


        /*searchET.onFocusChangeListener = object : View.OnFocusChangeListener{
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (!p1){
                    VariousTask.hideSoftKeyBoard(activity)
                }
            }

        }*/

        fromDateTV.setOnClickListener {

            val date = if (gotToDate != "2001-01-01") gotToDate else ""
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

            val date = if (gotFromDate != "2001-01-01") gotFromDate else ""
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
                listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup, searchKey, searchType, orderType)
            } else {
                if (isFromDateSelected && !isToDateSelected) {
                    Toast.makeText(activity, "শেষের তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isToDateSelected && !isFromDateSelected) {
                    Toast.makeText(activity, "শুরুর তারিখ দিন", Toast.LENGTH_SHORT).show()
                } else if (isFromDateSelected && isToDateSelected) {
                    listener?.selectedDate(gotFromDate, gotToDate, statusId, statusGroup, searchKey, searchType, orderType)
                }
            }

        }

        clearFilter.setOnClickListener {
            fromDateTV.text = ""
            toDateTV.text = ""
            statusSpinner.setSelection(0)
            gotFromDate = "2001-01-01"
            gotToDate = "2001-01-01"
            statusId = -1
            statusGroup = "-1"
            searchType = 0
            searchKey = ""
            searchET.text.clear()
            orderType = ""
        }
    }

    fun forceHideKeyboard(){
        VariousTask.hideSoftKeyBoard(activity)
    }

    private fun loadStatusGroup() {

        statusList.clear()
        statusList.add("সকল স্ট্যাটাস")
        val statusAdapter = CustomSpinnerAdapter(requireContext(), R.layout.item_view_spinner_item, statusList)
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

    private fun initOrderTypeFilter() {

        filterOrderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                orderType = when (p2) {
                    0 -> ""
                    1 -> "onlydelivery"
                    2 -> "takadelivery"
                    else -> ""
                }
            }
        }
    }

    private var listener: FilterListener? = null

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    interface FilterListener {
        fun selectedDate(fromDate1: String, toDate1: String, status1: Int, statusGroup: String, searchKey: String, searchType: Int, orderType: String)
    }
}
