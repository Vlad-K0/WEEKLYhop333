package com.example.weekly.Domain.Usecase.GroupUseCases

import com.example.weekly.Domain.Repository.GroupRepository

class DeleteGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(groupId: Int) {
        val group = repository.getGroupById(groupId)
        group?.let {
            repository.deleteGroup(it)
        }
    }
}
