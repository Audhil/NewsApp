package com.example.newsapp.repo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.newsapp.base.BaseTest
import com.example.newsapp.data.model.Article
import com.example.newsapp.data.model.HeadLinePojo
import com.example.newsapp.ui.main.MainRepo
import com.example.newsapp.util.Constants
import com.example.newsapp.util.NetworkError
import io.mockk.every
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Flowable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.SocketTimeoutException

class MainRepoTest : BaseTest() {

    @get:Rule
    val instantExecutorRule by lazy {
        InstantTaskExecutorRule()
    }

    @get:Rule
    val initSetUpTestRule by lazy {
        InitSetUpTestRule()
    }

    private var mainRepo: MainRepo? = null

    @Before
    fun `set up`() {
        mainRepo = spyk(
            MainRepo(
                errorLiveDataa = errorLiveData,
                api = api,
                context = context,
                testScheduler = testScheduler
            )
        )
    }

    @After
    fun `tear down`() {
        mainRepo = null
    }

    @Test
    fun `happyCase with proper response`() {
        //  given
        val headLinePojo = HeadLinePojo(
            articles = listOf(
                Article(title = "Jack and jill", author = "Mohammed Audhil"),
                Article(title = "Twinkle twinkle little star", author = "John McGrawHill"),
                Article(title = "Five little ducks!", author = "MacMohan Neil")
            )
        )

        every { context.getString(any()) } returns Constants.EMPTY
        every { api.getHeadLines(apiKey = any()) } returns Flowable.just(headLinePojo)

        //  when
        mainRepo?.loadHeadlines()

        //  then
        val arg1 = slot<HeadLinePojo>()
        val arg2 = slot<String>()
        verify(exactly = 1) {
            mainRepo?.onSuccess(
                capture(arg1), capture(arg2)
            )
        }
        assert(arg1.captured == headLinePojo)
        assert(arg2.captured == Constants.NEWS_FEEDS)

        assert(mainRepo?.articleListLiveData?.value?.size == 3)
        assert(mainRepo?.articleListLiveData?.value?.get(0)?.title == "Jack and jill")
    }

    @Test
    fun `unHappyCase with exception`() {
        //  given
        every { context.getString(any()) } returns Constants.EMPTY
        every { api.getHeadLines(apiKey = any()) } returns Flowable.error(SocketTimeoutException())

        //  when
        mainRepo?.loadHeadlines()

        //  then
        assert(mainRepo?.errorLiveData != null)
        assert(mainRepo?.errorLiveData?.value == NetworkError.SOCKET_TIMEOUT)
    }
}