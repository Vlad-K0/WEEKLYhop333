package com.example.weekly.Domain.Usecase.NoteUseCases

import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NoteRepository

class DeleteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.deleteNote(note)
    }
}