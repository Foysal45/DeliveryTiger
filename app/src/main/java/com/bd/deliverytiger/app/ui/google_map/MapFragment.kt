package com.bd.deliverytiger.app.ui.google_map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.databinding.FragmentMapBinding
import com.bd.deliverytiger.app.ui.home.HomeActivity
import com.bd.deliverytiger.app.ui.home.HomeViewModel
import com.bd.deliverytiger.app.utils.PloyDecoder
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
    private var currentLatitude = 0.0
    private var currentLongitude = 0.0

    private var collectorLatLng: LatLng = LatLng(23.7945821,90.3451037)
    private val polyDecoder = PloyDecoder()
    private var isLoading: Boolean = false

    private val homeViewModel: HomeViewModel by inject()
    private val viewModel: MapViewModel by inject()

    companion object {
        fun newInstance(): MapFragment = MapFragment()
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
            (activity as HomeActivity).setToolbarTitle("কালেক্টর ম্যাপ")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initMap()

        homeViewModel.currentLocation.observe(viewLifecycleOwner, Observer {

            currentLocation = it
            currentLatitude = it.latitude
            currentLongitude = it.longitude
            val currentLatLng = LatLng(currentLatitude, currentLongitude)
            Timber.d("debugMap currentLocation $currentLatitude $currentLongitude")
            map?.clear()
            map?.addMarker(
                MarkerOptions().position(currentLatLng)
                    .title("আপনার বর্তমান অবস্থান").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_marker_person))
            )
            map?.addMarker(
                MarkerOptions().position(collectorLatLng)
                    .title("কালেক্টর").icon(bitmapDescriptorFromVector(requireContext(), R.drawable.ic_bike))
            )
            val bound = LatLngBounds.builder().include(currentLatLng).include(collectorLatLng).build()
            val cameraUpdateBounds = CameraUpdateFactory.newLatLngBounds(bound, 100)
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 13.0f)
            map?.moveCamera(cameraUpdateBounds)
            fetchRoutingDetails()
        })
    }

    private fun initMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        map?.let { mMap ->
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(23.4956324,88.1007368), 7f))
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

    private fun fetchRoutingDetails() {

        if (isLoading) return
        isLoading = true
        viewModel.fetchRoutingDetails("$currentLongitude,$currentLatitude", "${collectorLatLng.longitude},${collectorLatLng.latitude}").observe(viewLifecycleOwner, Observer { model ->
            isLoading = false
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

}