package com.picpay.desafio.android.ui.contacts

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.data.UserRepository
import com.picpay.desafio.android.data.coroutine.DispatcherProvider
import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.model.UserUiData
import com.squareup.picasso.Picasso
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContactsViewModel : ViewModel(), KoinComponent {

    private val userRepository: UserRepository by inject()
    private val dispatcherProvider: DispatcherProvider by inject()

    private val users = MutableStateFlow(emptyList<User>())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading.asStateFlow()

    val usersUiData: StateFlow<List<UserUiData>> = users.map { users ->
        users.map { user ->
            user.toUserUiData(getBitmapFromUrl(user.img))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()
    )

    private suspend fun getBitmapFromUrl(url: String?): Bitmap? {
        return withContext(dispatcherProvider.IO) {
            Picasso.get().load(url).get()
        }
    }

    private fun User.toUserUiData(imgBitmap: Bitmap?): UserUiData {
        return UserUiData(id, username, name, imgBitmap)
    }

    fun getUsers() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                users.update { userRepository.getUsers() }
                _isLoading.value = false
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

}