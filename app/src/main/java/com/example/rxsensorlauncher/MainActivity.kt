package com.example.rxsensorlauncher

import android.annotation.SuppressLint
import android.content.Context

import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import androidx.preference.PreferenceManager

import android.content.Intent
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat

class MainActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context: Context = applicationContext;

        val prefs = PreferenceManager.getDefaultSharedPreferences(context)


        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.addToBackStack(null)

        var bundle: Bundle = Bundle()
        bundle.putString("Message", "Fragment Test")

        fragmentTransaction.replace(R.id.container, SettingsFragment.newInstance(bundle))
        fragmentTransaction.commit()

        var intent: Intent = Intent(application, SensorService::class.java)
        intent.putExtra("REQUEST_CODE", 1234)
        startForegroundService(intent)
    }
}
