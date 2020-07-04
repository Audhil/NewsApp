package com.example.newsapp.main

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.example.newsapp.R
import com.example.newsapp.di.APIModule
import com.example.newsapp.di.OtherModule
import com.example.newsapp.ui.main.MainActivity
import com.example.newsapp.util.FileReader
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

@UninstallModules(APIModule::class, OtherModule::class)
@HiltAndroidTest
class MainActivityTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(MainActivity::class.java, true, false)

    private lateinit var mockServer: MockWebServer

    @Before
    fun setUp() {
        mockServer = MockWebServer()
        mockServer.start(8080)
    }

    @After
    fun tearDown() =
        mockServer.shutdown()

    @Test
    fun happyTestCase() {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse()
                    .setResponseCode(200)
                    .setBody(FileReader.readStringFromFile("success_response.json"))
            }
        }
        activityRule.launchActivity(null)
        Espresso.onView(withId(R.id.progress_bar)).run {
            check(ViewAssertions.matches(not(ViewMatchers.isDisplayed())))
        }
        Espresso.onView(withId(R.id.repo_recycler_view)).run {
            check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }

    @Test
    fun unHappyTestCase() {
        mockServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                return MockResponse().throttleBody(1024, 5, TimeUnit.SECONDS)
            }
        }
        activityRule.launchActivity(null)
        Espresso.onView(withId(R.id.retry_btn)).run {
            check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
        }
    }
}