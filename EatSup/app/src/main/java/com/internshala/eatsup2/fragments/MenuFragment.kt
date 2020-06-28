package com.internshala.eatsup2.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.eatsup2.R
import com.internshala.eatsup2.activity.CartActivity
import com.internshala.eatsup2.adapter.MenuRecyclerAdapter
import com.internshala.eatsup2.database.CartDatabase
import com.internshala.eatsup2.database.CartEntity
import com.internshala.eatsup2.model.Menu
import com.internshala.eatsup2.util.ConnectionManager
import kotlinx.android.synthetic.main.recycler_home_single_row.*
import java.lang.Exception

class MenuFragment : Fragment() {

    lateinit var recyclerMenu: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val menuInfoList = arrayListOf<Menu>()
    lateinit var btnGoToCart: Button
    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val args = arguments
        val restname = args?.getString("restname")
        val restName = restname.toString()
        (activity as AppCompatActivity).supportActionBar?.title = restName
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerMenu = view.findViewById(R.id.recyclerHome)
        btnGoToCart = view.findViewById(R.id.btnGoToCart)
        btnGoToCart.visibility = View.GONE
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)

        val args = arguments
        val id = args?.getString("id")
        val restname = args?.getString("restname")
        val restName = restname.toString()

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val resArr = data.getJSONArray("data")
                            for (i in 0 until resArr.length()) {
                                val menuJsonObject = resArr.getJSONObject(i)
                                val menuObject = Menu(
                                    menuJsonObject.getString("id"),
                                    menuJsonObject.getString("name"),
                                    menuJsonObject.getString("cost_for_one"),
                                    menuJsonObject.getString("restaurant_id")
                                )
                                menuInfoList.add(menuObject)
                                recyclerAdapter =
                                    MenuRecyclerAdapter(activity as Context, menuInfoList, restName, btnGoToCart)
                                recyclerMenu.adapter = recyclerAdapter
                                recyclerMenu.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(context, "Some Error Occurred", Toast.LENGTH_SHORT)
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

        btnGoToCart.setOnClickListener {
            val intent = Intent(activity as Context, CartActivity::class.java)
            intent.putExtra("RestName", restName)
            startActivity(intent)
        }

        return view
    }
}
