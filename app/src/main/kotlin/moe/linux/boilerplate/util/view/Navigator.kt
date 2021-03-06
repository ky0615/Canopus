package moe.linux.boilerplate.util.view

import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.webkit.URLUtil
import moe.linux.boilerplate.R
import moe.linux.boilerplate.di.scope.ActivityScope
import timber.log.Timber
import javax.inject.Inject

@ActivityScope
class Navigator @Inject constructor(val activity: AppCompatActivity) {

    fun navigateToWebPage(url: String) {
        url.takeIf { URLUtil.isNetworkUrl(url) }?.apply {
            CustomTabsIntent.Builder().apply {
                setShowTitle(true)
                setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            }.build().launchUrl(activity, Uri.parse(url))
        } ?: Timber.e("URL parse error: $url")
    }

    fun navigateToWebPageWithExteralBrowser(url: String) {
        Uri.parse(url).also { uri -> activity.startActivity(Intent(Intent.ACTION_VIEW, uri)) }
    }

    fun getString(resId: Int) = activity.getString(resId)
    fun getString(resId: Int, vararg args: Any) = activity.getString(resId, args)
}
