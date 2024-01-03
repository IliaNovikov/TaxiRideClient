package com.novikov.taxixml.presentation.view.activity

import android.Manifest
import android.Manifest.permission_group.SMS
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.novikov.taxixml.R
import com.novikov.taxixml.databinding.ActivityMainBinding
import com.novikov.taxixml.singleton.NavigationController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment

        NavigationController.navHost = navHostFragment.navController

        if (FirebaseAuth.getInstance().currentUser != null){
            Log.i("firebaseUser", FirebaseAuth.getInstance().currentUser?.uid.toString())
            NavigationController.navHost.navigate(R.id.mainFragment)
            Toast.makeText(this, FirebaseAuth.getInstance().currentUser?.phoneNumber, Toast.LENGTH_LONG).show()
        }
        else{
            NavigationController.navHost.navigate(R.id.authorizationViaPhoneFragment)
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