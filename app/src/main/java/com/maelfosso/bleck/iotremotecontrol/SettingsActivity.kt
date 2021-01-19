package com.maelfosso.bleck.iotremotecontrol

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceManager
import com.maelfosso.bleck.iotremotecontrol.ui.settings.SettingsFragment

class SettingsActivity : AppCompatActivity() {

    companion object {
        const val KEY_1: String = "0001"
        var TAG: String = javaClass.name

        fun start(context: Context) {
            val intent = Intent(context, SettingsActivity::class.java).apply {}
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.beginTransaction()
            .replace(R.id.content_settings, SettingsFragment())
            .commit();
//
//        val sharedPref: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed();
        return true;
    }
}