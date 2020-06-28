package com.internshala.eatsup2.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.eatsup2.R
import com.internshala.eatsup2.activity.CartActivity
import com.internshala.eatsup2.database.CartDatabase
import com.internshala.eatsup2.database.CartEntity
import com.internshala.eatsup2.fragments.MenuFragment
import com.internshala.eatsup2.model.Menu

class MenuRecyclerAdapter(
    val context: Context,
    val menuList: ArrayList<Menu>,
    val restname: String,
    val btnGoToCart: Button
) : RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {

    var count: Int = 0
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtItem: TextView = view.findViewById(R.id.txtItem)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddtoCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu = menuList[position]
        holder.txtItem.text = menu.itemName
        val cost = "Rs." + menu.itemPrice
        holder.txtPrice.text = cost


        val cartEntity = CartEntity(
            menu.itemID.toInt() as Int,
            menu.itemName,
            menu.itemPrice,
            menu.restID
        )
        val checkCart = DBAsyncTask(context, cartEntity, 1).execute()
        val isCart = checkCart.get()
        if(isCart)
        {
            holder.btnAddToCart.text = "Add"
            holder.btnAddToCart.setBackgroundResource(R.drawable.btn2)
            deleteDBAsync(context).execute().get()
        }

        holder.btnAddToCart.setOnClickListener {
            if (!DBAsyncTask(context, cartEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, cartEntity, 2).execute()
                val result = async.get()
                count++
                if (result) {
                    holder.btnAddToCart.text = "Remove"
                    holder.btnAddToCart.setBackgroundResource(R.drawable.btn3)
                } else {
                    Toast.makeText(context, "Error occurred in adding To db", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                val async = DBAsyncTask(context, cartEntity, 3).execute()
                val result = async.get()
                count--
                if (result) {
                    holder.btnAddToCart.text = "Add"
                    holder.btnAddToCart.setBackgroundResource(R.drawable.btn2)
                } else {
                    Toast.makeText(
                        context,
                        "Error occurred in removing from db",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            if(count == 0)
            {
                btnGoToCart.visibility = View.GONE
            }
            else
            {
                btnGoToCart.visibility = View.VISIBLE
            }
        }
    }


    class DBAsyncTask(val context: Context, val cartEntity: CartEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        val db =
            Room.databaseBuilder(context, CartDatabase::class.java, "cart1-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val order: CartEntity? = db.cartDao().getItemById(cartEntity.itemId.toString())
                    db.close()
                    return order != null
                }
                2 -> {
                    db.cartDao().insertorder(cartEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.cartDao().deleteorder(cartEntity)
                    db.close()
                    return true
                }
            }
            return false
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