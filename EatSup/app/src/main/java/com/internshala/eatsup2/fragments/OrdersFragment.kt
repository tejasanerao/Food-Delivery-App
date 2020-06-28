package com.internshala.eatsup2.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.internshala.eatsup2.R
import com.internshala.eatsup2.activity.MainActivity
import com.internshala.eatsup2.adapter.OrderHistoryRecyclerAdapter
import com.internshala.eatsup2.model.OrderItems
import com.internshala.eatsup2.util.ConnectionManager

class OrdersFragment : Fragment() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val OrderInfoList = arrayListOf<OrderItems>()
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var RlOrders: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        sharedPreferences = this.context!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val UserId = sharedPreferences.getString("user_id", null)
        recyclerMenu = view.findViewById(R.id.recyclerOrderHistory)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        RlOrders = view.findViewById(R.id.RlOrders)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$UserId"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val orderArr = data.getJSONArray("data")
                            if(orderArr.length() == 0)
                            {
                                RlOrders.visibility = View.VISIBLE
                            }
                            else
                            {
                                RlOrders.visibility = View.GONE
                            }
                            for (i in 0 until orderArr.length()) {
                                val orderJsonObject = orderArr.getJSONObject(i)
                                val orderObject = OrderItems(
                                    orderJsonObject.getString("order_id"),
                                    orderJsonObject.getString("restaurant_name"),
                                    orderJsonObject.getString("total_cost"),
                                    orderJsonObject.getString("order_placed_at"),
                                    orderJsonObject.getJSONArray("food_items")
                                )
                                OrderInfoList.add(orderObject)
                                recyclerAdapter =
                                    OrderHistoryRecyclerAdapter(activity as Context, OrderInfoList)
                                recyclerMenu.adapter = recyclerAdapter
                                recyclerMenu.layoutManager = layoutManager
                            }

                        } else {
                            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Some Error Occurred in Try", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(context, "Some Error Occurred in Response", Toast.LENGTH_SHORT)
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity(Activity())
            }
            dialog.create()
            dialog.show()
        }


        return view
    }

}
