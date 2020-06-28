package com.internshala.eatsup2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.eatsup2.R
import com.internshala.eatsup2.database.CartEntity
import com.internshala.eatsup2.model.CartItems
import com.internshala.eatsup2.model.Menu

class CartRecyclerAdapter(val context: Context, val cartList: ArrayList<CartEntity>): RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val txtCartFoodItem: TextView = view.findViewById(R.id.txtCartFoodItem)
        val txtCartItemPrice: TextView = view.findViewById(R.id.txtCartItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]
        holder.txtCartFoodItem.text = item.itemName
        val cost = "Rs."+item.itemcost
        holder.txtCartItemPrice.text = cost
    }

}