package com.example.weekly.Domain.Usecase.NoteUseCases

import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetOrderedNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<Map<String, List<Note>>> {
        return repository.observeAllNotes().map { notes ->
            notes
                .groupBy { it.date }
                .mapValues { (_, dayNotes) ->
                    dayNotes.sortedWith(
                        compareBy<Note> { it.isDone }
                            .thenBy { it.startTime == null }
                            .thenBy { it.startTime }
                    )
                }
        }
    }
}