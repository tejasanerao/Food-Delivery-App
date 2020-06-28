package com.internshala.eatsup2.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.internshala.eatsup2.R
import com.internshala.eatsup2.database.CartDatabase
import com.internshala.eatsup2.database.CartEntity
import com.internshala.eatsup2.database.RestaurantDatabase
import com.internshala.eatsup2.fragments.*


class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousMenuItem: MenuItem? = null
    lateinit var txtHeaderName: TextView
    lateinit var txtHeaderPhone: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        navigationView = findViewById(R.id.navigationView)
        val header = navigationView.getHeaderView(0)
        txtHeaderName = header.findViewById(R.id.txtHeaderName)
        txtHeaderPhone = header.findViewById(R.id.txtHeaderPhone)
        txtHeaderName.text = sharedPreferences.getString("user_name", null)
        val phone = "+91-" + sharedPreferences.getString("user_mobile_number", null)
        txtHeaderPhone.text = phone
        callHome()
        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, HomeFragment()).commit()
                    supportActionBar?.title = "Home"
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, ProfileFragment()).commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.fav -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FavoritesFragment()).commit()
                    supportActionBar?.title = "Favorite Restaurants"
                    drawerLayout.closeDrawers()
                }
                R.id.orders -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, OrdersFragment()).commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faqs -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, FAQsFragment()).commit()
                    supportActionBar?.title = "Frequently Asked Questions"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    val dialog = AlertDialog.Builder(this@MainActivity)
                    dialog.setTitle("LOGOUT")
                    dialog.setMessage("Do you want to continue?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        deleterestDBAsync(this@MainActivity).execute()
                        sharedPreferences.edit().clear().apply()
                        sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                        startActivity(intent)
                        finish()
                    }

                    dialog.setNegativeButton("No") { text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "HomeFragment"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            is MenuFragment -> {
                setUpToolbar()
                val actionBarDrawerToggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    R.string.open_drawer,
                    R.string.close_drawer
                )
                drawerLayout.addDrawerListener(actionBarDrawerToggle)
                actionBarDrawerToggle.syncState()
                MainActivity.deleteDBAsync(this@MainActivity).execute()
                callHome()
            }
            !is HomeFragment -> {
                setUpToolbar()
                val actionBarDrawerToggle = ActionBarDrawerToggle(
                    this@MainActivity,
                    drawerLayout,
                    R.string.open_drawer,
                    R.string.close_drawer
                )
                drawerLayout.addDrawerListener(actionBarDrawerToggle)
                actionBarDrawerToggle.syncState()
                callHome()
            }
            else -> super.onBackPressed()
        }
    }

    fun callHome() {
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout, HomeFragment()).commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
    }

    class deleteDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "cart1-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().delete()
            db.close()
            return true
        }

    }

    class deleterestDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.restaurantDao().deleteallrests()
            db.close()
            return true
        }

    }

}
