package org.wit.placemark.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter

class PlacemarkMapPresenter(view: PlacemarkMapView) : BasePresenter(view) {

  fun doPopulateMap(map: GoogleMap, placemarks:List<PlacemarkModel>) {
    map.uiSettings.setZoomControlsEnabled(true)
    placemarks.forEach {
      val loc = LatLng(it.lat, it.lng)
      val options = MarkerOptions().title(it.title).position(loc)
      map.addMarker(options).tag = it.id
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.zoom))
    }
  }

  fun doMarkerSelected(marker: Marker) {
    async(UI) {
      val tag = marker.tag as Long
      val placemark = app.placemarks.findById(tag)
      if (placemark != null) view?.showPlacemark(placemark)
    }
  }

  fun loadPlacemarks() {
    async(UI) {
      view?.showPlacemarks(app.placemarks.findAll())
    }
  }
}