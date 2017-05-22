package moe.linux.boilerplate.view.activity

import android.os.Bundle
import com.mcxiaoke.koi.ext.startActivity
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import moe.linux.boilerplate.BuildConfig
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.mastodon.MastodonAccount
import moe.linux.boilerplate.databinding.ActivitySplashBinding
import moe.linux.boilerplate.di.scope.Account
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

    @field:[Inject Account]
    lateinit var accountRealm: RealmConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)
        binding = ActivitySplashBinding.inflate(layoutInflater, null, false)

        binding.versionText.text = resources.getString(R.string.splash_version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        // add initial preference

        compositeDisposable.add(
            Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (isHaveAccount()) openMainPage() else openLoginPage()
                }, { it.printStackTrace() }))
    }

    fun isHaveAccount(): Boolean =
        Realm.getInstance(accountRealm)
            .where(MastodonAccount::class.java)
            .findAll()
            .run { return this.size > 0 }

    fun openMainPage() {
        finish()
        Timber.d("start main activity")
        startActivity<MainActivity>()
    }

    fun openLoginPage() {
        finish()
        startActivity<AccountLoginActivity>()
    }
}