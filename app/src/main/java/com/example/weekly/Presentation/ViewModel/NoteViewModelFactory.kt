package com.example.weekly.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weekly.Domain.Usecase.GroupUseCases.DeleteGroupUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.GetAllGroupsUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.SaveGroupUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase

class NoteViewModelFactory(
    private val getOrderedNotesUseCase: GetOrderedNotesUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val toggleDoneStatusUseCase: ToggleDoneStatusUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val saveGroupUseCase: SaveGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                getOrderedNotesUseCase,
                getThemeUseCase,
                deleteUseCase,
                saveNoteUseCase,
                toggleDoneStatusUseCase,
                toggleThemeUseCase,
                getAllGroupsUseCase,
                saveGroupUseCase,
                deleteGroupUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
