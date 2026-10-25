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

@Entity(tableName = "clients")
data class ClientEntity(
  @PrimaryKey val id: String,
  val name: String,
  val phone: String,
  val notes: String = "",
  val lastService: String = "",
  val lastServiceDate: String = "",
  val totalSpentKz: Long = 0L,
  val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface ClientDao {
  @Query("SELECT * FROM clients ORDER BY createdAt DESC")
  fun getAllClients(): Flow<List<ClientEntity>>

  @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
  suspend fun getClientById(id: String): ClientEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertClient(client: ClientEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(clients: List<ClientEntity>)

  @Update
  suspend fun updateClient(client: ClientEntity)

  @Delete
  suspend fun deleteClient(client: ClientEntity)

  @Query("DELETE FROM clients WHERE id = :id")
  suspend fun deleteClientById(id: String)

  @Query("SELECT COUNT(*) FROM clients")
  suspend fun getClientsCount(): Int
}
