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
    private val collectorList: MutableList<LatLng> = mutableListOf(LatLng(23.7945821, 90.3451037), LatLng(23.7569087, 90.3904403), LatLng(23.7388383, 90.3957391), LatLng(23.7323596, 90.4046096))

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
        if (activity is HomeActivity) {
            (activity as HomeActivity).setToolbarTitle("কালেক্টর ট্র্যাকিং")
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
            map?.addMarker(
                MarkerOptions().position(currentLatLng)
                    .title("আপনার বর্তমান অবস্থান").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_marker_person))
            )
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
            val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 100)
            map?.moveCamera(cameraUpdateBounds)

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
                if (pickUpLocationList.size > 1) {
                    choosePickupLocation(pickUpLocationList)
                    binding?.pickUpBtn?.visibility = View.VISIBLE
                } else {
                    onPickUpLocation(list.first())
                }
            } else {
                context?.toast("প্রোফাইলে পিকআপ লোকেশান অ্যাড করুন")
            }
        })
    }

    private fun choosePickupLocation(list: List<PickupLocation>) {

        val tag = PickupLocationBottomSheet.tag
        val dialog = PickupLocationBottomSheet.newInstance(list)
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { model ->
            dialog.dismiss()
            onPickUpLocation(model)
        }
    }

    private fun onPickUpLocation(pickUpModel: PickupLocation) {
        if (isNearByHubView) {
            showNearbyHubs(pickUpModel)
        } else {
            fetchCollectorListForPickUp(pickUpModel)
        }
    }

    private fun showNearbyHubs(pickUpModel: PickupLocation) {

        viewModel.fetchHubByPickupLocation(pickUpModel).observe(viewLifecycleOwner, Observer { hubModel ->

            val bound = LatLngBounds.builder().include(currentLatLng)
            val hubName = hubModel?.name ?: "DT Hub"
            val address = hubModel?.hubAddress ?: ""
            var hubLatLng: LatLng? = null
            if (!hubModel?.latitude.isNullOrEmpty() && !hubModel?.longitude.isNullOrEmpty()) {
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
            }

            val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 100)
            map?.moveCamera(cameraUpdateBounds)
        })

    }

    private fun fetchCollectorListForPickUp(pickUpModel: PickupLocation) {
        showCollectorsInMap(collectorList)
    }

    private fun showCollectorsInMap(list: List<LatLng>) {

        val bound = LatLngBounds.builder()
            .include(currentLatLng)
        list.forEach { latLan ->
            bound.include(latLan)
            map?.addMarker(
                MarkerOptions()
                    .position(latLan)
                    .title("কালেক্টর")
                    .icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_bike))
            )
        }

        val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound.build(), 100)
        map?.moveCamera(cameraUpdateBounds)
        //val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.0f)

        collectorLatLng = list.first()
        fetchRoutingDetails("$currentLongitude,$currentLatitude", "${collectorLatLng.longitude},${collectorLatLng.latitude}")
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

}