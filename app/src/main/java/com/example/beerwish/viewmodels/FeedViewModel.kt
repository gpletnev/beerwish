package com.example.beerwish.viewmodels

import android.arch.lifecycle.MutableLiveData
import com.example.beerwish.data.remote.model.Checkin


class FeedViewModel() : ObservableViewModel() {
    val checkinList = MutableLiveData<List<Checkin>>()
}

