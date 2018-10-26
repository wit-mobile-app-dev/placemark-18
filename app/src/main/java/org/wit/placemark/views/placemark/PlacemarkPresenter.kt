package org.wit.placemark.views.placemark

import android.annotation.SuppressLint
import android.content.Intent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.intentFor
import org.wit.placemark.helpers.checkLocationPermissions
import org.wit.placemark.helpers.isPermissionGranted
import org.wit.placemark.views.editlocation.EditLocationView
import org.wit.placemark.helpers.showImagePicker
import org.wit.placemark.main.MainApp
import org.wit.placemark.models.Location
import org.wit.placemark.models.PlacemarkModel

class PlacemarkPresenter(val view: PlacemarkView) {

  val IMAGE_REQUEST = 1
  val LOCATION_REQUEST = 2

  var placemark = PlacemarkModel()
  var location = Location(52.245696, -7.139102, 15f)

  var locationService: FusedLocationProviderClient
  var app: MainApp
  var edit = false;

  init {
    app = view.application as MainApp
    locationService = LocationServices.getFusedLocationProviderClient(view)

    if (view.intent.hasExtra("placemark_edit")) {
      edit = true
      placemark = view.intent.extras.getParcelable<PlacemarkModel>("placemark_edit")
      view.showPlacemark(placemark)
    } else {
      checkLocationPermissions(view)
      doSetCurrentLocation()
    }
  }

  @SuppressLint("MissingPermission")
  fun doSetCurrentLocation() {
    locationService.lastLocation.addOnSuccessListener {
      placemark.lat = it.latitude
      placemark.lng = it.longitude
      placemark.zoom = 15f
      doConfigureMap(view.map)
    }
  }

  fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    if (isPermissionGranted(requestCode, grantResults)) {
      doSetCurrentLocation()
    }
  }

  fun doConfigureMap(map: GoogleMap) {
    map.uiSettings.setZoomControlsEnabled(true)
    val loc = LatLng(placemark.lat, placemark.lng)
    val options = MarkerOptions().title(placemark.title).position(loc)
    map.addMarker(options)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, placemark.zoom))
  }

  fun doAddOrSave(title: String, description: String) {
    placemark.title = title
    placemark.description = description
    if (edit) {
      app.placemarks.update(placemark)
    } else {
      app.placemarks.create(placemark)
    }
    view.finish()
  }

  fun doCancel() {
    view.finish()
  }

  fun doDelete() {
    app.placemarks.delete(placemark)
    view.finish()
  }

  fun doSelectImage() {
    showImagePicker(view, IMAGE_REQUEST)
  }

  fun doSetLocation() {
    if (placemark.zoom != 0f) {
      location.lat = placemark.lat
      location.lng = placemark.lng
      location.zoom = placemark.zoom
    }
    view.startActivityForResult(view.intentFor<EditLocationView>().putExtra("location", location), LOCATION_REQUEST)
  }

  fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    when (requestCode) {
      IMAGE_REQUEST -> {
        placemark.image = data.data.toString()
        view.showPlacemark(placemark)
      }
      LOCATION_REQUEST -> {
        location = data.extras.getParcelable<Location>("location")
        placemark.lat = location.lat
        placemark.lng = location.lng
        placemark.zoom = location.zoom
        doConfigureMap(view.map)
      }
    }
  }
}