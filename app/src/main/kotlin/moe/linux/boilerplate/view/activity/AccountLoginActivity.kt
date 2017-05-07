package moe.linux.boilerplate.view.activity

import android.os.Bundle
import com.mcxiaoke.koi.ext.inflater
import moe.linux.boilerplate.databinding.ActivityAccountLoginBinding
import moe.linux.boilerplate.viewModel.LoginViewModel
import timber.log.Timber
import javax.inject.Inject

class AccountLoginActivity : BaseActivity() {
    lateinit var binding: ActivityAccountLoginBinding

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)

        binding = ActivityAccountLoginBinding.inflate(inflater)

        binding.viewModel = loginViewModel
        setContentView(binding.root)

        intent.data?.also {
            // receive the callback
            try {
                Timber.d("receive url")
                Timber.d(it.toString())
                loginViewModel.loadClientId()
                loginViewModel.makeApiClient()
                it.getQueryParameter("code")
                    .takeIf { it.isNotEmpty() }
                    ?.apply { loginViewModel.getAccessToken(this) }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}
