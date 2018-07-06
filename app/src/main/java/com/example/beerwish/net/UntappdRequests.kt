package com.example.beerwish.net

import android.content.Context
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import com.example.beerwish.App
import com.example.beerwish.BuildConfig

class UntappdRequests(val context: Context = App.instance) {

    companion object {
        private const val API_END_POINT = "https://api.untappd.com/v4/"
        private val TOKEN_AUTH = "https://untappd.com/oauth/authenticate/"
        private val AUTHORIZE_END_POINT = "https://untappd.com/oauth/authorize/"
        private val CLIENT_ID = BuildConfig.client_id
        private val CLIENT_SECRET = BuildConfig.client_secret
        private val REDIRECT_URL = "${BuildConfig.scheme}://${BuildConfig.host}"//"ru.bigcraftday.appauth://oauth"//
        lateinit var access_token: String
    }

    fun requestCode() {
        val uri = Uri.parse("$TOKEN_AUTH?client_id=$CLIENT_ID&response_type=code&redirect_url=$REDIRECT_URL")
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(context, uri)
    }
}