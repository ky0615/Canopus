package moe.linux.boilerplate.model

import io.reactivex.disposables.CompositeDisposable
import moe.linux.boilerplate.api.mastodon.AccessToken
import moe.linux.boilerplate.api.mastodon.AppsResponse
import moe.linux.boilerplate.util.view.Navigator
import javax.inject.Inject

class MastodonAccountRegisterModel @Inject constructor(
    val compositeDisposable: CompositeDisposable,
    val navigator: Navigator) {

    fun saveAccount(apps: AppsResponse, token: AccessToken) {

    }
}