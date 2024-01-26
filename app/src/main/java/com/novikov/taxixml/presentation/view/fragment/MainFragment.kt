package com.novikov.taxixml.presentation.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentMainBinding
import com.novikov.taxixml.presentation.viewmodel.MainFragmentViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObject
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.MapWindow
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.search.BusinessObjectMetadata
import com.yandex.mapkit.search.ToponymObjectMetadata
import com.yandex.runtime.image.ImageProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()

    private lateinit var map: Map
    private lateinit var mapWindow: MapWindow

    private lateinit var placemark: PlacemarkMapObject

    private val inputListener = object : InputListener{
        override fun onMapTap(p0: Map, p1: Point) {
            try{
                Toast.makeText(requireContext(), "click" + p1.latitude.toString(), Toast.LENGTH_SHORT).show()
                viewModel.searchString.value = p1.latitude.toString() + " " + p1.longitude.toString()
            }
            catch (e: Exception){
                Log.e("view", e.message.toString())
            }

        }

        override fun onMapLongTap(p0: Map, p1: Point) {
            TODO("Not yet implemented")
        }

    }

    private val geoObjectTapListener = object :GeoObjectTapListener, MapObjectTapListener {
        override fun onObjectTap(p0: GeoObjectTapEvent): Boolean {
//            return try{
//                Toast.makeText(requireContext(), p0.geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java).address.formattedAddress, Toast.LENGTH_LONG).show()
//                Log.i("address", p0.geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java).address.formattedAddress)
//                true
//            } catch (e: NullPointerException){
//                Toast.makeText(requireContext(), p0.geoObject.metadataContainer.getItem(BusinessObjectMetadata::class.java).address.formattedAddress, Toast.LENGTH_LONG).show()
//                Log.i("address", p0.geoObject.metadataContainer.getItem(BusinessObjectMetadata::class.java).address.formattedAddress)
//                true
//            }
            lifecycleScope.launch {
                viewModel.getPointSearchResult(p0.geoObject.geometry[0].point!!, map)
            }.invokeOnCompletion {
                setPin(
                    viewModel.geoObject.geometry[0].point!!,
                    viewModel.geoObject.metadataContainer.getItem(ToponymObjectMetadata::class.java).address.formattedAddress)
            }
            return true
        }

        override fun onMapObjectTap(p0: MapObject, p1: Point): Boolean {
            Toast.makeText(requireContext(), "click", Toast.LENGTH_SHORT).show()
            return true
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f2d2f815-7d2a-4e71-b3f1-0ca53df6df72")
        MapKitFactory.initialize(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = binding.mvMain.map
        mapWindow = binding.mvMain.mapWindow

        map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))

        // Изначальная позиция пользователя
        lifecycleScope.launch {
            viewModel.getCurrentPosition()
        }.invokeOnCompletion {
            map.move(
                CameraPosition(
                Point(viewModel.position.value?.latitude!!, viewModel.position.value!!.longitude),
                14.0f,
                0.0f,
                0.0f
            ))
            setUserLocationMark()
            }

        //Слушатель кнопки btnToUserLocation
        binding.btnToUserLocation.setOnClickListener {
            lifecycleScope.launch {
                viewModel.getCurrentPosition()
            }.invokeOnCompletion {
                map.move(
                    CameraPosition(
                        Point(
                            viewModel.position.value?.latitude!!,
                            viewModel.position.value!!.longitude
                        ),
                        14.0f,
                        0.0f,
                        0.0f
                    )
                )
                map.mapObjects.remove(placemark)
                setUserLocationMark()
            }
        }

        //Слушатель нажатия на иконку поиска
        binding.etSearchLayout.setStartIconOnClickListener {
            if(binding.etSearch.text.toString().isNotEmpty()){
                Toast.makeText(requireContext(), "wait", Toast.LENGTH_SHORT).show()
                viewModel.searchString.value = binding.etSearch.text.toString()
                lifecycleScope.launch {
                    viewModel.getSearchResults(map)
                }.invokeOnCompletion {
                    Log.i("viewgo", viewModel.geoObjects.size.toString())
                    Toast.makeText(requireContext(), viewModel.geoObjects.size.toString(), Toast.LENGTH_LONG).show()
                }
            }
            else
                Toast.makeText(requireContext(), "empty", Toast.LENGTH_SHORT).show()
        }

        //Слушатель нажатия на кнопку btnAddressMark
        binding.btnAddressMark.setOnClickListener {
            val screenPoint = ScreenPoint((mapWindow.width()/2f), (mapWindow.height()/2f))

            val worldPoint = mapWindow.screenToWorld(screenPoint)

            val centerPlacemark = map.mapObjects.addPlacemark(worldPoint!!)

            centerPlacemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.map_point_icon))
        }

        //Слушатель нажатия на кнопку btnSettings
        binding.btnSettings.setOnClickListener {
            NavigationController.navHost.navigate(R.id.action_mainFragment_to_settingsFragment)
        }

        map.addTapListener(geoObjectTapListener)

        map.addInputListener(inputListener)

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mvMain.onStart()
    }

    override fun onStop() {
        binding.mvMain.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun setUserLocationMark(){
        placemark = map.mapObjects.addPlacemark(
            Point(
                viewModel.position.value?.latitude!!,
                viewModel.position.value!!.longitude
            )
        )
        placemark.setText("Вы здесь", TextStyle().apply {
            offset = -2f
            placement = TextStyle.Placement.BOTTOM
        })
        placemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.map_mark))

        placemark.addTapListener(geoObjectTapListener)
    }

    private fun setPin(point: Point, address: String){

        map.mapObjects.clear()

        setUserLocationMark()

        val pinPlacemark = map.mapObjects.addPlacemark(point)
        pinPlacemark.setIcon(ImageProvider.fromResource(requireContext(), R.drawable.map_point_icon))
        pinPlacemark.setText(
            address.split(',').drop(2).joinToString(),
            TextStyle().apply {
                offset = -2f
                placement = TextStyle.Placement.BOTTOM
            }
            )
    }
}