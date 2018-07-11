package com.draco.bixback

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.TextView
import android.text.TextUtils




/**
 * Created by Draco on 2/16/2018.
 */

class MainActivity : AppCompatActivity() {

    lateinit var back: RadioButton
    lateinit var home: RadioButton
    lateinit var recent: RadioButton
    lateinit var notifications: RadioButton
    lateinit var quick_settings: RadioButton
    lateinit var power_dialog: RadioButton
    lateinit var toggle_split_screen: RadioButton
    lateinit var flash: RadioButton
    lateinit var open_accessibility: Button
    lateinit var restart: Button
    lateinit var assistant: RadioButton
    lateinit var service_description: TextView

    lateinit var prefs: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        open_accessibility = findViewById(R.id.open_accessibility)

        back = findViewById(R.id.back)
        home = findViewById(R.id.home)
        recent = findViewById(R.id.recent)
        notifications = findViewById(R.id.notifications)
        quick_settings = findViewById(R.id.quick_settings)
        power_dialog = findViewById(R.id.power_dialog)
        toggle_split_screen = findViewById(R.id.toggle_split_screen)
        flash = findViewById(R.id.flash)
        assistant = findViewById(R.id.assistant)
        service_description = findViewById(R.id.service_description)
        restart = findViewById(R.id.restart)

        prefs = getSharedPreferences("bixBackPrefs", Context.MODE_PRIVATE)
        editor = prefs.edit()

        val currentAction = prefs.getString("action", "back")

        when (currentAction) {
            "back" -> back.isChecked = true
            "home" -> home.isChecked = true
            "recent" -> recent.isChecked = true
            "notifications" -> notifications.isChecked = true
            "quick_settings" -> quick_settings.isChecked = true
            "power_dialog" -> power_dialog.isChecked = true
            "toggle_split_screen" -> toggle_split_screen.isChecked = true
            "flash" -> flash.isChecked = true
            "assistant" -> assistant.isChecked = true
        }

        open_accessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        restart.setOnClickListener {
            finishAndRemoveTask()
            System.exit(0)
        }

        back.setOnClickListener { radioButtonPressed(it) }
        home.setOnClickListener { radioButtonPressed(it) }
        recent.setOnClickListener { radioButtonPressed(it) }
        notifications.setOnClickListener { radioButtonPressed(it) }
        quick_settings.setOnClickListener { radioButtonPressed(it) }
        power_dialog.setOnClickListener { radioButtonPressed(it) }
        toggle_split_screen.setOnClickListener { radioButtonPressed(it) }
        flash.setOnClickListener { radioButtonPressed(it) }
        assistant.setOnClickListener { radioButtonPressed(it) }
    }

    override fun onResume() {
        if (isAccessGranted()) {
            service_description.visibility = View.GONE
            restart.visibility = View.GONE
        } else {
            service_description.visibility = View.VISIBLE
            restart.visibility = View.VISIBLE
        }

        if (isAccessibilityEnabled()) {
            open_accessibility.visibility = View.GONE
        } else {
            open_accessibility.visibility = View.VISIBLE
        }
        super.onResume()
    }

    private fun radioButtonPressed(view: View) {
        when (view.id) {
            R.id.back -> editor.putString("action", "back")
            R.id.home -> editor.putString("action", "home")
            R.id.recent -> editor.putString("action", "recent")
            R.id.notifications -> editor.putString("action", "notifications")
            R.id.quick_settings -> editor.putString("action", "quick_settings")
            R.id.power_dialog -> editor.putString("action", "power_dialog")
            R.id.toggle_split_screen -> editor.putString("action", "toggle_split_screen")
            R.id.flash -> editor.putString("action", "flash")
            R.id.assistant -> editor.putString("action", "assistant")
        }
        editor.apply()
    }


    private fun isAccessGranted(): Boolean {
        if (packageManager.checkPermission(android.Manifest.permission.READ_LOGS, packageName) != PackageManager.PERMISSION_GRANTED) {
            Log.d("b", "we do not have the READ_LOGS permission!")
            return false

        } else
            Log.d("b", "we have the READ_LOGS permission already!")
        return true
    }

    private fun isAccessibilityEnabled():Boolean {
        var accessibilityEnabled = 0
            val service = packageName + "/" + AccessibilityService::class.java.canonicalName
        try {
            accessibilityEnabled = Settings.Secure.getInt(applicationContext.contentResolver,
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }

        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')

        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(applicationContext.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()

                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        } else {
            return false
        }

        return false
    }
}