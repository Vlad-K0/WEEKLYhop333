package com.example.weekly.Domain.Usecase.GroupUseCases

import com.example.weekly.Domain.Model.Group
import com.example.weekly.Domain.Repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GetAllGroupsUseCase(
    private val repository: GroupRepository
) {
    operator fun invoke(): Flow<List<Group>> {
        return repository.observeAllGroups()
    }
}
