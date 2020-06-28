package com.internshala.eatsup2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="cart1")
data class CartEntity(
    @PrimaryKey val itemId: Int,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "item_cost") val itemcost: String,
    @ColumnInfo(name = "rest_id") val restId: String
) {
}