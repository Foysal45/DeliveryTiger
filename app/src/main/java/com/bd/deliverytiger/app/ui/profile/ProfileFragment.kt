package com.bd.deliverytiger.app.ui.profile

import android.Manifest.permission
import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.RetrofitSingleton
import com.bd.deliverytiger.app.api.`interface`.ProfileUpdateInterface
import com.bd.deliverytiger.app.api.model.GenericResponse
import com.bd.deliverytiger.app.api.model.login.LoginResponse
import com.bd.deliverytiger.app.api.model.profile_update.ProfileUpdateReqBody
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.Timber
import com.bd.deliverytiger.app.utils.Validator
import com.bd.deliverytiger.app.utils.VariousTask
import com.bd.deliverytiger.app.utils.VariousTask.getCircularImage
import com.bd.deliverytiger.app.utils.VariousTask.saveImage
import com.bd.deliverytiger.app.utils.VariousTask.scaledBitmapImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    companion object {
        fun newInstance(): ProfileFragment = ProfileFragment().apply { }
        val tag = ProfileFragment::class.java.name
    }

    private lateinit var emailPassLay: LinearLayout
    private lateinit var updateInfoLay: LinearLayout
    private lateinit var etCustomerName: EditText
    private lateinit var etAddOrderMobileNo: EditText
    private lateinit var etAlternativeMobileNo: EditText
    private lateinit var etBikashMobileNo: EditText
    private lateinit var etEmailAddress: EditText
    private lateinit var etProductCollectionAddress: EditText
    private lateinit var checkSmsUpdate: CheckBox
    private lateinit var btnSaveProfile: Button
    private lateinit var ivProfileImage: ImageView
    private lateinit var ivEditProfileImage: ImageView

    private var customerName = ""
    private var mobileNo = ""
    private var alternativeMobileNo = ""
    private var bikashMobileNo = ""

    private var productCollectionAddress = ""
    private var emailAddress = ""
    private val GALLERY_REQ_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateInfoLay = view.findViewById(R.id.updateInfoLay)
        emailPassLay = view.findViewById(R.id.emailPassLay)
        etCustomerName = view.findViewById(R.id.etCustomerName)
        etAddOrderMobileNo = view.findViewById(R.id.etAddOrderMobileNo)
        etAlternativeMobileNo = view.findViewById(R.id.etAlternativeMobileNo)
        etBikashMobileNo = view.findViewById(R.id.etBikashMobileNo)
        etEmailAddress = view.findViewById(R.id.etEmailAddress)
        etProductCollectionAddress = view.findViewById(R.id.etProductCollectionAddress)
        checkSmsUpdate = view.findViewById(R.id.checkSmsUpdate)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)
        ivProfileImage = view.findViewById(R.id.ivProfileImage)
        ivEditProfileImage = view.findViewById(R.id.ivEditProfileImage)


        sunSetUserStoredData()

        btnSaveProfile.setOnClickListener {
            if (validate()) {
                updateProfile()
            }
        }

        ivEditProfileImage.setOnClickListener {
            getImageFromDevice()
        }

        if (SessionManager.profileImgUri.isNotEmpty()) {
            setProfileImgUrl(SessionManager.profileImgUri)
        }

    }


    private fun validate(): Boolean {
        var go = true
        getAllViewData()
        if (customerName.isEmpty()) {
            VariousTask.showShortToast(context, getString(R.string.write_yr_name))
            go = false
            etCustomerName.requestFocus()
        } else if (mobileNo.isEmpty()) {
            VariousTask.showShortToast(context, getString(R.string.write_phone_number))
            go = false
            etAddOrderMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(mobileNo) || mobileNo.length < 11) {
            VariousTask.showShortToast(
                context,
                getString(R.string.write_proper_phone_number_recharge)
            )
            go = false
            etAddOrderMobileNo.requestFocus()
        } else if (bikashMobileNo.isEmpty()) {
            VariousTask.showShortToast(context, getString(R.string.write_phone_number))
            go = false
            etBikashMobileNo.requestFocus()
        } else if (!Validator.isValidMobileNumber(bikashMobileNo) || bikashMobileNo.length < 11) {
            VariousTask.showShortToast(
                context,
                getString(R.string.write_proper_phone_number_recharge)
            )
            go = false
            etBikashMobileNo.requestFocus()
        } else if (emailAddress.isEmpty()) {
            go = false
            VariousTask.showShortToast(context!!, getString(R.string.write_email_address))
            etEmailAddress.requestFocus()
        } else if (productCollectionAddress.isEmpty()) {
            go = false
            VariousTask.showShortToast(context!!, getString(R.string.write_yr_address))
            etProductCollectionAddress.requestFocus()
        }
        VariousTask.hideSoftKeyBoard(activity!!)
        return go
    }

    private fun getAllViewData() {
        customerName = etCustomerName.text.toString()
        mobileNo = etAddOrderMobileNo.text.toString()
        alternativeMobileNo = etAlternativeMobileNo.text.toString()
        productCollectionAddress = etProductCollectionAddress.text.toString()
        bikashMobileNo = etBikashMobileNo.text.toString()
        emailAddress = etEmailAddress.text.toString()
    }

    private fun updateProfile() {

        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("অপেক্ষা করুন")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val profileUpdateInterface =
            RetrofitSingleton.getInstance(context!!).create(ProfileUpdateInterface::class.java)

        val proUpdateReqBody = ProfileUpdateReqBody(
            SessionManager.courierUserId,
            customerName,
            mobileNo,
            alternativeMobileNo,
            emailAddress,
            bikashMobileNo,
            productCollectionAddress,
            checkSmsUpdate.isChecked
        )

        profileUpdateInterface.updateMerchantInformation(
            SessionManager.courierUserId,
            proUpdateReqBody
        ).enqueue(object :
            Callback<GenericResponse<LoginResponse>> {
            override fun onFailure(call: Call<GenericResponse<LoginResponse>>, t: Throwable) {
                Timber.e("updateProfile f ", t.toString())
                progressDialog.dismiss()
            }

            override fun onResponse(
                call: Call<GenericResponse<LoginResponse>>,
                response: Response<GenericResponse<LoginResponse>>
            ) {
                progressDialog.dismiss()
                if (response.isSuccessful && response.body() != null && response.body()?.model != null) {
                    Timber.e("updateProfile f ", response.body().toString())
                    VariousTask.showShortToast(context, getString(R.string.update_success))
                    SessionManager.updateSession(proUpdateReqBody)
                    activity?.onBackPressed()
                }
            }

        })

    }

    private fun sunSetUserStoredData() {

        val model = SessionManager.getSessionData()
        etCustomerName.setText(model.userName)
        etAddOrderMobileNo.setText(model.mobile)
        etAlternativeMobileNo.setText(model.alterMobile)
        etProductCollectionAddress.setText(model.address)
        etBikashMobileNo.setText(model.bkashNumber)
        etEmailAddress.setText(model.emailAddress)

        checkSmsUpdate.isChecked = model.isSms!!
    }

    private fun setProfileImgUrl(imageUri: String?) {
        Timber.d("HomeActivityLog 1 ", SessionManager.profileImgUri)
        try {
            val imgFile = File(imageUri+"");
            val myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ivProfileImage.setImageDrawable(getCircularImage(context, myBitmap))

        } catch (e: Exception) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQ_CODE) {
            val imageStream: InputStream =
                activity?.contentResolver?.openInputStream(Uri.parse(data?.data.toString()))!!
            val scImg = scaledBitmapImage(BitmapFactory.decodeStream(imageStream))
            ivProfileImage.setImageDrawable(getCircularImage(context, scImg))

            SessionManager.profileImgUri = saveImage(scImg)
        }
    }

    private fun getImageFromDevice() {
        if (ContextCompat.checkSelfPermission(
                activity!!,
                permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity!!,
                permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf<String?>(
                    permission.READ_EXTERNAL_STORAGE,
                    permission.WRITE_EXTERNAL_STORAGE
                ),
                102
            )
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, GALLERY_REQ_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as HomeActivity).setToolbarTitle("এডিট প্রোফাইল")
    }
}
