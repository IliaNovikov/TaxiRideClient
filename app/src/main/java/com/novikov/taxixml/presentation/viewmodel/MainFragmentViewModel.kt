package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.R
import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.usecase.GetOrderDataUseCase
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SearchByAddressUseCase
import com.novikov.taxixml.domain.usecase.SearchByPointUseCase
import com.novikov.taxixml.domain.usecase.SetUserPositionUseCase
import com.novikov.taxixml.singleton.UserInfo
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession.DrivingRouteListener
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val app: Application,
    private val getUserPositionUseCase: GetUserPositionUseCase,
    private val setUserPositionUseCase: SetUserPositionUseCase,
    private val getOrderDataUseCase: GetOrderDataUseCase
) : AndroidViewModel(app) {

    var position: MutableLiveData<Position> = MutableLiveData()
    var searchString:MutableLiveData<String> = MutableLiveData()
    var geoObjects: List<GeoObjectCollection.Item> = ArrayList()
    var geoObject: GeoObject = GeoObject()
    val mldGeoObject = MutableLiveData<GeoObject>()
    val mldRoutePolyline = MutableLiveData<Polyline>()


    private val searchByAddressUseCase = SearchByAddressUseCase()
    private val searchByPointUseCase = SearchByPointUseCase()

    suspend fun getCurrentPosition(){
        position.value = getUserPositionUseCase.execute()
    }

    suspend fun getSearchResults(map: Map){
//        geoObjects = searchByAddressUseCase.execute(searchString, map)

        map.mapObjects.clear()

        val userLocation = map.mapObjects.addPlacemark(Point(position.value?.latitude!!, position.value!!.longitude))

        userLocation.setIcon(ImageProvider.fromResource(app.baseContext, R.drawable.map_mark))
        userLocation.setText("Вы здесь", TextStyle().apply {
            offset = -2f
            placement = TextStyle.Placement.BOTTOM
        })

        Log.i("vm", "start")

        geoObjects = searchByAddressUseCase.execute(searchString.value.toString(), map)

        for (geo in geoObjects){
            val placemark = map.mapObjects.addPlacemark(geo.obj!!.geometry[0].point!!)

            placemark.setIcon(ImageProvider.fromResource(app.baseContext, R.drawable.map_point_icon))

            try {
                val address = geo.obj!!.metadataContainer.getItem(ToponymObjectMetadata::class.java).address
                var stringAddress = address.formattedAddress

                stringAddress = stringAddress.split(',').drop(2).joinToString()

                Log.i("vm.addresses.toponym", stringAddress)
                placemark.setText(stringAddress, TextStyle().apply {
                    offset = -2f
                    placement = TextStyle.Placement.BOTTOM
                })
            }
            catch (e: NullPointerException){
                val address = geo.obj!!.metadataContainer.getItem(BusinessObjectMetadata::class.java).address
                var stringAddress = address.formattedAddress

                stringAddress = stringAddress.split(',').drop(2).joinToString()

                Log.i("vm.addresses.business", geo.obj!!.metadataContainer.getItem(BusinessObjectMetadata::class.java)?.name.toString())

                placemark.setText(geo.obj!!.metadataContainer.getItem(BusinessObjectMetadata::class.java)?.name.toString(), TextStyle().apply {
                    offset = -2f
                    placement = TextStyle.Placement.BOTTOM
                })
            }
        }

//        val searchManager = SearchFactory.getInstance().createSearchManager(
//            SearchManagerType.COMBINED)
//
//        val searchOptions = SearchOptions()
//
////        val searchSessionListener = object : Session.SearchListener {
////            override fun onSearchResponse(p0: Response) {
////                Log.i("sbauc", "response")
////                geoObjects = p0.collection.children
////                Log.i("sbaucgo", geoObjects.size.toString())
////                Log.i("sbauc", p0.collection.children.size.toString())
////            }
////
////            override fun onSearchError(p0: Error) {
////                Log.e("sbauc", p0.toString())
////            }
////        }
//
//        val session = searchManager.submit(searchString.value.toString(),
//            Geometry.fromPoint(Point(position.value?.latitude!!, position.value!!.longitude)) ,
//            searchOptions,
//            object : Session.SearchListener {
//                override fun onSearchResponse(p0: Response) {
//                    Log.i("sbauc", "response")
//                    geoObjects = p0.collection.children
//                    Log.i("sbaucgo", geoObjects.size.toString())
//                    Log.i("sbauc", p0.collection.children.size.toString())
//
//                    for (geo in geoObjects){
//                        val placemark = map.mapObjects.addPlacemark(geo.obj!!.geometry[0].point!!)
//
//                        placemark.setIcon(ImageProvider.fromResource(app.baseContext, R.drawable.map_point_icon))
//                        placemark.setText("Цель", TextStyle().apply {
//                            offset = -2f
//                            placement = TextStyle.Placement.BOTTOM
//                        })
//                    }
//                }
//
//                override fun onSearchError(p0: Error) {
//                    Log.e("sbauc", p0.toString())
//                }
//            }
//        )
    }

//    suspend fun getSearchByPointResult(){
//
//    }

    suspend fun getRouteByPoints(startPoint: Point, endPoint: Point){

        val countDownLatch = CountDownLatch(1)

        val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
        val drivingOptions = DrivingOptions().apply {
            routesCount = 1
        }
        val points = listOf(RequestPoint(startPoint, RequestPointType.WAYPOINT, null, null),
            RequestPoint(endPoint, RequestPointType.WAYPOINT, null, null))
        val vehicleOptions = VehicleOptions()
        val drivingSession = drivingRouter.requestRoutes(
            points,
            drivingOptions,
            vehicleOptions,
            object : DrivingRouteListener{
                override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
                    Log.i("route", p0[0].geometry.points.size.toString())
                    mldRoutePolyline.value = p0[0].geometry
                    countDownLatch.countDown()
                }

                override fun onDrivingRoutesError(p0: Error) {
                    countDownLatch.countDown()
                }

            }
        )
        withContext(Dispatchers.IO) {
            countDownLatch.await()
        }
    }

    suspend fun getPointSearchResult(point: Point, map: Map){
        val resultGeoObject = searchByPointUseCase.execute(point, map)
        try {
            Log.i("vmpoint", resultGeoObject.obj?.metadataContainer!!.getItem(BusinessObjectMetadata::class.java).name)
        }
        catch (e: Exception){
            Log.e("vmpointe", resultGeoObject.obj?.metadataContainer!!.getItem(ToponymObjectMetadata::class.java).address.formattedAddress)
        }
        geoObject = resultGeoObject.obj!!
        mldGeoObject.value = geoObject
    }

    suspend fun getOrderData(orderId: String){
        UserInfo.orderData = getOrderDataUseCase.execute(orderId)
    }

}