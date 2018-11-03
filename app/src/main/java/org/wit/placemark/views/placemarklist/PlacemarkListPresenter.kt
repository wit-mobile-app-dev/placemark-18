package org.wit.placemark.views.placemarklist

import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BasePresenter
import org.wit.placemark.views.VIEW

class PlacemarkListPresenter(view: PlacemarkListView) : BasePresenter(view) {

  fun getPlacemarks() = app.placemarks.findAll()

  fun doAddPlacemark() {
    view?.navigateTo(VIEW.PLACEMARK)
  }

  fun doEditPlacemark(placemark: PlacemarkModel) {
    view?.navigateTo(VIEW.PLACEMARK,0,"placemark_edit", placemark )
  }

  fun doShowPlacemarksMap() {
    view?.navigateTo(VIEW.MAPS)
  }
}