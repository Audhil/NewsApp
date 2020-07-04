package com.example.newsapp.base

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.newsapp.data.remote.API
import com.example.newsapp.util.AppExecutors
import com.example.newsapp.util.ErrorLiveData
import com.example.newsapp.util.showVLog
import io.mockk.*
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.IOException
import java.net.SocketException
import java.util.concurrent.Executors

open class BaseTest {

    val context by lazy {
        mockk<Context>()
    }

    val errorLiveData by lazy {
        ErrorLiveData(appExecutors)
    }

    val api by lazy {
        mockk<API>()
    }

    val testScheduler by lazy {
        TestScheduler()
    }

    //  private properties
    private val singleThreadExecutor by lazy {
        Executors.newSingleThreadExecutor()
    }

    val appExecutors: AppExecutors by lazy {
        mockk<AppExecutors>()
    }

    //  custom rule
    inner class InitSetUpTestRule : TestRule {
        override fun apply(base: Statement, description: Description): Statement {
            return object : Statement() {

                override fun evaluate() {
                    try {
                        preSetUps()
                        base.evaluate() //  this will run the test
                    } finally {
                        RxJavaPlugins.reset()
                        RxAndroidPlugins.reset()
                    }
                }

            }
        }
    }

    //  pre set up for test cases
    private fun preSetUps() {
        setUpExecutors()
        setUpLogs()
        setUpRxJava()
        setUpToasts()
    }

    private fun setUpToasts() {
        mockkStatic(Toast::class)
        every { Toast.makeText(any(), any<String>(), any()) } returns Toast(context)
        every { Toast.makeText(any(), any<String>(), any()).show() } just Runs
        every { Toast.makeText(any(), any<String>(), any()).cancel() } just Runs
    }

    private fun setUpExecutors() {
        every { appExecutors.diskIOThread() } returns singleThreadExecutor
        every { appExecutors.networkIOThread() } returns singleThreadExecutor
        every { appExecutors.mainThread() } returns singleThreadExecutor
    }

    private fun setUpLogs() {
        mockkStatic(Log::class)
        every { Log.v(any(), any()) } returns 0
        every { Log.d(any(), any()) } returns 0
        every { Log.i(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
    }

    private fun setUpRxJava() {
        //For some reason, without adding line (which calls static block of Schedulers and initializes
        //values in them) RxJava Scheduler usages throws ExceptionInInitializerError error
        Schedulers.single()
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        RxJavaPlugins.setInitSingleSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitComputationSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        setRxJavaErrorHandler()
    }

    private fun setRxJavaErrorHandler() {
        RxJavaPlugins.setErrorHandler { error ->
            val e = error
                .takeIf { it is UndeliverableException }
                ?.cause
                ?: error

            when (e) {
                is IOException, is SocketException ->
                    return@setErrorHandler

                is InterruptedException ->
                    return@setErrorHandler

                is NullPointerException, is IllegalArgumentException -> {
                    // that's likely a bug in the application
                    Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                    return@setErrorHandler
                }

                is IllegalStateException -> {
                    // that's a bug in RxJava or in a custom operator
                    Thread.currentThread().uncaughtExceptionHandler
                        .uncaughtException(Thread.currentThread(), e)
                    return@setErrorHandler
                }

                else ->
                    showVLog { "UnDeliverable Exception ${e.printStackTrace()}" }
            }
        }
    }
}