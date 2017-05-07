package moe.linux.boilerplate.util.view

import com.google.gson.GsonBuilder
import timber.log.Timber

fun prettyPrint(obj: Any) {
    Timber.d(GsonBuilder().setPrettyPrinting().create().toJson(obj))
}