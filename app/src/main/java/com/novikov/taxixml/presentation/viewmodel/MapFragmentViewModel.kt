package com.novikov.taxixml.presentation.viewmodel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.usecase.GetAddressesByStringUseCase
import com.novikov.taxixml.domain.usecase.GetLastOrderUseCase
import com.novikov.taxixml.domain.usecase.GetUserPositionUseCase
import com.novikov.taxixml.domain.usecase.SearchByAddressUseCase
import com.novikov.taxixml.domain.usecase.SearchByPointUseCase
import com.novikov.taxixml.singleton.UserInfo
import com.yandex.mapkit.GeoObject
import com.yandex.mapkit.GeoObjectCollection
import com.yandex.mapkit.RequestPoint
import com.yandex.mapkit.RequestPointType
import com.yandex.mapkit.directions.DirectionsFactory
import com.yandex.mapkit.directions.driving.DrivingOptions
import com.yandex.mapkit.directions.driving.DrivingRoute
import com.yandex.mapkit.directions.driving.DrivingSession
import com.yandex.mapkit.directions.driving.VehicleOptions
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.geometry.geo.PolylineIndex
import com.yandex.mapkit.geometry.geo.PolylineUtils
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.Error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.CountDownLatch
import javax.inject.Inject

@HiltViewModel
class MapFragmentViewModel @Inject constructor(
    private val app: Application,
    private val getAddressesByStringUseCase: GetAddressesByStringUseCase,
    private val searchByPointUseCase: SearchByPointUseCase,
    private val searchByAddressUseCase: SearchByAddressUseCase,
    private val getUserPositionUseCase: GetUserPositionUseCase,
    private val getLastOrderUseCase: GetLastOrderUseCase) : AndroidViewModel(app) {

    val addressArray = MutableLiveData<Array<String>>()

    var position: MutableLiveData<Position> = MutableLiveData()
    var searchString:MutableLiveData<String> = MutableLiveData()
    var geoObjects: List<GeoObjectCollection.Item> = ArrayList()
    var geoObject: GeoObject = GeoObject()
    val mldGeoObject = MutableLiveData<GeoObject>()
    val mldRoutePolyline = MutableLiveData(Polyline())

    val mldStartPoint = MutableLiveData(Point())
    val mldEndPoint = MutableLiveData(Point())

    val mldDistance = MutableLiveData(0.0f)
    val mldTraffic = MutableLiveData(0.0f)

    suspend fun getLastOrder(){
        UserInfo.orderData = getLastOrderUseCase.execute()
        mldStartPoint.value = UserInfo.orderData.startPoint
        mldEndPoint.value = UserInfo.orderData.endPoint
        getRouteByPoints(mldStartPoint.value!!, mldEndPoint.value!!)
    }

    suspend fun getCurrentPosition(){
        try {
            position.value = getUserPositionUseCase.execute()
        }
        catch (ex: Exception){
            Log.e("getCurrentPosition", ex.message.toString())
        }
    }

    suspend fun getAddressesByString(string: String){
        Log.i("vm", "start")
        try {
            addressArray.value = getAddressesByStringUseCase.execute(string)
        }
        catch (ex: Exception){
            Log.e("getAddressByString", ex.message.toString())
        }
    }

    suspend fun getRouteByPoints(startPoint: Point, endPoint: Point){

        try {
            val countDownLatch = CountDownLatch(1)

            val drivingRouter = DirectionsFactory.getInstance().createDrivingRouter()
            val drivingOptions = DrivingOptions().apply {
                routesCount = 1
            }
            val points = listOf(
                RequestPoint(startPoint, RequestPointType.WAYPOINT, null, null),
                RequestPoint(endPoint, RequestPointType.WAYPOINT, null, null)
            )
            val vehicleOptions = VehicleOptions()
            val drivingSession = drivingRouter.requestRoutes(
                points,
                drivingOptions,
                vehicleOptions,
                object : DrivingSession.DrivingRouteListener {
                    override fun onDrivingRoutes(p0: MutableList<DrivingRoute>) {
//                    Log.i("route", p0[0].geometry.points.size.toString())
                        if(p0.size > 0){
                            if (p0[0].geometry.points.size > 0) {
                                mldRoutePolyline.value = p0[0].geometry

                                val results = floatArrayOf(0f)

                                Location.distanceBetween(startPoint.latitude,
                                    startPoint.longitude,
                                    endPoint.latitude,
                                    endPoint.longitude,
                                    results)
                                Log.i("results", results[0].toString())
                                Log.i("routeEndPoint", endPoint.longitude.toString())
                                mldDistance.value = p0[0].metadata.weight.distance.value.toFloat()

                                var sum = 0
                                var count = 0

                                p0[0].jamSegments.forEach { it -> run {
                                    if ( it.jamType.ordinal != 0 ) {
                                        sum += it.jamType.ordinal
                                        count += 1
                                    }
                                } }

                                mldTraffic.value = (sum/count).toFloat()

                                Log.i("sum", sum.toString())
                                Log.i("avg", (sum/count).toString())
                                Log.i("weight", p0[0].metadata.weight.distance.value.toString())
                            }

                        }
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
        catch (ex: Exception){
            Log.e("getRouteByPoints", ex.message.toString())
        }
    }

    suspend fun getPointSearchResult(point: Point, map: Map){
        try{
            val resultGeoObject = searchByPointUseCase.execute(point, map)
            try {
                Log.i("vmpoint", resultGeoObject.obj?.metadataContainer!!.getItem(BusinessObjectMetadata::class.java).name)
            }
            catch (e: Exception){
                Log.e("vmpoint", resultGeoObject.obj?.metadataContainer!!.getItem(ToponymObjectMetadata::class.java).address.formattedAddress)
            }
            geoObject = resultGeoObject.obj!!
            mldGeoObject.value = geoObject
        }
        catch (ex: Exception){
            Log.e("getPointSearchResult", ex.message.toString())
        }

    }

    suspend fun  getAddressSearchResult(address: String, map: Map){

        val resultGeoObject = searchByAddressUseCase.execute(address, map)[0]

        try {
            Log.i("vmpoint", resultGeoObject.obj?.metadataContainer!!.getItem(BusinessObjectMetadata::class.java).name)
        }
        catch (e: Exception){
            Log.e("vmpoint", resultGeoObject.obj?.metadataContainer!!.getItem(ToponymObjectMetadata::class.java).address.formattedAddress)
        }

        mldGeoObject.value = resultGeoObject.obj

    }

}