package com.internshala.eatsup2.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CartEntity::class], version = 2)
abstract class CartDatabase: RoomDatabase()
{
    abstract fun cartDao(): CartDao
}