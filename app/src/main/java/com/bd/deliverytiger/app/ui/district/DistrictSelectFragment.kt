package com.bd.deliverytiger.app.ui.district


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
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator.hideSoftKeyBoard


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class DistrictSelectFragment : Fragment() {

    private var districtList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private lateinit var mContext: Context
    private lateinit var etDistrictSearch: AutoCompleteTextView
    private lateinit var rvDistrictSuggestion: RecyclerView
    private lateinit var adapter: ArrayAdapter<String>
    private val sList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private lateinit var districtSuggestAdapter: DistrictSelectAdapter

    companion object {
        @JvmStatic
        fun newInstance(context: Context, districtList: ArrayList<DistrictDeliveryChargePayLoad>) =
            DistrictSelectFragment().apply {
                this.districtList = districtList
                this.mContext = context
            }
    }

    private lateinit var rvSelectDistrict: RecyclerView
    private lateinit var distCross: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_district_select, container, false)
        rvSelectDistrict = v.findViewById(R.id.rvSelectDistrict)
        distCross = v.findViewById(R.id.distCross)
        etDistrictSearch = v.findViewById(R.id.etDistrictSearch)
        rvDistrictSuggestion = v.findViewById(R.id.rvDistrictSuggestion)


        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchTextListener()

        val districtSelectAdapter = DistrictSelectAdapter(mContext!!, districtList)
        rvSelectDistrict.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = districtSelectAdapter
        }

        districtSelectAdapter.setOnClick(object : DistrictSelectAdapter.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                districtClick?.onClick(position, name, clickedID)
                //  dismiss()
                hideSoftKeyBoard(activity!!)
                (mContext as FragmentActivity).supportFragmentManager.popBackStack()
            }

        })

        distCross.setOnClickListener {
            //  dismiss()
            hideSoftKeyBoard(activity!!)
            (mContext as FragmentActivity).supportFragmentManager.popBackStack()
        }
    }

    interface DistrictClick {
        fun onClick(position: Int, name: String, clickedID: Int)
    }

    private var districtClick: DistrictClick? = null
    fun setOnClick(districtClick: DistrictClick?) {
        this.districtClick = districtClick
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

        districtSuggestAdapter = DistrictSelectAdapter(context!!, sList)
        rvDistrictSuggestion.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = districtSuggestAdapter
        }

        districtSuggestAdapter.setOnClick(object : DistrictSelectAdapter.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                districtClick?.onClick(position, name, clickedID)
                //  dismiss()
                Timber.e("etDistrictSearch 6", " - districtSuggestAdapter clicked")
                hideSoftKeyBoard(activity!!)
                (mContext as FragmentActivity).supportFragmentManager.popBackStack()
            }
        })
    }

    private fun getMatchingString(searchTxt: String): ArrayList<DistrictDeliveryChargePayLoad> {
        sList.clear()
        for (model in districtList) {
            if (model.district != null) {
                if (model.district!!.toLowerCase().contains(searchTxt.toLowerCase())
                    || model.districtBng!!.contains(searchTxt)
                ) {
                    // Timber.e("etDistrictSearch 4 - " + model.district)
                    sList.add(model)
                }
            }
        }
        Timber.e("etDistrictSearch 5 - %s", sList.toString())

        if (sList.size > 0) {
            rvDistrictSuggestion.visibility = View.VISIBLE
            rvSelectDistrict.visibility = View.GONE
        } else {
            rvDistrictSuggestion.visibility = View.GONE
            rvSelectDistrict.visibility = View.VISIBLE
        }
        districtSuggestAdapter.notifyDataSetChanged()

        return sList
    }


}
