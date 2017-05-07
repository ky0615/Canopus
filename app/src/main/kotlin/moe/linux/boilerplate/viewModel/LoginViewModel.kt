package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.view.View
import com.google.gson.GsonBuilder
import com.mcxiaoke.koi.ext.toast
import io.reactivex.disposables.CompositeDisposable
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.mastodon.AppsResponse
import moe.linux.boilerplate.api.mastodon.MastodonApiClient
import moe.linux.boilerplate.util.view.Navigator
import moe.linux.boilerplate.util.view.ViewModel
import moe.linux.boilerplate.util.view.prettyPrint
import moe.linux.boilerplate.util.view.usingProgress
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    val compositeDisposable: CompositeDisposable,
    val navigator: Navigator) : BaseObservable(), ViewModel {

    @Bindable
    var instanceUrl = ""

    var apiClient: MastodonApiClient? = null

    val showProgressView = ObservableField<Boolean>(false)

    val enableLoginButton = ObservableField<Boolean>(true)

    var apps: AppsResponse? = null

    val redirectUri = navigator.getString(R.string.login_callback)!!

    fun onClickLoginButton(view: View) {
        Timber.d("instanceUrl: $instanceUrl")
        if (instanceUrl.isEmpty()) {
            navigator.activity.toast("required the instance url")
            return
        }
        makeApiClient()
        enableLoginButton.set(false)
        getClientId()
    }

    fun makeApiClient() {
        this.apiClient = MastodonApiClient("https://$instanceUrl")
    }

    fun getClientId() {
        compositeDisposable.add(
            usingProgress({
                showProgressView.set(true)
                Timber.d("progress: show")
            }, {
                Timber.d("progress: hide")
                showProgressView.set(false)
                enableLoginButton.set(true)
            }, apiClient!!.apps(navigator.getString(R.string.client_name), redirectUri))
                .subscribe({
                    prettyPrint(it)
                    navigator.activity.toast("client id: ${it?.client_id}")
                    apps = it
                    saveClientId()
                    navigator.navigateToWebPageWithExteralBrowser(apiClient!!.getAuthorizationUrl(it.client_id, redirectUri))
                }, { it.printStackTrace() }))
    }

    fun saveClientId() {
        navigator.activity.getSharedPreferences("login_token", 0)
            .edit()
            .putString("apps", GsonBuilder().create().toJson(apps))
            .putString("instanceUrl", instanceUrl)
            .apply()
    }

    fun loadClientId() {
        navigator.activity.getSharedPreferences("login_token", 0).also {
            apps = GsonBuilder().create().fromJson(
                it.getString("apps", "{}"), AppsResponse::class.java)
            instanceUrl = it.getString("instanceUrl", "")
        }
    }

    fun getAccessToken(token: String) {
        apps?.apply {
            compositeDisposable.add(
                usingProgress({
                    showProgressView.set(true)
                    Timber.d("progress: show")
                }, {
                    Timber.d("progress: hide")
                    showProgressView.set(false)
                }, apiClient!!.getAccessToken(client_id, client_secret, token, redirectUri))
                    .subscribe({
                        navigator.activity.toast(R.string.login_succeed)
                        Timber.d("access token: $it")
                    }, {
                        it.printStackTrace()
                        navigator.activity.toast(R.string.login_failed_get_access_token)
                    })
            )
        } ?: throw IllegalArgumentException("apps is required")
    }

    fun saveAccessToken(x: Any) {
        // TODO: save with realm
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}