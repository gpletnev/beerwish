package com.example.beerwish.viewmodels

import android.arch.lifecycle.MutableLiveData
import com.example.beerwish.data.remote.model.User

class UserViewModel(
        var user: MutableLiveData<User> = MutableLiveData<User>()
) : ObservableViewModel() {
    fun getFullName(): String {
        return if (user.value?.first_name != null) {
            "${user.value?.first_name} ${user.value?.last_name}"
        } else ""
    }
}