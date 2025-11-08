package com.dbApp.approom.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Compra::class], version = 1, exportSchema = false)
abstract class CompraDatabase : RoomDatabase() {
    abstract fun compraDao(): CompraDao

    companion object {
        @Volatile
        private var INSTANCE: CompraDatabase? = null

        fun getDatabase(context: Context): CompraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CompraDatabase::class.java,
                    "compra_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

