package com.maelfosso.bleck.iotremotecontrol

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.MediaController
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.ui.*
import androidx.preference.PreferenceManager

class StartActivity : AppCompatActivity() , NavigationView.OnNavigationItemSelectedListener {
    companion object {
        public var TAG: String = javaClass.name

        fun start(context: Context) {
            val intent = Intent(context, StartActivity::class.java).apply {}
            context.startActivity(intent)
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController;
    private lateinit var navView: NavigationView;
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_board, R.id.nav_bluetooth, R.id.nav_settings
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener(this)

        // Set settings values
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.

        menuInflater.inflate(R.menu.start, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        invalidateOptionsMenu()

        menu?.findItem(R.id.action_connection)?.setIcon(
            if (IOTApplication.wrappedArduinoDevice == null)
                R.drawable.ic_bluetooth_disabled_24px
            else
                R.drawable.ic_bluetooth_connected_24px
        )
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_connection -> {
                BluetoothConnectionActivity.start(this)
                true
            }
            R.id.action_settings -> {
                SettingsActivity.start(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val handled = NavigationUI.onNavDestinationSelected(item, navController)
        Log.d(TAG, "onNavigationItemSelected - $handled")

        if (!handled) {
            when (item.itemId) {
                R.id.nav_settings -> {
                    Toast.makeText(this, "Settings activity", Toast.LENGTH_SHORT).show();
                    SettingsActivity.start(this);
                }
                R.id.nav_bluetooth -> {
                    Toast.makeText(this, "Bluetooth activity", Toast.LENGTH_SHORT).show();
                    BluetoothConnectionActivity.start(this);
                }
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    override fun onDestroy() {
        IOTApplication.close()

        super.onDestroy()
    }
}