package com.milospavlovic4046.cryptotracker.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.data.local.entity.UserEntity
import com.milospavlovic4046.cryptotracker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    val user: StateFlow<UserEntity?> =
        userRepo.observeUser()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    init {
        viewModelScope.launch { userRepo.refreshFromRemote() }
    }

    fun updateName(newName: String) {
        val current = user.value ?: return
        val name = newName.trim()
        if (name.isBlank()) return

        viewModelScope.launch {
            userRepo.saveUser(name = name, age = current.age)
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            userRepo.clearUser()
        }
    }
}