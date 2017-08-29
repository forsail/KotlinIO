package com.lee.asher.kotlinio.extension

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by lihong on 2017/8/21.
 */
fun <T> Observable<T>.applyScheduleres(): Observable<T> {
    return subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}