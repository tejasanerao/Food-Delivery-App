package com.internshala.eatsup2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.eatsup2.R
import com.internshala.eatsup2.model.OrderFoodItems
import com.internshala.eatsup2.model.OrderItems

class OrderHistoryInnerRecyclerAdapter(val context: Context, val orderItemList: ArrayList<OrderFoodItems>): RecyclerView.Adapter<OrderHistoryInnerRecyclerAdapter.OrderInnerViewHolder>() {

    class OrderInnerViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val txtFoodItem: TextView = view.findViewById(R.id.txtFoodItem)
        val txtOrderedItemPrice: TextView = view.findViewById(R.id.txtOrderedItemPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderInnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_orderitems_single_row, parent, false)
        return OrderInnerViewHolder(view)
    }


    override fun getItemCount(): Int {
        return orderItemList.size
    }

    override fun onBindViewHolder(
        holder: OrderHistoryInnerRecyclerAdapter.OrderInnerViewHolder,
        position: Int
    ) {
        val itemlist = orderItemList[position]
        holder.txtFoodItem.text = itemlist.Item
        val cost = "Rs."+ itemlist.ItemPrice
        holder.txtOrderedItemPrice.text = cost
    }
}