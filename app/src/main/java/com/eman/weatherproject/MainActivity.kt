package com.eman.weatherproject

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var navigationDrawer: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar
    lateinit var navController: NavController
    lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navigationDrawer = findViewById(R.id.mainDrawer)
        navigationView = findViewById(R.id.mainNavView)
        toolbar = findViewById(R.id.mainToolbar)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        appBarConfig = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.favouriteFragment,
            R.id.settingFragment,
            R.id.alertFragment),navigationDrawer)

        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController,appBarConfig)
        navigationView.setupWithNavController(navController)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}
