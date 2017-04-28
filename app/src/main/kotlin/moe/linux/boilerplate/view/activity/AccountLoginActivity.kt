package moe.linux.boilerplate.view.activity

import android.os.Bundle

class AccountLoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)
    }
}