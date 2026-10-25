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

@Entity(tableName = "appointments")
data class AppointmentEntity(
  @PrimaryKey val id: String,
  val clientId: String,
  val clientName: String,
  val serviceId: String,
  val serviceName: String,
  val servicePrice: Long,
  val extraValue: Long = 0L,
  val totalPrice: Long = servicePrice + extraValue,
  val dateYmd: String, // format "YYYY-MM-DD"
  val timeHm: String,  // format "HH:mm"
  val status: String = "CONCLUIDO", // "CONCLUIDO", "AGENDADO", "CANCELADO"
  val paymentMethod: String = "Multicaixa Express",
  val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface AppointmentDao {
  @Query("SELECT * FROM appointments ORDER BY dateYmd DESC, timeHm DESC")
  fun getAllAppointments(): Flow<List<AppointmentEntity>>

  @Query("SELECT * FROM appointments WHERE dateYmd = :dateYmd ORDER BY timeHm ASC")
  fun getAppointmentsByDate(dateYmd: String): Flow<List<AppointmentEntity>>

  @Query("SELECT * FROM appointments WHERE dateYmd = :dateYmd AND status = 'CONCLUIDO'")
  suspend fun getCompletedAppointmentsForDate(dateYmd: String): List<AppointmentEntity>

  @Query("SELECT COALESCE(SUM(totalPrice), 0) FROM appointments WHERE dateYmd = :dateYmd AND status = 'CONCLUIDO'")
  fun getDailyTotal(dateYmd: String): Flow<Long>

  @Query("SELECT COUNT(*) FROM appointments WHERE dateYmd = :dateYmd AND status = 'CONCLUIDO'")
  fun getDailyCompletedCount(dateYmd: String): Flow<Int>

  @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
  suspend fun getAppointmentById(id: String): AppointmentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAppointment(appointment: AppointmentEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(appointments: List<AppointmentEntity>)

  @Update
  suspend fun updateAppointment(appointment: AppointmentEntity)

  @Delete
  suspend fun deleteAppointment(appointment: AppointmentEntity)

  @Query("DELETE FROM appointments WHERE id = :id")
  suspend fun deleteAppointmentById(id: String)
}
