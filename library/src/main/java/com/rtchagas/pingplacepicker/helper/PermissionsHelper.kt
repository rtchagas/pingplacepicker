package com.rtchagas.pingplacepicker.helper

import android.Manifest
import android.app.Activity
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.BasePermissionListener
import com.karumi.dexter.listener.single.CompositePermissionListener
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.rtchagas.pingplacepicker.R


object PermissionsHelper {

    fun checkForLocationPermission(activity: Activity, listener: BasePermissionListener?) {

        val dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                .withContext(activity)
                .withTitle(R.string.permission_fine_location_title)
                .withMessage(R.string.permission_fine_location_message)
                .withButtonText(android.R.string.ok)
                .withIcon(R.drawable.ic_map_marker_radius_black_24dp)
                .build()

        val compositeListener =
                if (listener != null) {
                    CompositePermissionListener(dialogPermissionListener, listener)
                }
                else {
                    CompositePermissionListener(dialogPermissionListener)
                }

        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(compositeListener)
                .check()
    }
}
