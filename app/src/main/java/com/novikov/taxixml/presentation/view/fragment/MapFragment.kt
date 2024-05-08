package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentMapBinding
import com.novikov.taxixml.presentation.view.dialog.TariffDialog
import com.novikov.taxixml.presentation.viewmodel.MapFragmentViewModel
import com.novikov.taxixml.singleton.UserInfo
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.PolylineMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapFragmentViewModel by viewModels()
    private val points = mutableMapOf<String, Point>("start" to Point(), "end" to Point())
    private lateinit var startPlacemark: PlacemarkMapObject
    private lateinit var endPlacemark: PlacemarkMapObject
    private lateinit var map: Map
    private lateinit var mapKitInstance: MapKit
    private lateinit var routePolyline: PolylineMapObject
    private lateinit var userLocation: UserLocationLayer

    private val cameraStartListener = object : CameraListener{
        override fun onCameraPositionChanged(
            p0: Map,
            p1: CameraPosition,
            p2: CameraUpdateReason,
            p3: Boolean
        ) {
            startPlacemark.isVisible = true
            startPlacemark.geometry = p1.target
        }
    }

    private val cameraEndListener = object : CameraListener{
        override fun onCameraPositionChanged(
            p0: Map,
            p1: CameraPosition,
            p2: CameraUpdateReason,
            p3: Boolean
        ) {
            endPlacemark.isVisible = true
            endPlacemark.geometry = p1.target
        }

    }

    private lateinit var tariffDialog: TariffDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapBinding.inflate(inflater)

        mapKitInstance = MapKitFactory.getInstance()
        userLocation = mapKitInstance.createUserLocationLayer(binding.mvMain.mapWindow)
        userLocation.isVisible = true

        map = binding.mvMain.map

        tariffDialog = TariffDialog()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPlacemark = binding.mvMain.map.mapObjects.addPlacemark(viewModel.mldStartPoint.value!!)
        endPlacemark = binding.mvMain.map.mapObjects.addPlacemark(viewModel.mldEndPoint.value!!)
        startPlacemark.isVisible = false
        endPlacemark.isVisible = false

        routePolyline = binding.mvMain.map.mapObjects.addPolyline()

        binding.btnSelectPoint.isVisible = false
        binding.btnSelectEndPoint.isVisible = false
        binding.btnContinue.isEnabled = false

        try{
            lifecycleScope.launch {
                viewModel.getCurrentPosition()
            }.invokeOnCompletion {
                lifecycleScope.launch {
                    viewModel.getPointSearchResult(Point(viewModel.position.value!!.latitude, viewModel.position.value!!.longitude), map)
                }.invokeOnCompletion {
                    try {
                        binding.mvMain.map.move(CameraPosition(
                            Point(viewModel.position.value!!.latitude, viewModel.position.value!!.longitude), 14f, 0f, 0f))
                        viewModel.mldStartPoint.value = Point(viewModel.position.value!!.latitude, viewModel.position.value!!.longitude)
                    }
                    catch (ex:Exception){
                        Log.e("getCurrentPosition", ex.message.toString())
                    }
                }

            }
        }
        catch (ex: Exception){
            Log.e("getCurrentPosition", ex.message.toString())
        }

        binding.etStartAddress.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getAddressesByString(it.toString())
            }
        }
        binding.etEndAddress.addTextChangedListener {
            lifecycleScope.launch {
                viewModel.getAddressesByString(it.toString())
            }
        }

        binding.btnStartAddressOnMap.setOnClickListener {
            binding.btnSelectPoint.isVisible = true
            val centerPoint = ScreenPoint((binding.mvMain.mapWindow.width() / 2).toFloat(), (binding.mvMain.mapWindow.height() / 2).toFloat())
            Log.i("world", binding.mvMain.mapWindow.screenToWorld(centerPoint)!!.latitude.toString())
            map.addCameraListener(cameraStartListener)
        }

        binding.btnEndAddressOnMap.setOnClickListener {
            binding.btnSelectEndPoint.isVisible = true
            val centerPoint = ScreenPoint((binding.mvMain.mapWindow.width() / 2).toFloat(), (binding.mvMain.mapWindow.height() / 2).toFloat())
            Log.i("world", binding.mvMain.mapWindow.screenToWorld(centerPoint)!!.latitude.toString())
            map.addCameraListener(cameraEndListener)
        }

        binding.btnSelectPoint.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getPointSearchResult(startPlacemark.geometry, map)
            }.invokeOnCompletion {
                viewModel.mldStartPoint.value = viewModel.mldGeoObject.value!!.geometry[0].point!!
            }
            map.removeCameraListener(cameraStartListener)
            it.isVisible = false
        }

        binding.btnSelectEndPoint.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getPointSearchResult(endPlacemark.geometry, map)
            }.invokeOnCompletion {
                viewModel.mldEndPoint.value = viewModel.mldGeoObject.value!!.geometry[0].point!!
            }
            map.removeCameraListener(cameraEndListener)
            it.isVisible = false
        }

        binding.btnToUserLocation.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getCurrentPosition()
            }.invokeOnCompletion {
                binding.mvMain.map.move(CameraPosition(
                    Point(viewModel.position.value!!.latitude, viewModel.position.value!!.longitude), 14f, 0f, 0f))
            }
        }

        binding.btnPlusScale.setOnClickListener {
            map.move(CameraPosition(
                map.cameraPosition.target,
                map.cameraPosition.zoom + 1f,
                map.cameraPosition.azimuth,
                map.cameraPosition.tilt))
        }
        binding.btnMinusScale.setOnClickListener {
            map.move(CameraPosition(
                map.cameraPosition.target,
                map.cameraPosition.zoom - 1f,
                map.cameraPosition.azimuth,
                map.cameraPosition.tilt))
        }

        binding.btnContinue.setOnClickListener {
            tariffDialog.distance = viewModel.mldDistance.value!!
            tariffDialog.traffic = viewModel.mldTraffic.value!!
            tariffDialog.show(parentFragmentManager, "tariff")
        }

        viewModel.addressArray.observe(requireActivity(), Observer {
            try{
                if (binding.etStartAddress.isFocused)
                    binding.etStartAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))
                else if (binding.etEndAddress.isFocused)
                    binding.etEndAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))
            }
            catch (ex: Exception){
                Log.e("addressArray", ex.message.toString())
            }
        })

        binding.etStartAddress.setOnItemClickListener { parent, view, position, id ->
            lifecycleScope.launch {
                Log.i("addressSearch", "start")
                viewModel.getAddressSearchResult(parent.getItemAtPosition(position).toString(), binding.mvMain.map)
            }.invokeOnCompletion {
                startPlacemark.isVisible = true
                Log.i("addressSearch", "end")
                viewModel.mldStartPoint.value = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                points["start"] = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                setPin(viewModel.mldGeoObject.value!!.geometry[0].point!!, "Стартовая точка")
                binding.mvMain.map.move(CameraPosition(viewModel.mldStartPoint.value!!, 14f, 0f, 0f))
                Log.i("addressSearch", "end2")
            }

            Toast.makeText(requireContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show()
        }

        binding.etEndAddress.setOnItemClickListener { parent, view, position, id ->
            lifecycleScope.launch {
                Log.i("addressSearch", "start")
                viewModel.getAddressSearchResult(parent.getItemAtPosition(position).toString(), binding.mvMain.map)
            }.invokeOnCompletion {
                endPlacemark.isVisible = true
                Log.i("addressSearch", "end")
                viewModel.mldEndPoint.value = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                points["start"] = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                setPin(viewModel.mldGeoObject.value!!.geometry[0].point!!, "Стартовая точка")
                binding.mvMain.map.move(CameraPosition(viewModel.mldEndPoint.value!!, 14f, 0f, 0f))
                Log.i("addressSearch", "end2")
            }
        }

        viewModel.mldStartPoint.observe(requireActivity(), Observer {
            try{
                val address = viewModel.mldGeoObject.value!!.
                metadataContainer.getItem(ToponymObjectMetadata::class.java).address.formattedAddress
                    .split(",")
                    .subList(2, 5)
                    .joinToString(",")
                binding.etStartAddress.setText(address)
                UserInfo.orderData.startPoint = it
                UserInfo.orderData.startAddress = address

            }
            catch (ex: Exception){
                Log.e("endPoint", ex.message.toString())
            }
            try{
                startPlacemark.isVisible = true
                setPin(it, "Стартовая точка")
                if(binding.etEndAddress.text.isNotEmpty()) {
                    binding.btnContinue.isEnabled = true
                    lifecycleScope.launch {
                        viewModel.getRouteByPoints(it, viewModel.mldEndPoint.value!!)
                        Log.i(
                            "startPoint",
                            viewModel.mldStartPoint.value!!.longitude.toString() + " " + viewModel.mldStartPoint.value!!.latitude.toString()
                        )
                    }.invokeOnCompletion {
                        try {
                            if (viewModel.mldStartPoint.value!!.latitude == viewModel.mldEndPoint.value!!.latitude
                                && viewModel.mldStartPoint.value!!.longitude == viewModel.mldEndPoint.value!!.longitude
                            )
                                binding.mvMain.map.mapObjects.addPolyline()
                            else
                                routePolyline.geometry = viewModel.mldRoutePolyline.value!!
//                        binding.mvMain.map.mapObjects.addPolyline(viewModel.mldRoutePolyline.value!!)
                        } catch (ex: Exception) {
                            Log.e("polyline", ex.message.toString())
                            binding.mvMain.map.mapObjects.addPolyline(Polyline())
                        }
                    }
                }
            }
            catch (ex: Exception){
                Log.e("mldStartPoint", ex.message.toString())
            }

        })
        viewModel.mldEndPoint.observe(requireActivity(), Observer {
            try{
                val address = viewModel.mldGeoObject.value!!.
                metadataContainer.getItem(ToponymObjectMetadata::class.java).address.formattedAddress
                    .split(",")
                    .subList(2, 5)
                    .joinToString(",")
                binding.etEndAddress.setText(address)
                UserInfo.orderData.endPoint = it
                UserInfo.orderData.endAddress = address
            }
            catch (ex: Exception){
                Log.e("endPoint", ex.message.toString())
            }
            try{
                startPlacemark.isVisible = true
                setPin(it, "Конечная точка")
                if(binding.etStartAddress.text.isNotEmpty()){
                    binding.btnContinue.isEnabled = true
                    lifecycleScope.launch {
                        viewModel.getRouteByPoints(viewModel.mldStartPoint.value!!, it)
                        Log.i("startPoint", viewModel.mldStartPoint.value!!.longitude.toString() + " " + viewModel.mldStartPoint.value!!.latitude.toString())
                    }.invokeOnCompletion {
                        try {
                            if(viewModel.mldStartPoint.value!!.latitude == viewModel.mldEndPoint.value!!.latitude
                                && viewModel.mldStartPoint.value!!.longitude == viewModel.mldEndPoint.value!!.longitude)
                                binding.mvMain.map.mapObjects.addPolyline()
                            else
                                routePolyline.geometry = viewModel.mldRoutePolyline.value!!
//                        binding.mvMain.map.mapObjects.addPolyline(viewModel.mldRoutePolyline.value!!)
                        }
                        catch (ex: Exception){
                            Log.e("polyline", ex.message.toString())
                            binding.mvMain.map.mapObjects.addPolyline(Polyline())
                        }
                    }
                }
            }
            catch (ex: Exception){
                Log.e("mldEndPoint", ex.message.toString())
            }


        })

//        binding.mvMain.map.mapObjects.clear()

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mvMain.onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        binding.mvMain.onStop()
        super.onStop()
    }

    private fun setPin(point: Point, address: String){

        Log.i("start", points["start"]!!.latitude.toString() + " " + points["start"]!!.longitude.toString())

//        binding.mvMain.map.mapObjects.remove(startPlacemark)
//
//        startPlacemark = binding.mvMain.map.mapObjects.addPlacemark(viewModel.mldStartPoint.value!!)

        startPlacemark.geometry = viewModel.mldStartPoint.value!!

        startPlacemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.map_point_icon))
        startPlacemark.setText(
            "Стартовая точка",
            TextStyle().apply {
                offset = -2f
                placement = TextStyle.Placement.BOTTOM
            }
        )

        endPlacemark.geometry = viewModel.mldEndPoint.value!!

        endPlacemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.map_point_icon))
        endPlacemark.setText(
            "Конечная точка",
            TextStyle().apply {
                offset = -2f
                placement = TextStyle.Placement.BOTTOM
            }
        )
    }

}