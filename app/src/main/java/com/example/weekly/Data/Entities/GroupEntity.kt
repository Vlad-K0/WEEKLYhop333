package com.example.weekly.Data.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "color")
    val color: String
)
