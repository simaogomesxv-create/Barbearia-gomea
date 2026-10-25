package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "services")
data class ServiceEntity(
  @PrimaryKey val id: String,
  val name: String,
  val priceKz: Long,
  val iconType: String = "hair",
  val isActive: Boolean = true,
  val sortOrder: Int = 0
)

@Dao
interface ServiceDao {
  @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY sortOrder ASC")
  fun getAllActiveServices(): Flow<List<ServiceEntity>>

  @Query("SELECT * FROM services ORDER BY sortOrder ASC")
  fun getAllServices(): Flow<List<ServiceEntity>>

  @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
  suspend fun getServiceById(id: String): ServiceEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertService(service: ServiceEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(services: List<ServiceEntity>)

  @Update
  suspend fun updateService(service: ServiceEntity)

  @Delete
  suspend fun deleteService(service: ServiceEntity)

  @Query("SELECT COUNT(*) FROM services")
  suspend fun getCount(): Int
}
