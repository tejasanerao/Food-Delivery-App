package com.internshala.eatsup2.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName="restaurants")
data class RestaurantEntity(
    @PrimaryKey val restId: Int,
    @ColumnInfo(name = "rest_name") val restName: String,
    @ColumnInfo(name ="rest_rating") val restRating: String,
    @ColumnInfo(name ="rest_cost_per_one") val restCostPerOne:String,
    @ColumnInfo(name= "rest_image") val restImage:String
)
{

}