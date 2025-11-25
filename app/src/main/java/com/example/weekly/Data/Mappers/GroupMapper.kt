package com.example.weekly.Data.Mappers

import com.example.weekly.Data.Entities.GroupEntity
import com.example.weekly.Domain.Model.Group

fun GroupEntity.toDomain(): Group {
    return Group(
        id = id,
        name = name,
        color = color
    )
}

fun Group.toEntity(): GroupEntity {
    return GroupEntity(
        id = id,
        name = name,
        color = color
    )
}
