package com.example.weekly.Data.Repository

import com.example.weekly.Data.Local.GroupDao
import com.example.weekly.Data.Mappers.toDomain
import com.example.weekly.Data.Mappers.toEntity
import com.example.weekly.Domain.Model.Group
import com.example.weekly.Domain.Repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GroupRepositoryImpl(
    private val groupDao: GroupDao
) : GroupRepository {
    
    override fun observeAllGroups(): Flow<List<Group>> {
        return groupDao.getAllGroups().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    override suspend fun getGroupById(id: Int): Group? {
        return groupDao.getGroupById(id)?.toDomain()
    }
    
    override suspend fun insertGroup(group: Group) {
        groupDao.insert(group.toEntity())
    }
    
    override suspend fun deleteGroup(group: Group) {
        groupDao.delete(group.toEntity())
    }
}
