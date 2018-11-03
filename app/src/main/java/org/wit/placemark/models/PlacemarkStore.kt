package org.wit.placemark.models

interface PlacemarkStore {
  suspend fun findAll(): List<PlacemarkModel>
  suspend fun findById(id:Long) : PlacemarkModel?
  fun create(placemark: PlacemarkModel)
  fun update(placemark: PlacemarkModel)
  fun delete(placemark: PlacemarkModel)
}