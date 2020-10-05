package com.bd.deliverytiger.app.ui.shipment_charges


import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.endpoint.DistrictInterface
import com.bd.deliverytiger.app.api.endpoint.PlaceOrderInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeRequest
import com.bd.deliverytiger.app.api.model.charge.DeliveryChargeResponse
import com.bd.deliverytiger.app.api.model.district.DeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.DistrictDeliveryChargePayLoad
import com.bd.deliverytiger.app.api.model.district.ThanaPayLoad
import com.bd.deliverytiger.app.ui.district.DistrictSelectFragment
import com.bd.deliverytiger.app.ui.district.v2.CustomModel
import com.bd.deliverytiger.app.ui.district.v2.DistrictThanaAriaSelectFragment
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.VariousTask
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class ShipmentChargeFragment : Fragment() {

    private lateinit var districtTV: TextView
    private lateinit var thanaTV: TextView
    private lateinit var calculateBtn: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var chargeRV: RecyclerView

    private lateinit var placeOrderInterface: PlaceOrderInterface
    private val districtList: ArrayList<DistrictDeliveryChargePayLoad> = ArrayList()
    private val thanaOrAriaList: ArrayList<ThanaPayLoad> = ArrayList()
    private var district = 0
    private var thana = 0
    private var ariaPostOffice = 0
    private var isAriaAvailable = true

    private lateinit var dataAdapter: ShipmentChargeAdapter

    companion object {
        fun newInstance(): ShipmentChargeFragment = ShipmentChargeFragment().apply {

        }

        val tag = ShipmentChargeFragment::class.java.name
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shipment_charge, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        districtTV = view.findViewById(R.id.shipment_charge_District)
        thanaTV = view.findViewById(R.id.shipment_charge_Thana)
        calculateBtn = view.findViewById(R.id.shipment_charge_calculate)
        progressBar = view.findViewById(R.id.shipment_charge_progressBar)
        chargeRV = view.findViewById(R.id.shipment_charge_rv)

        placeOrderInterface = RetrofitSingleton.getInstance(mContext).create(PlaceOrderInterface::class.java)

        dataAdapter = ShipmentChargeAdapter()
        with(chargeRV) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
            adapter = dataAdapter
        }

        districtTV.setOnClickListener {
            if (districtList.isEmpty()) {
                getDistrictThanaOrAria(0, 1)
            } else {
                goToDistrict()
            }
        }

        thanaTV.setOnClickListener {
            if (district != 0) {
                getDistrictThanaOrAria(district, 2)
            } else {
                VariousTask.showShortToast(requireContext(), getString(R.string.select_dist))
            }
        }

        calculateBtn.setOnClickListener {

            if (district == 0) {
                showShortToast(requireContext(), getString(R.string.select_dist))
                return@setOnClickListener
            }
            if (thana == 0) {
                showShortToast(requireContext(), getString(R.string.select_thana))
                return@setOnClickListener
            }
            getDeliveryCharge()
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("শিপমেন্ট চার্জ")
    }


    private fun getDistrictThanaOrAria(id: Int, track: Int) {
        VariousTask.hideSoftKeyBoard(requireActivity())
        //track = 1 district , track = 2 thana, track = 3 aria
        val pd = ProgressDialog(context)
        pd.setMessage("Loading")
        pd.show()

        val getDistrictThanaOrAria = RetrofitSingleton.getInstance(mContext).create(DistrictInterface::class.java)
        getDistrictThanaOrAria.getAllDistrictFromApi(id).enqueue(object : Callback<DeliveryChargePayLoad> {
            override fun onFailure(call: Call<DeliveryChargePayLoad>, t: Throwable) {
                Timber.e("districtThanaOrAria_f-", t.toString())
                pd.dismiss()
            }

            override fun onResponse(
                call: Call<DeliveryChargePayLoad>,
                response: Response<DeliveryChargePayLoad>
            ) {
                pd.dismiss()
                if (response.isSuccessful && response.body() != null && response.body()!!.data!!.districtInfo != null) {
                    Timber.e("districtThanaOrAria_s-", response.body().toString())

                    if (track == 1) {
                        districtList.addAll(response.body()!!.data!!.districtInfo!!)
                        goToDistrict()

                    } else if (track == 2) {
                        thanaOrAriaList.clear()
                        thanaOrAriaList.addAll(response.body()!!.data!!.districtInfo!![0].thanaHome!!)
                        if (thanaOrAriaList.isNotEmpty()) {
                            //customAlertDialog(thanaOrAriaList, 1)
                            val mList: ArrayList<CustomModel> = ArrayList()
                            for ((index, model) in thanaOrAriaList.withIndex()) {
                                mList.add(CustomModel(model.thanaId, model.thanaBng + "", model.thana + "", index))
                            }
                            thanaAriaSelect(thanaOrAriaList, 2, mList, "থানা নির্বাচন করুন")
                        }


                    } else if (track == 3) {
                        thanaOrAriaList.clear()
                        thanaOrAriaList.addAll(response.body()!!.data!!.districtInfo!![0].thanaHome!!)
                        if (thanaOrAriaList.isNotEmpty()) {
                            //customAlertDialog(thanaOrAriaList, 2)
                            val mList: ArrayList<CustomModel> = ArrayList()
                            for ((index, model) in thanaOrAriaList.withIndex()) {
                                mList.add(CustomModel(model.thanaId, model.thanaBng + "", model.thana + "", index))
                            }
                            thanaAriaSelect(thanaOrAriaList, 3, mList, "এরিয়া/পোস্ট অফিস নির্বাচন করুন")
                        }
                    }
                }
            }

        })
    }

    private fun goToDistrict() {

        val distFrag = DistrictSelectFragment.newInstance(mContext, districtList)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.setOnClick(object : DistrictSelectFragment.DistrictClick {
            override fun onClick(position: Int, name: String, clickedID: Int) {
                //Timber.e("etDistrictSearch 6 - ",name + " " + clickedID.toString())
                districtTV.text = name
                district = clickedID

                thana = 0
                ariaPostOffice = 0
                thanaTV.text = ""
            }
        })
    }

    private fun thanaAriaSelect(
        thanaOrAriaList: ArrayList<ThanaPayLoad>,
        track: Int,
        list: ArrayList<CustomModel>, title: String
    ) {
        //track = 1 district , track = 2 thana, track = 3 aria
        val distFrag = DistrictThanaAriaSelectFragment.newInstance(mContext, list, title)
        val ft = activity?.supportFragmentManager?.beginTransaction()
        ft?.setCustomAnimations(R.anim.slide_out_up, R.anim.slide_in_up)
        ft?.add(R.id.mainActivityContainer, distFrag, DistrictSelectFragment.tag)
        ft?.addToBackStack(DistrictSelectFragment.tag)
        ft?.commit()

        distFrag.onItemClick = { adapterPosition: Int, name: String, id: Int, listPostion ->
            Timber.e("distFrag1", adapterPosition.toString() + " " + listPostion.toString() + " " + name + " " + id + " " + thanaOrAriaList[listPostion].postalCode + " s")

            if (track == 1) {
                thanaTV.text = districtList[listPostion].districtBng
                district = id
                thanaTV.text = ""
                thana = 0
                ariaPostOffice = 0
            } else if (track == 2) {
                isAriaAvailable = thanaOrAriaList[listPostion].hasArea == 1
                thanaTV.setText(thanaOrAriaList[listPostion].thanaBng)
                thana = thanaOrAriaList[listPostion].thanaId
                ariaPostOffice = 0
                //etAriaPostOffice.setText("")
            } else if (track == 3) {
                if (thanaOrAriaList[listPostion].postalCode != null) {
                    if (thanaOrAriaList[listPostion].postalCode!!.isNotEmpty()) {
                        ariaPostOffice = thanaOrAriaList[listPostion].postalCode?.toInt()!!
                        //etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng + " (" + thanaOrAriaList[listPostion].postalCode + ")")
                    } else {
                        ariaPostOffice = 0
                        // isAriaAvailable = false
                        //etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng )
                    }
                } else {
                    ariaPostOffice = 0
                    //etAriaPostOffice.setText(thanaOrAriaList[listPostion].thanaBng )
                }
            }
        }
    }

    /*private fun customAlertDialog(thanaOrAriaList: ArrayList<ThanaPayLoad>, track: Int){
        VariousTask.hideSoftKeyBoard(activity!!)
        val dialogBuilder = AlertDialog.Builder(mContext)

        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.custom_alert_lay, null)
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        val textViewHead: TextView = dialogView.findViewById(R.id.headerDistrictOrThana)
        val ivDistClose: ImageView = dialogView.findViewById(R.id.ivDistClose)
        val rvListOfThanaOrAria: RecyclerView = dialogView.findViewById(R.id.rvListOfThanaOrAria)

        if(track == 1) textViewHead.text = "থানা নির্বাচন করুন" else textViewHead.text = "এরিয়া/পোস্ট অফিস নির্বাচন করুন"

        val dialog = dialogBuilder.create()
        dialog.show()
        val thanaOrAriaAdapter = ThanaOrAriaAdapter(mContext,thanaOrAriaList)
        rvListOfThanaOrAria.apply {
            layoutManager = LinearLayoutManager(mContext)
            adapter = thanaOrAriaAdapter
        }

        ivDistClose.setOnClickListener {
            dialog.dismiss()
        }

        thanaOrAriaAdapter.setOnClick(object : ThanaOrAriaAdapter.OnClickedListener{
            override fun onClick(pos: Int) {
                dialog.dismiss()
                isAriaAvailable = thanaOrAriaList[pos].hasArea == 1
                Timber.e("isAriaAvailable",isAriaAvailable.toString() +" "+track)
                if (track == 1) {
                    thanaTV.text = thanaOrAriaList[pos].thanaBng
                    thana = thanaOrAriaList[pos].thanaId
                    ariaPostOffice = 0
                    //etAriaPostOffice.setText("")
                } else {
                    if (thanaOrAriaList[pos].postalCode != null) {
                        if (thanaOrAriaList[pos].postalCode!!.isNotEmpty()) {
                            ariaPostOffice = thanaOrAriaList[pos].postalCode?.toInt()!!
                            //etAriaPostOffice.setText(thanaOrAriaList[pos].thanaBng+" ("+thanaOrAriaList[pos].postalCode+")")
                        } else {
                            ariaPostOffice = 0
                            isAriaAvailable = false
                        }
                    }
                }
            }

        })


    }*/

    fun showShortToast(context: Context?, message: String) {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            //toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }
    }

    private fun getDeliveryCharge() {

        progressBar.visibility = View.VISIBLE
        calculateBtn.isEnabled = false

        placeOrderInterface.getDeliveryCharge(DeliveryChargeRequest(district, thana)).enqueue(object : Callback<GenericResponse<List<DeliveryChargeResponse>>> {
            override fun onFailure(call: Call<GenericResponse<List<DeliveryChargeResponse>>>, t: Throwable) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    calculateBtn.isEnabled = true
                }
            }

            override fun onResponse(
                call: Call<GenericResponse<List<DeliveryChargeResponse>>>,
                response: Response<GenericResponse<List<DeliveryChargeResponse>>>
            ) {
                if (isAdded) {
                    progressBar.visibility = View.GONE
                    calculateBtn.isEnabled = true
                }
                if (response.isSuccessful && response.body() != null && isAdded) {
                    if (response.body()!!.model != null) {
                        val model = response.body()!!.model
                        dataAdapter.initLoad(model)
                    }
                }
            }

        })
    }

}
