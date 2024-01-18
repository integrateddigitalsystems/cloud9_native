package com.ids.cloud9native.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.ids.cloud9native.R
import com.ids.cloud9native.controller.Fragment.FragmentSignature
import com.ids.cloud9native.controller.MyApplication
import com.ids.cloud9native.controller.base.Fragment.FragmentCompany
import com.ids.cloud9native.controller.base.Fragment.FragmentVisitDetails


class GetLocation(activity:Activity) {
    var context =activity
    var mPermissionResult: ActivityResultLauncher<Array<String>>? = null
    var mLocationManager: LocationManager? = null
    var locationListenerGps: LocationListener? = null
    var LOCATION_REFRESH_DISTANCE = 5
    var LOCATION_REFRESH_TIME = 5000
    fun getLocation(apiListener: ApiListener, fragmentCompany: FragmentCompany?,fragmentVisitDetails: FragmentVisitDetails?,fragmentSignature: FragmentSignature?) : android.location.Location? {
        var currLoc: android.location.Location?=null
        var gps_enabled = false
        var network_enabled = false

        mPermissionResult = if (fragmentCompany!=null)
            fragmentCompany.mPermissionResult
        else if (fragmentVisitDetails!=null) fragmentVisitDetails.mPermissionResult
        else fragmentSignature!!.mPermissionResult

        locationListenerGps = LocationListener { location ->
            if (currLoc==null && location!=null){
                currLoc = location
                apiListener.onDataRetrieved(
                    true,
                    currLoc,
                )
                mLocationManager!!.removeUpdates(locationListenerGps!!)
            }
        }

        safeCall {
            mLocationManager =
                context.getSystemService(Service.LOCATION_SERVICE) as LocationManager
        }
        safeCall {
            gps_enabled = mLocationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        safeCall {
            network_enabled =
                mLocationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        if ((ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    ) && (gps_enabled || network_enabled)
        ) {

//            if (gps_enabled && currLoc==null){
//                currLoc =getLastKnownLocation()
//                if (currLoc==null &&network_enabled){
//                    currLoc =getLastKnownLocation()
                    if (currLoc==null){
                        safeCall {
                            val locationResult = object : MyLocation.LocationResult() {
                                @SuppressLint("MissingPermission")
                                override fun gotLocation(location: Location?) {
                                    currLoc =location
                                    if (currLoc != null) {
                                        apiListener.onDataRetrieved(
                                            true,
                                            currLoc,
                                        )
                                    } else {
                                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location: android.location.Location? ->
                                            currLoc = location
                                            //  currLoc = null
                                            if (currLoc != null) {
                                                apiListener.onDataRetrieved(
                                                    true,
                                                    currLoc,
                                                )
                                            }
                                        }
                                    }


                                }

                            }

                            val myLocation = MyLocation()
                            myLocation.getLocation(context, locationResult)

                        }
                    }
//                }
//            }
        }
        else {
            if (gps_enabled && network_enabled){
                context.createActionDialogCancel(context.getString(R.string.permission_background_android), 0,{  openChooser()},{
                    context.toast(context.getString(R.string.location_updates_disabled))
                })
            }

            else {
                context.createActionDialog(
                    context. getString(R.string.gps_settings), 0
                ) {
                    MyApplication.toSettingsGps =false
                    context.startActivity(
                        Intent(
                            Settings.ACTION_LOCATION_SOURCE_SETTINGS
                        )
                    )
                }
            }

        }
        return currLoc
    }

    fun openChooser() {
        mPermissionResult!!.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }
    @SuppressLint("MissingPermission")
    private fun getLastKnownLocation(): android.location.Location? {
        val providers: List<String> = mLocationManager!!.getProviders(true)
        var bestLocation: android.location.Location? = null
        for (provider in providers) {
            val l: android.location.Location = mLocationManager!!.getLastKnownLocation(provider)
                ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }
        return bestLocation
    }

}