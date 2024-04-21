package com.novikov.taxixml.presentation.view.activity

import android.Manifest
import android.Manifest.permission_group.SMS
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.ActivityMainBinding
import com.novikov.taxixml.domain.model.Address
import com.novikov.taxixml.domain.model.Card
import com.novikov.taxixml.presentation.viewmodel.MainActivityViewModel
import com.novikov.taxixml.singleton.NavigationController
import com.novikov.taxixml.singleton.UserInfo
import com.yandex.mapkit.MapKitFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vm: MainActivityViewModel by viewModels()

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        MapKitFactory.setApiKey("f2d2f815-7d2a-4e71-b3f1-0ca53df6df72")

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        NavigationController.navHost = navHostFragment.navController

        loadingDialog = AlertDialog.Builder(this, androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dialog).apply {
            setView(R.layout.dialog_loading)
        }.create()

        if (FirebaseAuth.getInstance().currentUser != null){
            Log.i("main activity", "get data")
            vm.uid.value = FirebaseAuth.getInstance().currentUser!!.uid
            lifecycleScope.launch {
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.show()
                vm.getUserData()
            }.invokeOnCompletion {

                UserInfo.name = vm.userData.name
                UserInfo.phone = vm.userData.phone
                UserInfo.uid = vm.userData.uid
                UserInfo.cards = vm.userData.cards as ArrayList<Card>
                UserInfo.savedAddresses = vm.userData.savedAddresses as ArrayList<Address>

                Log.i("main activity", UserInfo.name)

                loadingDialog.dismiss()

                if (UserInfo.name.isNotEmpty()){
                    Log.i("firebaseUser", FirebaseAuth.getInstance().currentUser?.uid.toString())
                    NavigationController.navHost.navigate(R.id.orderTaxiFragment)
                    Toast.makeText(this, FirebaseAuth.getInstance().currentUser?.phoneNumber, Toast.LENGTH_LONG).show()
                }
                else{
                    NavigationController.navHost.navigate(R.id.authorizationViaPhoneFragment)
                }
            }
        }



        checkPermissions()

        setContentView(binding.root)
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults[0] == RESULT_OK){
            Toast.makeText(this, "Для корректной работы программы нужно дать разрешение на доступ к геолокации", Toast.LENGTH_LONG).show()
        }
        else{
            Toast.makeText(this, "successful", Toast.LENGTH_SHORT).show()
        }
    }
}