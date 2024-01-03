package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
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
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.TextStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()

    private lateinit var map: Map

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        MapKitFactory.setApiKey("f2d2f815-7d2a-4e71-b3f1-0ca53df6df72")
        MapKitFactory.initialize(requireContext())

        binding = FragmentMainBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = binding.mvMain.map
        map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))

        lifecycleScope.launch {
            viewModel.getCurrentPosition()
        }.invokeOnCompletion {
            Toast.makeText(requireContext(), viewModel.position.value?.longitude.toString(), Toast.LENGTH_LONG).show()
            map.move(
                CameraPosition(
                Point(viewModel.position.value?.latitude!!, viewModel.position.value!!.longitude),
                14.0f,
                0.0f,
                0.0f
            ))

            var placemark = map.mapObjects.addPlacemark(Point(viewModel.position.value?.latitude!!, viewModel.position.value!!.longitude))
            placemark.setText("Вы здесь", TextStyle().setOffset(3f))

            binding.ibToUserLocation.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.getCurrentPosition()
                }.invokeOnCompletion {
                    Toast.makeText(
                        requireContext(),
                        viewModel.position.value?.longitude.toString(),
                        Toast.LENGTH_LONG
                    ).show()
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

                    placemark = map.mapObjects.addPlacemark(
                        Point(
                            viewModel.position.value?.latitude!!,
                            viewModel.position.value!!.longitude
                        )
                    )
                    placemark.setText("Вы здесь", TextStyle().setOffset(3f))
                }
            }
        }


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
}