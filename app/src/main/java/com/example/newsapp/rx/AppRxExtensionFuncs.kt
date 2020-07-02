package com.example.newsapp.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.TestScheduler

//  Flowable
fun <T> Flowable<T>.makeFlowableRxConnection(iRxListeners: IRxListeners<T>, tag: String, testScheduler: TestScheduler? = null): Disposable =
        testScheduler?.let {
            this.apply {
                subscribeOn(testScheduler)
                observeOn(testScheduler)
            }.subscribeWith(AppRxDisposableSubscriber(iRxListeners, tag))
        }
                ?: this.compose(AppRxSchedulers.applyFlowableSchedulers())
                        .subscribeWith(AppRxDisposableSubscriber(iRxListeners, tag))

//  Observable
fun <T> Observable<T>.makeObservableRxConnection(iRxListeners: IRxListeners<T>, tag: String, testScheduler: TestScheduler? = null): Disposable =
        testScheduler?.let {
            this.apply {
                subscribeOn(testScheduler)
                observeOn(testScheduler)
            }.subscribeWith(AppRxDisposableObserver(iRxListeners, tag))
        }
                ?: this.compose(AppRxSchedulers.applySchedulers())
                        .subscribeWith(AppRxDisposableObserver(iRxListeners, tag))

//  Single
fun <T> Single<T>.makeSingleRxConnection(iRxListeners: IRxListeners<T>, tag: String, testScheduler: TestScheduler? = null): Disposable =
        testScheduler?.let {
            this.apply {
                subscribeOn(testScheduler)
                observeOn(testScheduler)
            }.subscribeWith(AppRxDisposableSingleObserver(iRxListeners, tag))
        }
                ?: this.compose(AppRxSchedulers.applySingleSchedulers())
                        .subscribeWith(AppRxDisposableSingleObserver(iRxListeners, tag))