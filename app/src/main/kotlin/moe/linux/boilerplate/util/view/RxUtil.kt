package moe.linux.boilerplate.util.view

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun usingProgress(start: () -> Unit = {}, finish: (Unit) -> Unit = {})
    = Observable.using(start, { Observable.just(null) }, finish)!!


fun <T> usingProgress(start: () -> Unit = {}, finish: (Unit) -> Unit = {}, observable: Observable<T>)
    = Observable.using(start, { observable }, finish)!!

fun <T> usingProgress(start: () -> Unit = {}, finish: (Unit) -> Unit = {}, observable: Single<T>)
    = usingProgress(start, finish, observable.toObservable())

fun <T> Single<T>.bindThread() =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

fun <T> Observable<T>.bindThread() =
    this.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())