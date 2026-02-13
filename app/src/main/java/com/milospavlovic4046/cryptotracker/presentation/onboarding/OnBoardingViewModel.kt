package com.milospavlovic4046.cryptotracker.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milospavlovic4046.cryptotracker.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userRepo: UserRepository
) : ViewModel() {

    fun saveUser(name: String, age: Int) {
        viewModelScope.launch {
            userRepo.saveUser(name, age)
        }
    }
}