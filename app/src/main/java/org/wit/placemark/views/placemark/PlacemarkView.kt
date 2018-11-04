package org.wit.placemark.views.placemark

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_placemark.*
import kotlinx.android.synthetic.main.placemark_details.*
import kotlinx.android.synthetic.main.placemark_image.*
import kotlinx.android.synthetic.main.placemark_location.*
import kotlinx.android.synthetic.main.placemark_position.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.toast
import org.wit.placemark.R
import org.wit.placemark.helpers.readImageFromPath
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.views.BaseView

class PlacemarkView : BaseView(), AnkoLogger {

  lateinit var presenter: PlacemarkPresenter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark)
    super.init(toolbar)

    presenter = initPresenter (PlacemarkPresenter(this)) as PlacemarkPresenter

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      presenter.doConfigureMap(it)
      it.setOnMapClickListener { presenter.doSetLocation() }
    }
    placemarkImage.setOnClickListener { presenter.doSelectImage() }
  }

  override fun showPlacemark(placemark: PlacemarkModel) {
    placemarkTitle.setText(placemark.title)
    description.setText(placemark.description)
    placemarkImage.setImageBitmap(readImageFromPath(this, placemark.image))
    lat.setText("%.6f".format(placemark.lat))
    lng.setText("%.6f".format(placemark.lng))
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.menu_placemark, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem?): Boolean {
    when (item?.itemId) {
      R.id.item_delete -> {
        presenter.doDelete()
      }
      R.id.item_save -> {
        if (placemarkTitle.text.toString().isEmpty()) {
          toast(R.string.enter_placemark_title)
        } else {
          presenter.doAddOrSave(placemarkTitle.text.toString(), description.text.toString())
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  override fun onDestroy() {
    super.onDestroy()
    mapView.onDestroy()
  }

  override fun onLowMemory() {
    super.onLowMemory()
    mapView.onLowMemory()
  }

  override fun onPause() {
    super.onPause()
    mapView.onPause()
  }

  override fun onResume() {
    super.onResume()
    mapView.onResume()
    presenter.doResartLocationUpdates()
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }
}
