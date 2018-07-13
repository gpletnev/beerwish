package com.example.beerwish

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beerwish.MainActivity.Companion.ACCESS_TOKEN_EXTRA
import com.example.beerwish.adapters.CheckinAdapter
import com.example.beerwish.data.remote.model.Checkin
import com.example.beerwish.data.remote.model.Checkins
import com.example.beerwish.net.UntappdServer
import com.example.beerwish.viewmodels.FeedViewModel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson


class FeedFragment : Fragment() {

    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.feed_fragment, container, false)

        viewModel = ViewModelProviders.of(this).get(FeedViewModel::class.java)

        val adapter = CheckinAdapter()
        val feedList = view.findViewById<RecyclerView>(R.id.feed_list)
        feedList.adapter = adapter
        feedList.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))


        viewModel.checkinList.observe(viewLifecycleOwner, Observer { checkins ->
            if (checkins != null) adapter.submitList(checkins)
        })
        return view
    }

    fun requestFriendsFeed() {
        if (FuelManager.instance.baseParams.isEmpty()) return

        val userInfoUri = "${UntappdServer.API_END_POINT}checkin/recent"//access_token=${access_token}"//&limit=10"
        userInfoUri.httpGet().responseString() { request, response, result ->
            Log.d("request", "$request")
            Log.d("response", "$response")
            Log.d("result", "$result")
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.d("exception", "$ex")
                }
                is Result.Success -> {
                    val data = result.get()//.replace("[]", "")
                    val checkinsJson = Json(data).obj().getJSONObject("response").getJSONObject("checkins")
                    Log.d("requestFriendsFeed", checkinsJson.toString())
                    val checkins = Gson().fromJson(checkinsJson.toString().replace("[]", "null"), Checkins::class.java)
                    viewModel.checkinList.value = checkins.items
                    viewModel.notifyChange()
                }
            }
        }
    }
}