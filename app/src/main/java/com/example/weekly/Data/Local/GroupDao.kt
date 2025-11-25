package com.example.weekly.Data.Local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.weekly.Data.Entities.GroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupDao {
    
    @Query("SELECT * FROM groups ORDER BY name")
    fun getAllGroups(): Flow<List<GroupEntity>>
    
    @Query("SELECT * FROM groups WHERE id = :id")
    suspend fun getGroupById(id: Int): GroupEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: GroupEntity)
    
    @Upsert
    suspend fun upsert(group: GroupEntity)
    
    @Delete
    suspend fun delete(group: GroupEntity)
}
