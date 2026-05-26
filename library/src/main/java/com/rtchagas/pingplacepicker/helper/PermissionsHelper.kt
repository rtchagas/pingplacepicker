package com.rtchagas.pingplacepicker.helper

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.rtchagas.pingplacepicker.R

internal object PermissionsHelper {

    /**
     * Shows the rationale dialog explaining why location is needed. The dialog
     * is informational only — it does not request the permission (that happens
     * via [androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions]).
     */
    fun showLocationRationaleDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle(R.string.permission_fine_location_title)
            .setMessage(R.string.permission_fine_location_message)
            .setIcon(R.drawable.ic_map_marker_radius_black_24dp)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }
}
