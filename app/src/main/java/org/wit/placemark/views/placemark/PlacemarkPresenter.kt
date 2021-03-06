package org.wit.placemark.views.placemark

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.wit.placemark.helpers.checkLocationPermissions
import org.wit.placemark.helpers.createDefaultLocationRequest
import org.wit.placemark.helpers.isPermissionGranted
import org.wit.placemark.helpers.showImagePicker
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.*

class PlacemarkPresenter(view: BaseView) : BasePresenter(view) {

  var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
  val locationRequest = createDefaultLocationRequest()
  var placemark = PlacemarkModel()

  var map: GoogleMap? = null
  var edit = false

  init {
    if (view.intent.hasExtra("placemark_edit")) {
      edit = true
      placemark = view.intent.extras.getParcelable<PlacemarkModel>("placemark_edit")
      view.showPlacemark(placemark)
    } else {
      if (checkLocationPermissions(view)) {
        doSetCurrentLocation()
      }
    }
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      locationUpdate(it.latitude, it.longitude)
    }
  }

  fun locationUpdate(lat: Double, lng: Double) {
    placemark.lat = lat
    placemark.lng = lng
    placemark.zoom = 15f
    map?.clear()
    map?.uiSettings?.setZoomControlsEnabled(true)
    val options = MarkerOptions().title(placemark.title).position(LatLng(placemark.lat, placemark.lng))
    map?.addMarker(options)
    map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(placemark.lat, placemark.lng), placemark.zoom))
    view?.showPlacemark(placemark)
  }


  @SuppressLint("MissingPermission")
  fun doResartLocationUpdates() {

    var locationCallback = object : LocationCallback() {
      override fun onLocationResult(locationResult: LocationResult?) {
        if (locationResult != null && locationResult.locations != null) {
          val l = locationResult.locations.last()
          locationUpdate(l.latitude, l.longitude)
        }
      }
    }
    if (!edit) {
      locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }
  }

  override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    } else {
      locationUpdate(52.245696, -7.139102)
    }
  }

  fun doConfigureMap(m: GoogleMap) {
    map = m
    locationUpdate(placemark.lat, placemark.lng)
  }

  fun doAddOrSave(title: String, description: String) {
    placemark.title = title
    placemark.description = description
    if (edit) {
      app.placemarks.update(placemark)
    } else {
      app.placemarks.create(placemark)
    }
    view?.finish()
  }

  fun doDelete() {
    app.placemarks.delete(placemark)
    view?.close()
  }

  fun doSelectImage() {
    if (view != null)
      showImagePicker(view!!, IMAGE_REQUEST)
  }

  fun doSetLocation() {
    view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(placemark.lat, placemark.lng, placemark.zoom))
  }

  override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    when (requestCode) {
      IMAGE_REQUEST -> {
        placemark.image = data.data.toString()
        view?.showPlacemark(placemark)
      }
      LOCATION_REQUEST -> {
        val location = data.extras.getParcelable<Location>("location")
        locationUpdate(location.lat, location.lng)
      }
    }
  }
}