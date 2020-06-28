package com.internshala.eatsup2.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.internshala.eatsup2.R
import com.internshala.eatsup2.model.Menu
import com.internshala.eatsup2.model.OrderFoodItems
import com.internshala.eatsup2.model.OrderItems

class OrderHistoryRecyclerAdapter(val context: Context, val orderList: ArrayList<OrderItems>) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtOrdersRestName: TextView = view.findViewById(R.id.txtOrdersRestName)
        val txtDateTime: TextView = view.findViewById(R.id.txtDateTime)
        val txtTotalCost: TextView = view.findViewById(R.id.txtOrderTotalCost)
        val innerRecyclerOrderHistory: RecyclerView = view.findViewById(R.id.innerRecyclerOrderHistory)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_orders_single_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun setUpRecycler(innerRecyclerOrderHistory: RecyclerView, orderList: OrderItems) {
        val foodItemsList = ArrayList<OrderFoodItems>()
        for (i in 0 until orderList.FoodItems.length()) {
            val foodJson = orderList.FoodItems.getJSONObject(i)
            foodItemsList.add(
                OrderFoodItems(
                    foodJson.getString("food_item_id"),
                    foodJson.getString("name"),
                    foodJson.getString("cost")
                )
            )
        }
        val orderItemAdapter = OrderHistoryInnerRecyclerAdapter(context,foodItemsList)
        val layoutManager = LinearLayoutManager(context)
        innerRecyclerOrderHistory.layoutManager = layoutManager
        innerRecyclerOrderHistory.adapter = orderItemAdapter
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orderList[position]
        holder.txtOrdersRestName.text = order.RestName
        val datetime = "Order placed at: "+ order.DateTime
        holder.txtDateTime.text = datetime
        val totalcost = order.TotalCost
        holder.txtTotalCost.text = "Total: Rs."+totalcost
        setUpRecycler(holder.innerRecyclerOrderHistory, order)
    }
}