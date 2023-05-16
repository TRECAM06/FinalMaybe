package com.app.finalmaybe

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest

class SettingsActivity : AppCompatActivity() {

    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val filePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val permissionRequestCode = 101

    private lateinit var locationSwitch: android.widget.Switch
    private lateinit var fileSwitch: android.widget.Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        locationSwitch = findViewById(R.id.location_switch)
        fileSwitch = findViewById(R.id.file_switch)

        locationSwitch.isChecked = hasPermission(locationPermission)
        fileSwitch.isChecked = hasPermission(filePermission)

        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            handlePermissionChange(locationPermission, isChecked)
        }
        fileSwitch.setOnCheckedChangeListener { _, isChecked ->
            handlePermissionChange(filePermission, isChecked)
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun handlePermissionChange(permission: String, isChecked: Boolean) {
        if (isChecked) {
            requestPermission(permission)
        } else {
            revokePermission(permission)
        }
    }

    private fun requestPermission(permission: String) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), permissionRequestCode)
    }

    private fun revokePermission(permission: String) {
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
            if (permissions.contains(locationPermission)) {
                locationSwitch.isChecked = hasPermission(locationPermission)
            }
            if (permissions.contains(filePermission)) {
                fileSwitch.isChecked = hasPermission(filePermission)
            }
        }
    }
}