package com.novikov.taxixml.data

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Point
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.novikov.taxixml.domain.model.Position
import com.novikov.taxixml.domain.repository.UserPositionRepository
import java.lang.Exception

class UserPositionRepositoryImpl(private val context: Context) : UserPositionRepository {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override suspend fun getPosition(): Position {
        val lastPosition = if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
        {
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        }
        else
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        return Position(lastPosition?.latitude!!, lastPosition.longitude)

    }

    override suspend fun setPosition(position: Position) {
        TODO("Not yet implemented")
    }
}