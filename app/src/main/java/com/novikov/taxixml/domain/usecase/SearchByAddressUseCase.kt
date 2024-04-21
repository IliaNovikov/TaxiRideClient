package com.novikov.taxixml.domain.usecase

import android.util.Log
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.VisibleRegionUtils
import com.yandex.mapkit.search.Response
import com.yandex.mapkit.search.SearchFactory
import com.yandex.mapkit.search.SearchManagerType
import com.yandex.mapkit.search.SearchOptions
import com.yandex.mapkit.search.SearchType
import com.yandex.mapkit.search.Session.SearchListener
import com.yandex.runtime.Error
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay

class SearchByAddressUseCase {

    suspend fun execute(searchString: String, map: Map): List<GeoObjectCollection.Item>{

        var geoObjects: List<GeoObjectCollection.Item> = ArrayList()

        val searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)

        val searchOptions = SearchOptions().apply {
            searchTypes = SearchType.NONE.value
            resultPageSize = 32
        }

        val searchSessionListener = object :  SearchListener{
            override fun onSearchResponse(p0: Response) {
                Log.i("sbauc", "response")
                geoObjects = p0.collection.children
            }

            override fun onSearchError(p0: Error) {
                Log.e("sbauc", p0.toString())
            }
        }

        val session = searchManager.submit(searchString, VisibleRegionUtils.toPolygon(map.visibleRegion), searchOptions, searchSessionListener)

        delay(1000)

        return geoObjects
    }



}