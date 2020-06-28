package com.internshala.eatsup2.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.internshala.eatsup2.R
import com.internshala.eatsup2.activity.MainActivity
import com.internshala.eatsup2.database.RestaurantDatabase
import com.internshala.eatsup2.database.RestaurantEntity
import com.internshala.eatsup2.fragments.HomeFragment
import com.internshala.eatsup2.fragments.MenuFragment
import com.internshala.eatsup2.model.Restaurants
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurants>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtRestName)
        val txtPerPrice: TextView = view.findViewById(R.id.txtPerPrice)
        val imgRestImage: ImageView = view.findViewById(R.id.imgRestImage)
        val txtRating: TextView = view.findViewById(R.id.txtRestRating)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
        val Llcontent: LinearLayout = view.findViewById(R.id.Llcontent)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val rest = itemList[position]
        val restId = rest.RestId
        val imageurl = rest.RestImage
        holder.txtRestName.text = rest.RestName
        val price = rest.RestCostPerOne+"/person"
        holder.txtPerPrice.text = price
        holder.txtRating.text = rest.RestRating
        Picasso.get().load(rest.RestImage).into(holder.imgRestImage);

        val restaurantEntity = RestaurantEntity(
            restId.toInt(),
            holder.txtRestName.text.toString(),
            holder.txtRating.text.toString(),
            holder.txtPerPrice.text.toString(),
            imageurl
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if(isFav)
        {
            holder.imgFav.setImageResource(R.drawable.ic_favtrue)
        }
        else
        {
            holder.imgFav.setImageResource(R.drawable.ic_fav)
        }

        holder.imgFav.setOnClickListener {
            if(!DBAsyncTask(context, restaurantEntity, 1).execute().get())
            {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context, "Added To Fav", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_favtrue)
                }
                else
                {
                    Toast.makeText(context, "Error occurred in adding To Fav", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context, "Removed To Fav", Toast.LENGTH_SHORT).show()
                    holder.imgFav.setImageResource(R.drawable.ic_fav)
                }
                else
                {
                    Toast.makeText(context, "Error occurred in removing To Fav", Toast.LENGTH_SHORT).show()
                }

            }
        }

        holder.Llcontent.setOnClickListener()
        {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putString("id", rest.RestId)
            args.putString("restname", rest.RestName)
            fragment.arguments = args
            val transaction = (context as MainActivity).supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frameLayout, fragment)
            transaction.commit()
        }
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurants-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? =
                        db.restaurantDao().getRestById(restaurantEntity.restId.toString())
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}