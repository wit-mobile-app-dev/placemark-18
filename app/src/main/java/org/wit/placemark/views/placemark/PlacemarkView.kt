package org.wit.placemark.views.placemark

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
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

class PlacemarkView : AppCompatActivity(), AnkoLogger {

  lateinit var map: GoogleMap
  lateinit var presenter: PlacemarkPresenter
  var placemark = PlacemarkModel()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_placemark)
    toolbarAdd.title = title
    setSupportActionBar(toolbarAdd)

    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync {
      map = it
      presenter.doConfigureMap(map)
    }

    presenter = PlacemarkPresenter(this)

    chooseImage.setOnClickListener { presenter.doSelectImage() }

    placemarkLocation.setOnClickListener { presenter.doSetLocation() }
  }

  fun showPlacemark(placemark: PlacemarkModel) {
    placemarkTitle.setText(placemark.title)
    description.setText(placemark.description)
    placemarkImage.setImageBitmap(readImageFromPath(this, placemark.image))
    if (placemark.image != null) {
      chooseImage.setText(R.string.change_placemark_image)
    }
    showLocation (placemark.lat, placemark.lng)
  }

  fun showLocation (latitude: Double, longitude : Double) {
    lat.setText("%.6f".format(latitude))
    lng.setText("%.6f".format(longitude))
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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (data != null) {
      presenter.doActivityResult(requestCode, resultCode, data)
    }
  }

  override fun onBackPressed() {
    presenter.doCancel()
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
  }

  override fun onSaveInstanceState(outState: Bundle?) {
    super.onSaveInstanceState(outState)
    mapView.onSaveInstanceState(outState)
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    presenter.doRequestPermissionsResult(requestCode, permissions, grantResults)
  }
}

