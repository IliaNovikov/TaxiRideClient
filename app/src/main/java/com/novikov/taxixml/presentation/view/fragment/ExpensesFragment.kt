package com.novikov.taxixml.presentation.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.novikov.taxixml.R
import com.novikov.taxixml.adapters.HistoryOrderAdapter
import com.novikov.taxixml.databinding.FragmentExpensesBinding
import com.novikov.taxixml.domain.model.HistoryOrder
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.concurrent.CountDownLatch

class ExpensesFragment : Fragment() {

    private lateinit var binding: FragmentExpensesBinding

    private val orders = ArrayList<HistoryOrder>()
    private lateinit var adapter: HistoryOrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentExpensesBinding.inflate(layoutInflater)

        adapter = HistoryOrderAdapter(requireContext(), orders)

        Firebase.database.reference.child("orders").orderByChild("clientUid").equalTo(UserInfo.uid).get().addOnCompleteListener {

            binding.tvTotalOrders.text = requireContext().getString(R.string.total_orders) + " " + it.result.children.count()

            val yValsPie = ArrayList<Entry>()

            var economSum = 0
            var comfortSum = 0
            var businessSum = 0

            val yValsBar = ArrayList<BarEntry>()

            val xValsBar = arrayListOf(LocalDate.now().toString(),
                LocalDate.now().minusDays(1).toString(),
                LocalDate.now().minusDays(2).toString(),
                LocalDate.now().minusDays(3).toString(),
                LocalDate.now().minusDays(4).toString(),
                LocalDate.now().minusDays(5).toString(),
                LocalDate.now().minusDays(6).toString())

            xValsBar.reverse()

            for (i in it.result.children){

                val dateIndex = xValsBar.indexOf(xValsBar.find { item -> item == i.child("date").value.toString() })

                yValsBar.add(BarEntry(i.child("price").value.toString().toFloat(), dateIndex))

                when(i.child("tariff").value.toString()){
                    "econom" -> {
                        economSum += i.child("price").value.toString().toInt()
                    }
                    "comfort" -> {
                        comfortSum += i.child("price").value.toString().toInt()
                    }
                    "business" -> {
                        businessSum += i.child("price").value.toString().toInt()
                    }
                }



                Log.i("orderSum", i.child("price").value.toString())
            }

            yValsPie.add(Entry(economSum.toFloat(), 0))
            yValsPie.add(Entry(comfortSum.toFloat(), 0))
            yValsPie.add(Entry(businessSum.toFloat(), 0))

            val dataSetPie = PieDataSet(yValsPie, null)

            val xValsPie  = arrayListOf("econom", "comfort", "business")

            val pieData = PieData(xValsPie, dataSetPie)

            binding.pcTariff.data = pieData
            dataSetPie.setColors(ColorTemplate.PASTEL_COLORS)
            binding.pcTariff.animateXY(1500, 1500)
            binding.pcTariff.legend.isEnabled = false
            binding.pcTariff.setDescription("")
            dataSetPie.valueTextSize = 10f

            val dataSetBar = BarDataSet(yValsBar, null)

            val barData = BarData(xValsBar, dataSetBar)
            binding.bcLastWeek.data = barData
            dataSetBar.setColors(ColorTemplate.PASTEL_COLORS)
            binding.bcLastWeek.animateXY(1500, 1500)
            binding.bcLastWeek.legend.isEnabled = false
            binding.bcLastWeek.setDescription("")
        }

        binding.btnGoBack.setOnClickListener {
            NavigationController.navHost.navigate(R.id.settingsFragment)
        }


        lifecycleScope.launch {
            Firebase.database.reference.child("orders").orderByChild("clientUid").equalTo(UserInfo.uid).get().addOnCompleteListener {
                Log.i("orders", it.result.value.toString())
                if (it.result.exists()){
                    for (i in it.result.children){
                        val order = HistoryOrder()
                        order.id = i.key.toString()
                        order.date = i.child("date").value.toString()
                        order.startAddress = i.child("startAddress").value.toString()
                        order.endAddress = i.child("endAddress").value.toString()
                        order.driverUid = i.child("driverUid").value.toString()
//                        val countDownLatch = CountDownLatch(1)
                        orders.add(order)
//                        countDownLatch.await()
                    }

//                    Log.i("ordersSize", orders.size.toString())
                }
            }.await()

            var countDownLatch = CountDownLatch(4)
//            countDownLatch = CountDownLatch(4)
            orders.forEach {order ->
                lifecycleScope.launch {
                    Firebase.database.reference.child("applications").child(order.driverUid).get().addOnCompleteListener {
                        Log.i("orderInfo", it.result.value.toString())
                        order.apply {
                            this.driverName = it.result.child("personal").child("name").value.toString()
                            this.carBrand = it.result.child("car").child("carBrand").value.toString()
                            this.carNumber = it.result.child("car").child("carNumber").value.toString()
                        }
                        countDownLatch.countDown()

                    }.await()
                }.invokeOnCompletion {

                    Log.i("cdl", countDownLatch.count.toString())
                }
            }

            withContext(Dispatchers.IO) {
                countDownLatch.await()
            }

//            Log.i("ordersSize", orders.size.toString())
        }.invokeOnCompletion {
            adapter.notifyDataSetChanged()
            Log.i("ordersSize", orders.size.toString())
            Log.i("orderDriver", orders[0].driverName)
            binding.rvOrders.adapter = adapter
            Log.i("rvOrders", binding.rvOrders.adapter!!.itemCount.toString())
        }

        return binding.root
    }
}