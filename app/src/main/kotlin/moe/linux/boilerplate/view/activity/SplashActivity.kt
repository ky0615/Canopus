package moe.linux.boilerplate.view.activity

import android.os.Bundle
import io.reactivex.Observable
import moe.linux.boilerplate.BuildConfig
import moe.linux.boilerplate.R
import moe.linux.boilerplate.databinding.ActivitySplashBinding
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    lateinit var binding: ActivitySplashBinding

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
                .subscribe({
                    if (isHaveAccount()) openMainPage() else openMainPage()
                }, { it.printStackTrace() }))
    }

    // TODO: implement
    fun isHaveAccount() = true

    fun openMainPage() {
        finish()
        startActivity(MainActivity::class)
    }

    fun openLoginPage() {
        finish()
        startActivity(AccountLoginActivity::class)
    }
}