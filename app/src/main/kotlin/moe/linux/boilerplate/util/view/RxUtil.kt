package moe.linux.boilerplate.util.view

import io.reactivex.Observable

fun usingProgress(start: () -> Unit = {}, finish: (Unit) -> Unit = {})
    = Observable.using(start, { Observable.just(null) }, finish)!!


fun <T> usingProgress(start: () -> Unit = {}, finish: (Unit) -> Unit = {}, observable: Observable<T>)
    = Observable.using(start, { observable }, finish)!!
