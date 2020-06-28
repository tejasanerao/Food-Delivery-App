package com.internshala.eatsup2.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.internshala.eatsup2.R
import com.internshala.eatsup2.adapter.FavoriteRecyclerAdapter
import com.internshala.eatsup2.database.RestaurantDatabase
import com.internshala.eatsup2.database.RestaurantEntity

class FavoritesFragment : Fragment() {

    lateinit var recyclerFav: RecyclerView
    lateinit var RlFavs: RelativeLayout
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavoriteRecyclerAdapter
    var dbRestList = listOf<RestaurantEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        recyclerFav = view.findViewById(R.id.recyclerFav)
        layoutManager = LinearLayoutManager(activity as Context)
        RlFavs = view.findViewById(R.id.RlFavs)
        dbRestList = RetrieveFavorites(activity as Context).execute().get()
        if(dbRestList.size == 0)
        {
            RlFavs.visibility = View.VISIBLE
        }
        else
        {
            RlFavs.visibility = View.GONE
        }
        if (activity != null) {
            recyclerAdapter = FavoriteRecyclerAdapter(
                activity as Context,
                dbRestList
            )
            recyclerFav.adapter = recyclerAdapter
            recyclerFav.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavorites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db =
                Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db")
                    .build()
            return db.restaurantDao().getallRestaurants()
        }

    }

}
