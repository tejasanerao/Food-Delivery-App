package com.internshala.eatsup2.adapter

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.eatsup2.R
import com.internshala.eatsup2.activity.MainActivity
import com.internshala.eatsup2.database.RestaurantDatabase
import com.internshala.eatsup2.database.RestaurantEntity
import com.internshala.eatsup2.fragments.MenuFragment
import com.squareup.picasso.Picasso

class FavoriteRecyclerAdapter(val context: Context, val itemFavList: List<RestaurantEntity>): RecyclerView.Adapter<FavoriteRecyclerAdapter.FavViewHolder>()  {
    class FavViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtFavRestName: TextView = view.findViewById(R.id.txtFavRestName)
        val txtFavPerPrice: TextView = view.findViewById(R.id.txtFavPerPrice)
        val imgFavRestImage: ImageView = view.findViewById(R.id.imgFavRestImage)
        val txtFavRestRating: TextView = view.findViewById(R.id.txtFavRestRating)
        val imgFavFav: ImageView = view.findViewById(R.id.imgFavFav)
        val LlFavcontent: LinearLayout = view.findViewById(R.id.LlFavcontent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_fav_single_row, parent, false)
        return FavViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemFavList.size
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val restFav = itemFavList[position]
        val restId = restFav.restId
        holder.txtFavRestName.text = restFav.restName
        holder.txtFavRestRating.text = restFav.restRating
        holder.txtFavPerPrice.text = restFav.restCostPerOne
        val imageurl = restFav.restImage
        Picasso.get().load(restFav.restImage).into(holder.imgFavRestImage);

        val restaurantEntity = RestaurantEntity(
            restId.toInt(),
            holder.txtFavRestName.text.toString(),
            holder.txtFavRestRating.text.toString(),
            holder.txtFavPerPrice.text.toString(),
            imageurl
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if(isFav)
        {
            holder.imgFavFav.setImageResource(R.drawable.ic_favtrue)
        }
        else
        {
            holder.imgFavFav.setImageResource(R.drawable.ic_fav)
        }

        holder.imgFavFav.setOnClickListener {
            if(!DBAsyncTask(context, restaurantEntity, 1).execute().get())
            {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if(result)
                {
                    Toast.makeText(context, "Added To Fav", Toast.LENGTH_SHORT).show()
                    holder.imgFavFav.setImageResource(R.drawable.ic_favtrue)
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
                    holder.imgFavFav.setImageResource(R.drawable.ic_fav)
                }
                else
                {
                    Toast.makeText(context, "Error occurred in removing To Fav", Toast.LENGTH_SHORT).show()
                }

            }
        }

        holder.LlFavcontent.setOnClickListener()
        {
            val fragment = MenuFragment()
            val args = Bundle()
            args.putString("id", restFav.restId.toString())
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