package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.FragmentMapBinding
import com.novikov.taxixml.presentation.view.dialog.TariffDialog
import com.novikov.taxixml.presentation.viewmodel.MainFragmentViewModel
import com.novikov.taxixml.presentation.viewmodel.MapFragmentViewModel
import com.novikov.taxixml.presentation.viewmodel.OrderTaxiFragmentViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.map.TextStyle
import com.yandex.mapkit.search.ToponymObjectMetadata
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

        map = binding.mvMain.map
        tariffDialog = TariffDialog()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startPlacemark = binding.mvMain.map.mapObjects.addPlacemark(viewModel.mldStartPoint.value!!)
        endPlacemark = binding.mvMain.map.mapObjects.addPlacemark(viewModel.mldEndPoint.value!!)

        lifecycleScope.launch {
            viewModel.getCurrentPosition()
        }.invokeOnCompletion {
            binding.mvMain.map.move(CameraPosition(
                Point(viewModel.position.value!!.latitude, viewModel.position.value!!.longitude), 14f, 0f, 0f))
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
            tariffDialog.show(parentFragmentManager, "tariff")
        }

        viewModel.addressArray.observe(requireActivity(), Observer {
            if (binding.etStartAddress.isFocused)
                binding.etStartAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))
            else if (binding.etEndAddress.isFocused)
                binding.etEndAddress.setAdapter(ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, it))

        })

        binding.etStartAddress.setOnItemClickListener { parent, view, position, id ->
            lifecycleScope.launch {
                Log.i("addressSearch", "start")
                viewModel.getAddressSearchResult(parent.getItemAtPosition(position).toString(), binding.mvMain.map)
            }.invokeOnCompletion {
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
                Log.i("addressSearch", "end")
                viewModel.mldEndPoint.value = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                points["start"] = viewModel.mldGeoObject.value!!.geometry[0].point!!
//                setPin(viewModel.mldGeoObject.value!!.geometry[0].point!!, "Стартовая точка")
                binding.mvMain.map.move(CameraPosition(viewModel.mldEndPoint.value!!, 14f, 0f, 0f))
                Log.i("addressSearch", "end2")
            }
        }

        viewModel.mldStartPoint.observe(requireActivity(), Observer {
            setPin(it, "Стартовая точка")
        })
        viewModel.mldEndPoint.observe(requireActivity(), Observer {
            setPin(it, "Конечная точка")
            lifecycleScope.launch {
                viewModel.getRouteByPoints(viewModel.mldStartPoint.value!!, it)
                Log.i("startPoint", viewModel.mldStartPoint.value!!.longitude.toString() + " " + viewModel.mldStartPoint.value!!.latitude.toString())
            }.invokeOnCompletion {
                try {
                    if(viewModel.mldStartPoint.value!!.latitude == viewModel.mldEndPoint.value!!.latitude
                        && viewModel.mldStartPoint.value!!.longitude == viewModel.mldEndPoint.value!!.longitude)
                        binding.mvMain.map.mapObjects.addPolyline()
                    else
                        binding.mvMain.map.mapObjects.addPolyline(viewModel.mldRoutePolyline.value!!)
                }
                catch (ex: Exception){
                    Log.e("polyline", ex.message.toString())
                    binding.mvMain.map.mapObjects.addPolyline(Polyline())
                }
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