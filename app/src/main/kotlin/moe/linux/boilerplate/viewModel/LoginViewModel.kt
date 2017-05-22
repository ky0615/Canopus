package moe.linux.boilerplate.viewModel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.view.View
import com.google.gson.GsonBuilder
import com.mcxiaoke.koi.ext.toast
import com.sys1yagi.mastodon4j.rx.RxAccounts
import io.reactivex.disposables.CompositeDisposable
import io.realm.Realm
import io.realm.RealmConfiguration
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.mastodon.*
import moe.linux.boilerplate.di.scope.Account
import moe.linux.boilerplate.util.view.*
import timber.log.Timber
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    val compositeDisposable: CompositeDisposable,
    val navigator: Navigator,
    val mastodonClientBuilder: MastodonClientBuilder,
    @Account val accountRealmConfiguration: RealmConfiguration) : BaseObservable(), ViewModel {

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
                        Timber.d("access token:")
                        prettyPrint(it)
                        saveAccount(it)
                    }, {
                        it.printStackTrace()
                        navigator.activity.toast(R.string.login_failed_get_access_token)
                    })
            )
        } ?: throw IllegalArgumentException("apps is required")
    }

    fun saveAccount(token: AccessToken) {
        val client = mastodonClientBuilder
            .build(instanceUrl)
            .accessToken(token.accessToken)
            .build()

        compositeDisposable.add(
            usingProgress({ showProgressView.set(true) }, { showProgressView.set(false) }, RxAccounts(client).getVerifyCredentials().bindThread())
                .subscribe({ account ->
                    prettyPrint(account)
                    Realm.getInstance(accountRealmConfiguration)
                        .apply {
                            beginTransaction()
                            createObject(MastodonAccount::class.java, MastodonAccount.makeUID(instanceUrl, account.id))
                                .also {
                                    it.accountId = account.id
                                    it.username = account.userName
                                    it.instanceUrl = instanceUrl
                                }
                            commitTransaction()
                        }
                }, { it.printStackTrace() })
        )
    }

    override fun destroy() {
        compositeDisposable.clear()
    }
}