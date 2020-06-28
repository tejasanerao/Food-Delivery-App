package com.internshala.eatsup2.fragments

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.eatsup2.R
import com.internshala.eatsup2.adapter.HomeRecyclerAdapter
import com.internshala.eatsup2.database.RestaurantDatabase
import com.internshala.eatsup2.database.RestaurantEntity
import com.internshala.eatsup2.model.Restaurants
import com.internshala.eatsup2.util.ConnectionManager
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    val restInfoList = arrayListOf<Restaurants>()
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var costComparator = Comparator<Restaurants>
    { rest1, rest2 ->
        if (rest1.RestCostPerOne.compareTo(rest2.RestCostPerOne, true) == 0) {
            rest1.RestName.compareTo(rest2.RestName, true)
        } else {
            rest1.RestCostPerOne.compareTo(rest2.RestCostPerOne, true)
        }
    }

    var ratingComparator = Comparator<Restaurants>
    { rest1, rest2 ->
        if (rest1.RestRating.compareTo(rest2.RestRating, true) == 0) {
            rest1.RestName.compareTo(rest2.RestName, true)
        } else {
            rest1.RestRating.compareTo(rest2.RestRating, true)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE
        progressBar = view.findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            progressLayout.visibility = View.GONE
                            val resArr = data.getJSONArray("data")
                            for (i in 0 until resArr.length()) {
                                val restJsonObject = resArr.getJSONObject(i)
                                val restObject = Restaurants(
                                    restJsonObject.getString("id"),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("rating"),
                                    restJsonObject.getString("cost_for_one"),
                                    restJsonObject.getString("image_url")
                                )
                                restInfoList.add(restObject)
                                recyclerAdapter =
                                    HomeRecyclerAdapter(activity as Context, restInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.sort) {
            val dialog = android.app.AlertDialog.Builder(activity as Context)
            var title = dialog.setTitle("Sort By?")
            val listItems = arrayOf(
                "Cost(High to low)",
                "Cost(Low to low)",
                "Ratings(High to Low)",
                "Ratings(Low to High)"
            )
            dialog.setSingleChoiceItems(
                listItems,
                -1,
                DialogInterface.OnClickListener() { dialog, which ->
                    when (which) {
                        0 -> {
                            Collections.sort(restInfoList, costComparator)
                            restInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                            dialog.cancel()
                        }
                        1 -> {
                            Collections.sort(restInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged()
                            dialog.cancel()
                        }
                        2 -> {
                            Collections.sort(restInfoList, ratingComparator)
                            restInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged()
                            dialog.cancel()
                        }
                        3 -> {
                            Collections.sort(restInfoList, ratingComparator)
                            recyclerAdapter.notifyDataSetChanged()
                            dialog.cancel()
                        }
                    }
                })
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}
