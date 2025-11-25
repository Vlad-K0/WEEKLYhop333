package com.example.weekly.Data.Repository

import com.example.weekly.Data.Local.NoteDao
import com.example.weekly.Data.Mappers.toDomain
import com.example.weekly.Data.Mappers.toEntity
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {
    override fun observeAllNotes(): Flow<List<Note>> {
        return dao.getAllNotes().map {
            entities -> entities.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getNoteById(noteId: Int): Note? {
        return dao.getNoteById(noteId)?.toDomain()
    }

    override suspend fun saveNote(note: Note) {
        dao.insert(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        dao.delete(note.toEntity())
    }

    override suspend fun toggleDoneStatus(note: Note) {
        val updatedNote = note.copy(isDone = !note.isDone)
        dao.update(updatedNote.toEntity())
    }
}