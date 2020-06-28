package com.internshala.eatsup2.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.eatsup2.R
import com.internshala.eatsup2.adapter.CartRecyclerAdapter
import com.internshala.eatsup2.database.CartDatabase
import com.internshala.eatsup2.database.CartEntity
import com.internshala.eatsup2.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var recyclerCart: RecyclerView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtCartRestName: TextView
    lateinit var txtCartTotalCost: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var RlCart: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    var dbList = listOf<CartEntity>()
    val orderList = arrayListOf<CartEntity>()
    var sum = 0
    var restname: String? = "name"
    lateinit var recyclerAdapter: CartRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        recyclerCart = findViewById(R.id.recyclerCart)
        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        RlCart = findViewById(R.id.RlCart)
        layoutManager = LinearLayoutManager(applicationContext)
        txtCartRestName = findViewById(R.id.txtCartRestName)
        txtCartTotalCost = findViewById(R.id.txtCartTotalCost)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        dbList = RetrieveOrders(applicationContext).execute().get()
        if(dbList.size == 0)
        {
            txtCartRestName.visibility = View.GONE
            txtCartTotalCost.visibility = View.GONE
            btnPlaceOrder.visibility = View.GONE
            RlCart.visibility = View.VISIBLE
        }
        else
        {
            txtCartRestName.visibility = View.VISIBLE
            txtCartTotalCost.visibility = View.VISIBLE
            btnPlaceOrder.visibility = View.VISIBLE
            RlCart.visibility = View.GONE
        }
        for (i in dbList) {
            orderList.add(i)
        }
        for (i in 0 until orderList.size) {
            sum += orderList[i].itemcost.toInt()
        }

        if (intent != null) {
            restname = intent.getStringExtra("RestName")
        }

        txtCartRestName.text = restname
        val cost = "Total Cost: Rs.$sum"
        txtCartTotalCost.text = cost

        recyclerAdapter = CartRecyclerAdapter(applicationContext, orderList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

        btnPlaceOrder.setOnClickListener {
            val userId = sharedPreferences.getString("user_id", null)
            val queue = Volley.newRequestQueue(this@CartActivity)
            val jsonParams = JSONObject()
            jsonParams.put("user_id", userId)
            jsonParams.put("total_cost", sum)
            val restid = dbList[0].restId.toString()
            jsonParams.put("restaurant_id", restid)
            val foodArray = JSONArray()
            for (i in 0 until orderList.size) {
                val itemId = JSONObject()
                itemId.put("food_item_id", orderList[i].itemId)
                foodArray.put(i, itemId)
            }

            jsonParams.put("food", foodArray)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            if (ConnectionManager().checkConnectivity(this@CartActivity)) {
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val clearCart =
                                        ClearDBAsync(applicationContext, restid).execute().get()
                                    val intent = Intent(this@CartActivity, OrderPlaced::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(
                                        this@CartActivity,
                                        "Some Error Occurred",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@CartActivity,
                                    "Some Error Occurred in Try",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@CartActivity,
                                "Some Error Occurred in Response",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["content-type"] = "application/json"
                            headers["token"] = "9e730f1070405c"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } else {
                val dialog = AlertDialog.Builder(this@CartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                }

                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@CartActivity)
                }
                dialog.create()
                dialog.show()
            }
        }
    }


    class RetrieveOrders(val context: Context) :
        AsyncTask<Void, Void, List<CartEntity>>() {
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val db =
                Room.databaseBuilder(context, CartDatabase::class.java, "cart1-db")
                    .build()
            return db.cartDao().getallitems()
        }
    }

    class ClearDBAsync(context: Context, val resId: String) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "cart1-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    class deleteDBAsync(context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, CartDatabase::class.java, "cart1-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.cartDao().delete()
            db.close()
            return true
        }
    }
}
