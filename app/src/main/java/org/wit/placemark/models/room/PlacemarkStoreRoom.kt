package org.wit.placemark.room

import android.content.Context
import androidx.room.Room
import org.jetbrains.anko.coroutines.experimental.bg
import org.wit.placemark.models.PlacemarkModel
import org.wit.placemark.models.PlacemarkStore

class PlacemarkStoreRoom(val context: Context) : PlacemarkStore {

  var dao: PlacemarkDao

  init {
    val database = Room.databaseBuilder(context, Database::class.java, "room_sample.db")
        .fallbackToDestructiveMigration()
        .build()
    dao = database.placemarkDao()
  }

  suspend override fun findAll(): List<PlacemarkModel> {
    val deferredPlacemarks = bg {
      dao.findAll()
    }
    val placemarks = deferredPlacemarks.await()
    return placemarks
  }

  suspend override fun findById(id: Long): PlacemarkModel? {
    val deferredPlacemark = bg {
      dao.findById(id)
    }
    val placemark = deferredPlacemark.await()
    return placemark
  }

  override fun create(placemark: PlacemarkModel) {
    bg {
      dao.create(placemark)
    }
  }

  override fun update(placemark: PlacemarkModel) {
    bg {
      dao.update(placemark)
    }
  }

  override fun delete(placemark: PlacemarkModel) {
    bg {
      dao.deletePlacemark(placemark)
    }
  }

  fun clear() {
  }
}