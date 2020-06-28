package com.internshala.eatsup2.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import com.internshala.eatsup2.R
import com.internshala.eatsup2.fragments.HomeFragment
import com.internshala.eatsup2.fragments.MenuFragment

class OrderPlaced : AppCompatActivity() {

    lateinit var btnOrderPlaced: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnOrderPlaced = findViewById(R.id.btnOrderPlaced)

        btnOrderPlaced.setOnClickListener {
            val intent = Intent(this@OrderPlaced, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frameLayout)
        when (frag) {
            !is HomeFragment -> {
                val intent = Intent(this@OrderPlaced, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else -> super.onBackPressed()
        }
    }
}
