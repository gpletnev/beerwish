package com.example.beerwish

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.beerwish.data.remote.model.User
import com.example.beerwish.databinding.ActivityMainBinding
import com.example.beerwish.databinding.NavigationHeaderBinding
import com.example.beerwish.viewmodels.UserViewModel
import com.github.kittinunf.fuel.android.core.Json
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private const val API_END_POINT = "https://api.untappd.com/v4/"
        private val TOKEN_AUTH = "https://untappd.com/oauth/authenticate/"
        private val AUTHORIZE_END_POINT = "https://untappd.com/oauth/authorize/"
        private val CLIENT_ID = BuildConfig.client_id
        private val CLIENT_SECRET = BuildConfig.client_secret
        private val REDIRECT_URL = "${BuildConfig.scheme}://${BuildConfig.host}"
        const val ACCESS_TOKEN_EXTRA = "ACCESS_TOKEN_EXTRA"
    }

    var access_token: String? = null
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var userViewModel: UserViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ACCESS_TOKEN_EXTRA)) {
                access_token = savedInstanceState.getString(ACCESS_TOKEN_EXTRA)
            }
        }

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        userViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val navigationHeaderBinding: NavigationHeaderBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.navigation_header, binding.navigationView, true)
        navigationHeaderBinding.userViewModel = userViewModel

        binding.navigationView.setNavigationItemSelectedListener(this)
        when (access_token) {
            null -> binding.navigationView.menu.findItem(R.id.login).title = getString(R.string.login)
            else -> binding.navigationView.menu.findItem(R.id.login).title = getString(R.string.logout)
        }

        if (supportFragmentManager.findFragmentByTag(FeedFragment::class.java.simpleName) == null) {
            val fragment = FeedFragment()

            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, fragment, FeedFragment::class.java.simpleName).commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString(ACCESS_TOKEN_EXTRA, access_token)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        access_token = savedInstanceState?.getString(ACCESS_TOKEN_EXTRA)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.login -> {
                when (access_token) {
                    null -> requestCode()
                    else -> {
                        access_token = null
                        FuelManager.instance.baseParams = listOf()
                        item.title = getString(R.string.login)
                    }
                }
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (!access_token.isNullOrEmpty()) {
            requestUserInfo()
        } else {
            with(intent) {
                if (action == Intent.ACTION_VIEW && hasCategory(Intent.CATEGORY_BROWSABLE)) {
                    if (data?.scheme == BuildConfig.scheme) {
                        with(data.toString()) {
                            when {
                                contains("code=") -> {
                                    val code = substringAfter("code=").substringBefore("&")
                                    requestToken(code)
                                }
                                contains("access_token=") -> {
                                    val access_token = substringAfter("access_token=").substringBefore("&")
                                    Log.d("access_token", access_token)
                                }
                                else -> {
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestCode() {
        val uri = Uri.parse("$TOKEN_AUTH?client_id=$CLIENT_ID&response_type=code&redirect_url=$REDIRECT_URL")
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this, uri)
    }

    private fun requestToken(code: String) {
        val authorize = "$AUTHORIZE_END_POINT?client_id=$CLIENT_ID&response_type=code&redirect_url=$REDIRECT_URL&client_secret=$CLIENT_SECRET&code=$code"
        authorize.httpGet().responseString() { request, response, result ->
            Log.d("request", "$request")
            Log.d("response", "$response")
            Log.d("result", "$result")
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.d("exception", "$ex")
                }
                is Result.Success -> {
                    val data = result.get()
                    access_token = Json(data).obj().getJSONObject("response").get("access_token").toString()
                    Log.d("access_token", "$access_token")
                    FuelManager.instance.baseParams = listOf("access_token" to access_token)
                    requestUserInfo()
                }
            }
        }
    }

    fun requestUserInfo() {
        val userInfoUri = "${API_END_POINT}user/info"
        userInfoUri.httpGet(listOf("compact" to true)).responseString() { request, response, result ->
            Log.d("request", "$request")
            Log.d("response", "$response")
            Log.d("result", "$result")
            when (result) {
                is Result.Failure -> {
                    val ex = result.getException()
                    Log.d("exception", "$ex")
                }
                is Result.Success -> {
                    val data = result.get()
                    val userJson = Json(data).obj().getJSONObject("response").getJSONObject("user")
                    val user = Gson().fromJson(userJson.toString(), User::class.java)
                    userViewModel.user.value = user
                    userViewModel.notifyChange()
                    navigation_view.menu.findItem(R.id.login).title = getString(R.string.logout)
                    val feedFragment = supportFragmentManager.findFragmentByTag(FeedFragment::class.java.simpleName) as FeedFragment
                    feedFragment.requestFriendsFeed()
                }
            }
        }
    }
}