package com.example.weekly.Data.Mappers

import com.example.weekly.Data.Entities.NoteEntity
import com.example.weekly.Domain.Model.Note

fun NoteEntity.toDomain(): Note {
    return Note(
        id = this.id,
        content = this.content,
        date = this.date,
        isDone = this.isDone,
        startTime = this.startTime
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = this.id,
        content = this.content,
        date = this.date,
        isDone = this.isDone,
        startTime = this.startTime
    )
}