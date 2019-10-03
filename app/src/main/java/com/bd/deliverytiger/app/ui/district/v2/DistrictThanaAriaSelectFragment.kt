package com.bd.deliverytiger.app.ui.district.v2


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.VariousTask

/**
 * A simple [Fragment] subclass.
 */
class DistrictThanaAriaSelectFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(context: Context, list: ArrayList<CustomModel>,title: String) =
            DistrictThanaAriaSelectFragment().apply {
                this.list = list
                this.mContext = context
                this.title = title
            }

        val tag = DistrictThanaAriaSelectFragment::class.java.name
    }

    private lateinit var title: String
    private var list: ArrayList<CustomModel> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var etDistrictSearch: AutoCompleteTextView
    private lateinit var rvDistrictSuggestion: RecyclerView
    private lateinit var adapter: ArrayAdapter<CustomModel>
    private val suggestList: ArrayList<CustomModel> = ArrayList()
    private lateinit var districtSuggestAdapter: ListAdapter
    private lateinit var rvSelectDistrict: RecyclerView
    private lateinit var distCross: ImageView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_district_thana_aria_select, container, false)
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)
        rvSelectDistrict = v.findViewById(R.id.rvSelectDistrict)
        distCross = v.findViewById(R.id.distCross)
        etDistrictSearch = v.findViewById(R.id.etDistrictSearch)
        rvDistrictSuggestion = v.findViewById(R.id.rvDistrictSuggestion)

        searchTextListener()
        etDistrictSearch.hint = title

        val listAdapter = ListAdapter(mContext!!, list)
        rvSelectDistrict.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        listAdapter.onItemClick = { position: Int, name: String, id: Int, listPosition ->
            onItemClick?.invoke(position, name, id,listPosition)
            //  dismiss()
            Timber.e("etDistrict 6", " - district clicked")
            VariousTask.hideSoftKeyBoard(activity!!)
            (mContext as FragmentActivity).supportFragmentManager.popBackStack()
        }

        distCross.setOnClickListener {
            //  dismiss()
            VariousTask.hideSoftKeyBoard(activity!!)
            (mContext as FragmentActivity).supportFragmentManager.popBackStack()
        }
    }


    private fun searchTextListener() {
        etDistrictSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Timber.e("etDistrictSearch 1 - "+ s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Timber.e("etDistrictSearch 2 - "+ s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // Timber.e("etDistrictSearch 3 - " + s.toString())
                getMatchingString(s.toString())
            }

        })

        districtSuggestAdapter = ListAdapter(context!!, suggestList)
        rvDistrictSuggestion.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = districtSuggestAdapter
        }


        districtSuggestAdapter.onItemClick = { position: Int, name: String, id: Int, listPosition ->
            onItemClick?.invoke(position, name, id,listPosition)
            //  dismiss()
            Timber.e("etDistrictSearch 6", " - districtSuggestAdapter clicked")
            VariousTask.hideSoftKeyBoard(activity!!)
            (mContext as FragmentActivity).supportFragmentManager.popBackStack()
        }
    }

    private fun getMatchingString(searchTxt: String): ArrayList<CustomModel> {
        suggestList.clear()
        for (model in list) {
            if (model != null) {
                if (model.engName.toLowerCase().contains(searchTxt.toLowerCase()) || model.bangName.contains(
                        searchTxt
                    )
                ) {
                    // Timber.e("etDistrictSearch 4 - " + model.district)
                    suggestList.add(model)
                }
            }
        }
        Timber.e("etDistrictSearch 5 - %s", suggestList.toString())

        if (suggestList.size > 0) {
            rvDistrictSuggestion.visibility = View.VISIBLE
            rvSelectDistrict.visibility = View.GONE
        } else {
            rvDistrictSuggestion.visibility = View.GONE
            rvSelectDistrict.visibility = View.VISIBLE
        }
        districtSuggestAdapter.notifyDataSetChanged()

        return suggestList
    }

    var onItemClick: ((position: Int, name: String, id: Int,listPosition: Int) -> Unit)? = null
}
