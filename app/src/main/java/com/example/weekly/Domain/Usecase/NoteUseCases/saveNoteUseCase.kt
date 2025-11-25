package com.example.weekly.Domain.Usecase.NoteUseCases

import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NoteRepository

class saveNoteUseCase(private val noteRepository: NoteRepository) {
    suspend fun saveNote(note: Note){
        noteRepository.saveNote(note)
    }
}