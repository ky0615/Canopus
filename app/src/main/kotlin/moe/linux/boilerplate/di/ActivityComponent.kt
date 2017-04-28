package moe.linux.boilerplate.di

import dagger.Subcomponent
import moe.linux.boilerplate.di.scope.ActivityScope
import moe.linux.boilerplate.view.activity.AccountLoginActivity
import moe.linux.boilerplate.view.activity.MainActivity
import moe.linux.boilerplate.view.activity.SplashActivity

@ActivityScope
@Subcomponent(modules = arrayOf(
    ActivityModule::class
))
interface ActivityComponent {
    fun injectTo(activity: SplashActivity)

    fun injectTo(activity: MainActivity)

    fun injectTo(activity: AccountLoginActivity)

    fun plus(module: FragmentModule): FragmentComponent
}
