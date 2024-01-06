package com.novikov.taxixml.domain.usecase

import android.util.Log
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session
import com.yandex.runtime.Error
import kotlinx.coroutines.delay

class SearchByPointUseCase {
    val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
    suspend fun execute(point: Point, map: Map) : GeoObjectCollection.Item{

        var geoObject = GeoObjectCollection.Item()

        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.NONE.value
            resultPageSize = 1
        }

        val searchSessionListener = object : Session.SearchListener {
            override fun onSearchResponse(p0: Response) {
                Log.i("sbpuc", "response")
                geoObject = p0.collection.children.firstOrNull()!!
            }

            override fun onSearchError(p0: Error) {
                Log.e("sbpuc", p0.toString())
            }
        }

        val session = searchManager.submit(point, 16, searchOptions, searchSessionListener)

        delay(1000)
        return geoObject
    }
}