package com.rtchagas.pingplacepicker.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.R
import com.rtchagas.pingplacepicker.helper.PermissionsHelper
import com.rtchagas.pingplacepicker.inject.PingKoinComponent
import com.rtchagas.pingplacepicker.inject.PingKoinContext
import com.rtchagas.pingplacepicker.viewmodel.PlacePickerViewModel
import com.rtchagas.pingplacepicker.viewmodel.Resource
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_place_picker.*
import org.jetbrains.anko.toast
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.abs


class PlacePickerActivity : AppCompatActivity(), PingKoinComponent,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        PlaceConfirmDialogFragment.OnPlaceConfirmedListener {

    companion object {

        private const val TAG = "Ping#PlacePicker"

        // For passing extra parameters to this activity.
        const val EXTRA_LOCATION = "extra_location"

        // Keys for storing activity state.
        private const val STATE_CAMERA_POSITION = "state_camera_position"
        private const val STATE_LOCATION = "state_location"

        private const val AUTOCOMPLETE_REQUEST_CODE = 1001

        private const val DIALOG_CONFIRM_PLACE_TAG = "dialog_place_confirm"
    }

    private var googleMap: GoogleMap? = null

    private var isLocationPermissionGranted = false

    private var cameraPosition: CameraPosition? = null

    private val defaultLocation = LatLng(37.4219999, -122.0862462)

    private var defaultZoom = -1f

    private var lastKnownLocation: LatLng? = null

    private var maxLocationRetries: Int = 3

    private var placeAdapter: PlacePickerAdapter? = null

    private val viewModel: PlacePickerViewModel by viewModel()

    private val disposables = CompositeDisposable()

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_picker)

        // Check if PING was killed for some reason.
        // If so, should restart the activity and init everything again.
        if (PingKoinContext.koinApp == null) {
            finish()
            return
        }

        // Configure the toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Check whether a pre-defined location was set.
        intent.getParcelableExtra<LatLng?>(EXTRA_LOCATION)?.let {
            lastKnownLocation = it
        }

        // Retrieve location and camera position from saved instance state.
        lastKnownLocation = savedInstanceState
                ?.getParcelable(STATE_LOCATION) ?: lastKnownLocation
        cameraPosition = savedInstanceState
                ?.getParcelable(STATE_CAMERA_POSITION) ?: cameraPosition

        // Construct a FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Sets the default zoom
        defaultZoom = resources.getInteger(R.integer.default_zoom).toFloat()

        // Initialize the UI
        initializeUi()

        // Restore any active fragment
        restoreFragments()

        // Initializes the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == AUTOCOMPLETE_REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
            data?.run {
                val place = Autocomplete.getPlaceFromIntent(this)
                showConfirmPlacePopup(place)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_place_picker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (android.R.id.home == item.itemId) {
            finish()
            return true
        }

        if (R.id.action_search == item.itemId) {
            requestPlacesSearch()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_CAMERA_POSITION, googleMap?.cameraPosition)
        outState.putParcelable(STATE_LOCATION, lastKnownLocation)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.clear()
    }

    override fun onMapReady(map: GoogleMap?) {
        googleMap = map
        map?.setOnMarkerClickListener(this)
        checkForPermission()
    }

    override fun onMarkerClick(marker: Marker): Boolean {

        val place = marker.tag as Place
        showConfirmPlacePopup(place)

        return !resources.getBoolean(R.bool.auto_center_on_marker_click)
    }

    override fun onPlaceConfirmed(place: Place) {
        val data = Intent()
        data.putExtra(PingPlacePicker.EXTRA_PLACE, place)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun bindPlaces(places: List<Place>) {

        // Bind to the recycler view

        if (placeAdapter == null) {
            placeAdapter = PlacePickerAdapter(places) { showConfirmPlacePopup(it) }
        }
        else {
            placeAdapter?.swapData(places)
        }

        rvNearbyPlaces.adapter = placeAdapter

        // Bind to the map

        googleMap?.run {

            clear()

            for (place in places) {

                val marker: Marker = addMarker(MarkerOptions()
                        .position(place.latLng!!)
                        .icon(getPlaceMarkerBitmap(place)))

                marker.tag = place
            }
        }
    }

    private fun checkForPermission() {

        PermissionsHelper.checkForLocationPermission(this, object : BasePermissionListener() {

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                isLocationPermissionGranted = false
                initMap()
            }

            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                isLocationPermissionGranted = true
                initMap()
            }
        })
    }

    private fun getCurrentLatLngBounds(): LatLngBounds {

        val radius = resources.getInteger(R.integer.autocomplete_search_bias_radius).toDouble()
        val location: LatLng = lastKnownLocation ?: defaultLocation

        val northEast: LatLng = SphericalUtil.computeOffset(location, radius, 45.0)
        val southWest: LatLng = SphericalUtil.computeOffset(location, radius, 225.0)

        return LatLngBounds(southWest, northEast)
    }

    private fun getDeviceLocation(animate: Boolean) {

        // Get the best and most recent location of the device, which may be null in rare
        // cases when a location is not available.

        try {
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult
                    ?.addOnFailureListener(this) { setDefaultLocation() }
                    ?.addOnSuccessListener(this) { location: Location? ->

                        // In rare cases location may be null...
                        if (location == null) {
                            if (maxLocationRetries > 0) {
                                maxLocationRetries--
                                Handler().postDelayed({ getDeviceLocation(animate) }, 1000)
                            }
                            else {
                                // Location is not available. Give up...
                                setDefaultLocation()
                                Snackbar.make(coordinator,
                                        R.string.picker_location_unavailable,
                                        Snackbar.LENGTH_INDEFINITE)
                                        .setAction(R.string.places_try_again) {
                                            getDeviceLocation(animate)
                                        }
                                        .show()
                            }
                            return@addOnSuccessListener
                        }

                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = LatLng(location.latitude, location.longitude)

                        val update = CameraUpdateFactory
                                .newLatLngZoom(lastKnownLocation, defaultZoom)

                        if (animate) {
                            googleMap?.animateCamera(update)
                        }
                        else {
                            googleMap?.moveCamera(update)
                        }

                        // Load the places near this location
                        loadNearbyPlaces()
                    }
        } catch (e: SecurityException) {
            Log.e(TAG, e.message)
        }
    }

    @Suppress("DEPRECATION")
    private fun getPlaceMarkerBitmap(place: Place): BitmapDescriptor {

        val innerIconSize: Int = resources.getDimensionPixelSize(R.dimen.marker_inner_icon_size)

        val bgDrawable = ResourcesCompat.getDrawable(resources,
                R.drawable.ic_map_marker_solid_red_32dp, null)!!

        val fgDrawable = ResourcesCompat.getDrawable(resources,
                UiUtils.getPlaceDrawableRes(this, place), null)!!
        DrawableCompat.setTint(fgDrawable, resources.getColor(R.color.colorMarkerInnerIcon))

        val bitmap = Bitmap.createBitmap(bgDrawable.intrinsicWidth,
                bgDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)

        bgDrawable.setBounds(0, 0, canvas.width, canvas.height)

        val left = (canvas.width - innerIconSize) / 2
        val top = (canvas.height - innerIconSize) / 3
        val right = left + innerIconSize
        val bottom = top + innerIconSize

        fgDrawable.setBounds(left, top, right, bottom)

        bgDrawable.draw(canvas)
        fgDrawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun handlePlaceByLocation(result: Resource<Place?>) {

        when (result.status) {
            Resource.Status.LOADING -> {
                pbLoading.show()
            }
            Resource.Status.SUCCESS -> {
                result.data?.run { showConfirmPlacePopup(this) }
                pbLoading.hide()
            }
            Resource.Status.ERROR -> {
                toast(R.string.picker_load_this_place_error)
                pbLoading.hide()
            }
            Resource.Status.NO_DATA -> {
                Log.d(TAG, "No places data found...")
            }
        }

    }

    private fun handlePlacesLoaded(result: Resource<List<Place>>) {

        when (result.status) {
            Resource.Status.LOADING -> {
                pbLoading.show()
            }
            Resource.Status.SUCCESS -> {
                bindPlaces((result.data ?: listOf()))
                pbLoading.hide()
            }
            Resource.Status.ERROR -> {
                toast(R.string.picker_load_places_error)
                pbLoading.hide()
            }
            Resource.Status.NO_DATA -> {
                Log.d(TAG, "No places data found...")
            }
        }
    }

    private fun initializeUi() {

        // Initialize the recycler view
        rvNearbyPlaces.layoutManager = LinearLayoutManager(this)

        // Bind the click listeners
        disposables.addAll(
                btnMyLocation.onclick { getDeviceLocation(true) },
                btnRefreshLocation.onclick { refreshNearbyPlaces() },
                cardSearch.onclick { requestPlacesSearch() },
                mapContainer.onclick { selectThisPlace() }
        )

        // Hide or show the refresh places button according to nearby search flag
        btnRefreshLocation.isVisible = PingPlacePicker.isNearbySearchEnabled

        // Hide or show the card search according to the width
        cardSearch.isVisible = resources.getBoolean(R.bool.show_card_search)

        // Add a nice fade effect to toolbar
        appBarLayout.addOnOffsetChangedListener(
                AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                    toolbar.alpha = abs(verticalOffset / appBarLayout.totalScrollRange.toFloat())
                })

        // Disable vertical scrolling on appBarLayout (it messes with the map...)

        // Set default behavior
        val appBarLayoutParams = appBarLayout.layoutParams as CoordinatorLayout.LayoutParams
        appBarLayoutParams.behavior = AppBarLayout.Behavior()

        // Disable the drag
        val behavior = appBarLayoutParams.behavior as AppBarLayout.Behavior
        behavior.setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return false
            }
        })

        // Set the size of AppBarLayout to 68% of the total height
        coordinator.doOnLayout {
            val size: Int = (it.height * 68) / 100
            appBarLayoutParams.height = size
        }
    }

    private fun initMap() {

        // Turn on/off the My Location layer and the related control on the map
        updateLocationUI()

        // Restore any saved state
        restoreMapState()

        if (isLocationPermissionGranted) {

            if (lastKnownLocation == null) {
                // Get the current location of the device and set the position of the map
                getDeviceLocation(false)
            }
            else {
                // Use the last know location to point the map to
                setDefaultLocation()
                loadNearbyPlaces()
            }
        }
        else {
            setDefaultLocation()
        }
    }

    private fun loadNearbyPlaces() {
        viewModel.getNearbyPlaces(lastKnownLocation ?: defaultLocation)
                .observe(this, Observer { handlePlacesLoaded(it) })
    }

    private fun refreshNearbyPlaces() {
        googleMap?.cameraPosition?.run {
            viewModel.getNearbyPlaces(target)
                    .observe(this@PlacePickerActivity, Observer { handlePlacesLoaded(it) })
        }
    }

    private fun requestPlacesSearch() {

        // This only works if location permission is granted
        if (!isLocationPermissionGranted) {
            checkForPermission()
            return
        }

        // Places API needs a location as well...
        if (lastKnownLocation == null) {
            return
        }

        // These fields are not charged by Google:
        // https://developers.google.com/places/android-sdk/usage-and-billing#basic-data
        val placeFields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG,
                Place.Field.TYPES,
                Place.Field.PHOTO_METADATAS)

        val rectangularBounds = RectangularBounds.newInstance(getCurrentLatLngBounds())

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                .setLocationBias(rectangularBounds)
                .build(this)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }

    private fun restoreFragments() {
        val confirmFragment = supportFragmentManager
                .findFragmentByTag(DIALOG_CONFIRM_PLACE_TAG) as PlaceConfirmDialogFragment?
        confirmFragment?.run {
            confirmListener = this@PlacePickerActivity
        }
    }

    private fun restoreMapState() {
        cameraPosition?.run {
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(this))
        }
    }

    private fun selectThisPlace() {
        googleMap?.cameraPosition?.run {
            viewModel.getPlaceByLocation(target).observe(this@PlacePickerActivity,
                    Observer { handlePlaceByLocation(it) })
        }
    }

    private fun setDefaultLocation() {
        val default: LatLng = lastKnownLocation ?: defaultLocation
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(default, defaultZoom))
    }

    private fun showConfirmPlacePopup(place: Place) {
        val fragment = PlaceConfirmDialogFragment.newInstance(place, this)
        fragment.show(supportFragmentManager, DIALOG_CONFIRM_PLACE_TAG)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {

        googleMap?.let {

            it.uiSettings?.isMyLocationButtonEnabled = false
            it.uiSettings?.isMapToolbarEnabled = false

            if (isLocationPermissionGranted) {
                it.isMyLocationEnabled = true
                btnMyLocation.visibility = View.VISIBLE
            }
            else {
                btnMyLocation.visibility = View.GONE
                it.isMyLocationEnabled = false
            }
        }
    }
}
