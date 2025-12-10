package com.example.weekly.Domain.Usecase.NoteUseCases

import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NoteRepository
import java.time.LocalTime

class SaveNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(
        id: Int, 
        date: String, 
        content: String, 
        startTime: LocalTime?,
        groupId: Int? = null
    ): Long {
        val oldNote = if (id != 0) repository.getNoteById(id) else null

        val newNote = Note(
            id = id, // Если 0, Room сам сгенерирует
            date = date,
            content = content,
            isDone = oldNote?.isDone ?: false,
            startTime = startTime,
            groupId = groupId
        )

        return repository.saveNote(newNote)
    }
}