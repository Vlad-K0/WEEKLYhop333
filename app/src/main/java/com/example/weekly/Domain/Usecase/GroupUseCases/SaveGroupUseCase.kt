package com.example.weekly.Domain.Usecase.GroupUseCases

import com.example.weekly.Domain.Model.Group
import com.example.weekly.Domain.Repository.GroupRepository

class SaveGroupUseCase(
    private val repository: GroupRepository
) {
    suspend operator fun invoke(id: Int = 0, name: String, color: String) {
        val group = Group(
            id = id,
            name = name,
            color = color
        )
        repository.insertGroup(group)
    }
}
