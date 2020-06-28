package com.internshala.eatsup2.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {

    @Insert
    fun insertorder(cartEntity: CartEntity)

    @Delete
    fun deleteorder(cartEntity: CartEntity)

    @Query("DELETE FROM cart1 WHERE rest_id = :restId")
    fun deleteAll(restId:String)

    @Query("DELETE FROM cart1")
    fun delete()

    @Query("SELECT * FROM cart1")
    fun getallitems(): List<CartEntity>

    @Query("DELETE FROM cart1 WHERE rest_id = :restId")
    fun deleteOrders(restId: String)

    @Query("SELECT * FROM cart1 WHERE itemId = :itemid")
    fun getItemById(itemid: String): CartEntity

}