package com.internshala.eatsup2.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {

    @Insert
    fun insertRestaurant(restEntity: RestaurantEntity)

    @Delete
    fun deleteRestaurant(restEntity: RestaurantEntity)

    @Query("DELETE FROM restaurants")
    fun deleteallrests()

    @Query("SELECT * FROM restaurants")
    fun getallRestaurants(): List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE restId = :restid")
    fun getRestById(restid: String): RestaurantEntity

}