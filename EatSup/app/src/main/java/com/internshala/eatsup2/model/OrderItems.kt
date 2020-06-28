package com.internshala.eatsup2.model

import org.json.JSONArray

data class OrderItems(
    val OrderId: String,
    val RestName: String,
    val TotalCost: String,
    val DateTime: String,
    val FoodItems: JSONArray
) {
}