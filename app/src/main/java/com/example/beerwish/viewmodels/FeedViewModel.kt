package com.example.beerwish.viewmodels

import android.arch.lifecycle.MutableLiveData
import com.example.beerwish.data.remote.model.Checkin


class FeedViewModel() : ObservableViewModel() {
    val searchQuery = MutableLiveData<String>()
    val checkinList = MutableLiveData<List<Checkin>>()

    fun filteredList(): List<Checkin>? {
        return checkinList.value?.filter {
            searchQuery.value.let { it1 ->
                it.toString().contains(it1 ?: "", true)
            }
        }
    }
}

