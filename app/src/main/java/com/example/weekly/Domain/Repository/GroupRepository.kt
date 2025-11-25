package com.example.weekly.Domain.Repository

import com.example.weekly.Domain.Model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun observeAllGroups(): Flow<List<Group>>
    suspend fun getGroupById(id: Int): Group?
    suspend fun insertGroup(group: Group)
    suspend fun deleteGroup(group: Group)
}
