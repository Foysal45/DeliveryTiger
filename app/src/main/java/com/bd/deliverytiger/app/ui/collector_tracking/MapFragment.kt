package com.bd.deliverytiger.app.ui.collector_tracking

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.cod_collection.HubInfo
import com.bd.deliverytiger.app.api.model.pickup_location.PickupLocation
import com.bd.deliverytiger.app.api.model.rider.RiderInfo
import com.bd.deliverytiger.app.api.model.tracking.DistanceMapping
import com.bd.deliverytiger.app.databinding.FragmentMapBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.utils.PloyDecoder
import com.bd.deliverytiger.app.utils.SessionManager
import com.bd.deliverytiger.app.utils.toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class MapFragment : Fragment(), OnMapReadyCallback {

    private var binding: FragmentMapBinding? = null
    private var map: GoogleMap? = null

    private var currentLocation: Location? = null
    private var currentLatLng: LatLng = LatLng(0.0, 0.0)
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private var collectorLatLng: LatLng = LatLng(23.7945821, 90.3451037)
    private val polyDecoder = PloyDecoder()

    private val pickUpLocationList: MutableList<PickupLocation> = mutableListOf()
    //private val collectorList: MutableList<LatLng> = mutableListOf(LatLng(23.7945821, 90.3451037), LatLng(23.7569087, 90.3904403), LatLng(23.7388383, 90.3957391), LatLng(23.7323596, 90.4046096))

    private var bundle: Bundle? = null
    private var isHubView: Boolean = false
    private var isNearByHubView: Boolean = false
    private var hubModel: HubInfo? = null
    private var mobileNumber: String = ""

    private val homeViewModel: HomeViewModel by inject()
    private val viewModel: MapViewModel by inject()

    companion object {
        fun newInstance(bundle: Bundle?): MapFragment = MapFragment().apply {
            this.bundle = bundle
        }
        val tag: String = MapFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentMapBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onResume() {
        super.onResume()
        val title = when {
            isHubView -> "হাব ট্র্যাকিং"
            isNearByHubView -> "হাব ট্র্যাকিং"
            else -> "কালেক্টর ট্র্যাকিং"
        }
        if (activity is HomeActivity) {
            (activity as HomeActivity).setToolbarTitle(title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isHubView = bundle?.getBoolean("hubView", false) ?: false
        isNearByHubView = bundle?.getBoolean("isNearByHubView", false) ?: false
        hubModel = bundle?.getParcelable("hubModel")

        initMap()

        homeViewModel.currentLocation.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            currentLocation = it
            currentLatitude = it.latitude
            currentLongitude = it.longitude
            currentLatLng = LatLng(currentLatitude, currentLongitude)
            Timber.d("debugMap currentLocation $currentLatitude $currentLongitude")
            map?.clear()
            showCurrentMarker()
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.0f)
            map?.moveCamera(cameraUpdate)

            when {
                isHubView -> showHubLocation()
                isNearByHubView -> fetchPickUpLocation()
                else -> fetchPickUpLocation()
            }

            homeViewModel.currentLocation.value = null
        })

        binding?.pickUpBtn?.setOnClickListener {
            choosePickupLocation(pickUpLocationList)
        }

        binding?.callBtn?.setOnClickListener {
            callNumber(mobileNumber)
        }
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.let { mMap ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(23.4956324, 88.1007368), 7f))
            with(mMap.uiSettings) {
                isZoomControlsEnabled = false
                isMyLocationButtonEnabled = true
                isMapToolbarEnabled = false
                setAllGesturesEnabled(true)
                isCompassEnabled = true
            }
            with(mMap) {
                isTrafficEnabled = false
                isIndoorEnabled = false
                isBuildingsEnabled = true
            }
            (activity as HomeActivity).fetchCurrentLocation()

        }
    }

    private fun showHubLocation() {

        hubModel ?: return

        mobileNumber = hubModel?.hubMobile ?: ""
        val hubName = hubModel?.name ?: "DT Hub"
        val address = hubModel?.hubAddress ?: ""
        val hubAddress = "$hubName\n$address"
        var hubLatLng: LatLng? = null
        if (!hubModel?.latitude.isNullOrEmpty() && !hubModel?.longitude.isNullOrEmpty()) {
            hubLatLng = LatLng(hubModel?.latitude?.toDouble() ?: 0.0, hubModel?.longitude?.toDouble() ?: 0.0)
        }

        if (mobileNumber.trim().isNotEmpty()) {
            binding?.callBtn?.visibility = View.VISIBLE
            binding?.msg?.text = "কালেকশন হাবে\nফোন করুন"
        }

        if (hubLatLng != null) {
            val hubMarker = map?.addMarker(
                MarkerOptions()
                    .position(hubLatLng)
                    .title(hubName)
                    .snippet(address)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_home_circle))
            )
            hubMarker?.showInfoWindow()

            val bound = LatLngBounds.builder()
                .include(currentLatLng)
                .include(hubLatLng)

            try {
                val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 200)
                map?.moveCamera(cameraUpdateBounds)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            fetchRoutingDetails("$currentLongitude,$currentLatitude", "${hubLatLng.longitude},${hubLatLng.latitude}")
        }

    }

    private fun fetchRoutingDetails(startLngLat: String, endLngLat: String) {

        viewModel.fetchRoutingDetails(startLngLat, endLngLat).observe(viewLifecycleOwner, Observer { model ->

            if (!model.routes.isNullOrEmpty()) {
                val route = model.routes!!.first()
                route.geometry?.let { geometry ->

                    val polylineOptions = PolylineOptions().width(5f).color(Color.BLACK)
                    val list = polyDecoder.decode(geometry)
                    list.forEach { latLng ->
                        polylineOptions.add(latLng)
                    }
                    map?.addPolyline(polylineOptions)
                }
            }
        })
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun bitmapDescriptorFromVector(context: Context, @DrawableRes id: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, id)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    private fun fetchPickUpLocation() {

        viewModel.getPickupLocations(SessionManager.courierUserId).observe(viewLifecycleOwner, Observer { list ->
            if (list.isNotEmpty()) {
                pickUpLocationList.clear()
                pickUpLocationList.addAll(list)
                if (!isNearByHubView) {
                    val filterList = pickUpLocationList.filter { it.acceptedOrderCount > 0 }
                    if (filterList.isNullOrEmpty()) {
                        context?.toast("এই মুহূর্তে কোনো কালেক্টর নিযুক্ত করা হয়নি")
                        return@Observer
                    }
                }
                if (isNearByHubView) {
                    /*pickUpLocationList.add(0,
                        PickupLocation(thanaName = "",
                            pickupAddress = "আপনার বর্তমান অবস্থানের নিকটস্থ হাব",
                            latitude = currentLatitude.toString(),
                            longitude = currentLongitude.toString()))*/

                    choosePickupLocation(pickUpLocationList)
                    binding?.pickUpBtn?.visibility = View.VISIBLE
                } else {
                    if (pickUpLocationList.size > 1) {
                        choosePickupLocation(pickUpLocationList)
                        binding?.pickUpBtn?.visibility = View.VISIBLE
                    } else {
                        onPickUpLocation(list.first())
                    }
                }
            } else {
                context?.toast("প্রোফাইলে পিকআপ লোকেশন অ্যাড করুন")
            }
        })
    }

    private fun choosePickupLocation(list: List<PickupLocation>) {

        val tag = PickupLocationBottomSheet.tag
        val dialog = PickupLocationBottomSheet.newInstance(list, isNearByHubView)
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { model ->
            dialog.dismiss()
            onPickUpLocation(model)
        }
        dialog.onNearByHubClicked = {
            dialog.dismiss()
            val model = PickupLocation(thanaName = "",
                pickupAddress = "আপনার বর্তমান অবস্থানের নিকটস্থ হাব",
                latitude = currentLatitude.toString(),
                longitude = currentLongitude.toString())
            onPickUpLocation(model)
        }
    }

    private fun onPickUpLocation(pickUpModel: PickupLocation) {

        //Test
        /*pickUpModel.apply {
            //districtId = 14
            //thanaId = 15945
            //courierUserId = 31517
            latitude = "23.7501826"
            longitude = "90.3905834"
        }*/
        if (isNearByHubView) {
            showNearbyHubs(pickUpModel)
        } else {
            fetchCollectorListForPickUp(pickUpModel)
        }
    }

    private fun showNearbyHubs(pickUpModel: PickupLocation) {

        if (pickUpModel.id == 0) {
            viewModel.fetchAllHubInfo().observe(viewLifecycleOwner, Observer { list ->

                val distanceList: MutableList<DistanceMapping> = mutableListOf()
                list.forEach { hubInfo ->
                    val distance = floatArrayOf(0.0f)
                    Location.distanceBetween(currentLatitude, currentLongitude, hubInfo.latitude?.toDoubleOrNull() ?: 0.0, hubInfo.longitude?.toDoubleOrNull() ?: 0.0, distance)
                    if (distance.isNotEmpty()) {
                        distanceList.add(DistanceMapping(distance.first(), hubInfo))
                    }
                }
                distanceList.sortBy { it.distance }
                Timber.d("distanceList $distanceList")
                if (distanceList.isNotEmpty()) {
                    drawHubLocationOnMap(pickUpModel, distanceList.first().hubModel)
                }

                // Show all hub location on map
                /*val bound = LatLngBounds.builder()
                distanceList.forEach {
                    val hubModel = it.hubModel
                    showCurrentMarker()
                    val hubName = hubModel?.name ?: "DT Hub"
                    val address = hubModel?.hubAddress ?: ""
                    var hubLatLng: LatLng? = null
                    if (isValidCoordinate(hubModel?.latitude) && isValidCoordinate(hubModel?.longitude)) {
                        hubLatLng = LatLng(hubModel.latitude?.toDouble() ?: 0.0, hubModel?.longitude?.toDouble() ?: 0.0)
                    }
                    val hubMarker = map?.addMarker(
                        MarkerOptions()
                            .position(hubLatLng!!)
                            .title(hubName)
                            .snippet(address)
                            .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_home_circle))
                    )
                    hubMarker?.showInfoWindow()
                    bound.include(hubLatLng)
                }
                try {
                    val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 200)
                    map?.moveCamera(cameraUpdateBounds)
                } catch (e: Exception) {
                    e.printStackTrace()
                }*/

            })
        } else {
            viewModel.fetchHubByPickupLocation(pickUpModel).observe(viewLifecycleOwner, Observer { hubModel ->
                drawHubLocationOnMap(pickUpModel, hubModel)
            })
        }

    }

    private fun drawHubLocationOnMap(pickUpModel: PickupLocation, hubModel: HubInfo) {
        map?.clear()
        showCurrentMarker()
        val bound = LatLngBounds.builder()//.include(currentLatLng)
        val hubName = hubModel?.name ?: "DT Hub"
        val address = hubModel?.hubAddress ?: ""
        mobileNumber = hubModel.hubMobile ?: ""
        if (mobileNumber.trim().isNotEmpty()) {
            binding?.callBtn?.visibility = View.VISIBLE
            binding?.msg?.text = "কালেকশন হাবে\nফোন করুন"
        }

        if (isValidCoordinate(pickUpModel.latitude) && isValidCoordinate(pickUpModel.longitude)) {
            val pickupLatLng = LatLng(pickUpModel.latitude.toDouble(), pickUpModel.longitude.toDouble())
            val pickupMarker = map?.addMarker(
                MarkerOptions()
                    .position(pickupLatLng)
                    .title(pickUpModel.pickupAddress)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_location1))
            )
            bound.include(pickupLatLng)
        } else {
            context?.toast("প্রোফাইলে পিকআপ জিপিএস লোকেশন অ্যাড করুন")
        }

        var hubLatLng: LatLng? = null
        if (isValidCoordinate(hubModel?.latitude) && isValidCoordinate(hubModel?.longitude)) {
            hubLatLng = LatLng(hubModel.latitude?.toDouble() ?: 0.0, hubModel?.longitude?.toDouble() ?: 0.0)
        }
        if (hubLatLng != null) {
            val hubMarker = map?.addMarker(
                MarkerOptions()
                    .position(hubLatLng)
                    .title(hubName)
                    .snippet(address)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_home_circle))
            )
            hubMarker?.showInfoWindow()
            bound.include(hubLatLng)

            if (isValidCoordinate(pickUpModel.longitude) && isValidCoordinate(pickUpModel.latitude)) {
                fetchRoutingDetails("${pickUpModel.longitude},${pickUpModel.latitude}", "${hubLatLng.longitude},${hubLatLng.latitude}")
            }
        }

        try {
            val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 200)
            map?.moveCamera(cameraUpdateBounds)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fetchCollectorListForPickUp(pickUpModel: PickupLocation) {

        viewModel.fetchRiderByPickupLocation(pickUpModel).observe(viewLifecycleOwner, Observer { list ->
            showCollectorsInMap(list, pickUpModel)
        })
    }

    private fun showCollectorsInMap(list: List<RiderInfo>, pickUpModel: PickupLocation) {

        val bound = LatLngBounds.builder()//.include(currentLatLng)
        map?.clear()
        showCurrentMarker()
        if (isValidCoordinate(pickUpModel.latitude) && isValidCoordinate(pickUpModel.longitude)) {
            val pickupLatLng = LatLng(pickUpModel.latitude.toDouble(), pickUpModel.longitude.toDouble())
            val pickupMarker = map?.addMarker(
                MarkerOptions()
                    .position(pickupLatLng)
                    .title(pickUpModel.pickupAddress)
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_home_circle))
            )
            bound.include(pickupLatLng)
        } else {
            context?.toast("প্রোফাইলে পিকআপ জিপিএস লোকেশন অ্যাড করুন")
        }

        list.forEach { model ->

            val name = model?.name ?: "Collector"
            val mobile = model?.mobile ?: ""
            var hubLatLng: LatLng? = null
            if (isValidCoordinate(model.latitude) && isValidCoordinate(model.longitude)) {
                hubLatLng = LatLng(model.latitude?.toDouble() ?: 0.0, model.longitude?.toDouble() ?: 0.0)
            }
            if (hubLatLng != null) {
                val hubMarker = map?.addMarker(
                    MarkerOptions()
                        .position(hubLatLng)
                        .title(name)
                        .snippet(mobile)
                        .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_bike))
                )
                hubMarker?.showInfoWindow()
                bound.include(hubLatLng)
            }
        }

        try {
            val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 200)
            map?.moveCamera(cameraUpdateBounds)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.0f)

        if (list.isNotEmpty()) {
            val firstModel = list.first()
            mobileNumber = firstModel.mobile ?: ""
            if (mobileNumber.trim().isNotEmpty()) {
                binding?.callBtn?.visibility = View.VISIBLE
                binding?.msg?.text = "কালেক্টরকে\nফোন করুন"
            }
            /*if (isValidCoordinate(firstModel.latitude) && isValidCoordinate(firstModel.longitude)) {
                collectorLatLng = LatLng(firstModel.latitude?.toDouble() ?: 0.0, firstModel.longitude?.toDouble() ?: 0.0)
                if (isValidCoordinate(pickUpModel.longitude) && isValidCoordinate(pickUpModel.latitude)) {
                    fetchRoutingDetails("${pickUpModel.longitude},${pickUpModel.latitude}", "${collectorLatLng.longitude},${collectorLatLng.latitude}")
                }
            }*/
        } else {
            context?.toast("এই মুহূর্তে কোনো কালেক্টর নিযুক্ত করা হয়নি")
        }

    }

    private fun showCurrentMarker() {
        map?.addMarker(
            MarkerOptions().position(currentLatLng)
                .title("আপনার বর্তমান অবস্থান").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_marker_person))
        )
    }

    private fun callNumber(number: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } catch (e: Exception) {
            requireContext().toast("Could not find an activity to place the call")
        }
    }

    private fun isValidCoordinate(coordinate: String?): Boolean {
        if (coordinate.isNullOrEmpty()) return false
        if (coordinate.trim().isEmpty()) return false
        if (coordinate == "0.0") return false
        if (coordinate == "0") return false
        return true
    }

}