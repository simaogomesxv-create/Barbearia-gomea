package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [ClientEntity::class, AppointmentEntity::class, ServiceEntity::class],
  version = 1,
  exportSchema = false
)
abstract class BarberDatabase : RoomDatabase() {
  abstract fun clientDao(): ClientDao
  abstract fun appointmentDao(): AppointmentDao
  abstract fun serviceDao(): ServiceDao

  companion object {
    @Volatile
    private var INSTANCE: BarberDatabase? = null

    fun getDatabase(context: Context): BarberDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          BarberDatabase::class.java,
          "barberpro_database"
        )
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
