package moe.linux.boilerplate.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import io.reactivex.Observable
import moe.linux.boilerplate.R
import moe.linux.boilerplate.api.github.GithubApiClient
import moe.linux.boilerplate.api.mastodon.MastodonApiClient
import moe.linux.boilerplate.api.qiita.QiitaApiClient
import moe.linux.boilerplate.databinding.ActivityMainBinding
import moe.linux.boilerplate.view.fragment.BaseFragment
import moe.linux.boilerplate.view.fragment.FrontFragment
import moe.linux.boilerplate.view.fragment.GithubListFragment
import moe.linux.boilerplate.view.fragment.QiitaListFragment
import timber.log.Timber
import javax.inject.Inject

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var client: GithubApiClient

    @Inject
    lateinit var qiitaClient: QiitaApiClient

    lateinit var onStateChange: Observable<Page>

    val frontFragment: FrontFragment by lazyFragment(FrontFragment.TAG, { FrontFragment.newInstance() })

    val qiitaListFragment: QiitaListFragment by lazyFragment(QiitaListFragment.TAG, { QiitaListFragment.newInstance() })

    val githubListFragment: GithubListFragment by lazyFragment(GithubListFragment.TAG, { GithubListFragment.newInstance() })

    val mastodonApiClient by lazy { MastodonApiClient("https://mstdn.jp") }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.injectTo(this)

        mastodonApiClient
            .apps("canopus")
            .subscribe({
                Timber.d("recieve")
                Timber.d(it.toString())
            }, {
                Timber.e(it)
            })

        initView()
        initFragment(savedInstanceState)

        onStateChange.subscribe({
            when (it) {
                MainActivity.Page.FRONT -> frontFragment
                MainActivity.Page.Github -> githubListFragment
                MainActivity.Page.Qiita -> qiitaListFragment
            }.apply {
                switchFragment(this, this.TAG)
            }
        })
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()

        onStateChange = Observable.merge<Page>(listOf(
            Observable.create<Page> { subscribe ->
                binding.navView.setNavigationItemSelectedListener {
                    subscribe.onNext(Page.parseWithId(it.itemId))
                    binding.drawer.closeDrawer(GravityCompat.START)
                    true
                }
            },
            Observable.create<Page> { subscribe ->
                binding.bottomMenu.setOnNavigationItemSelectedListener {
                    subscribe.onNext(Page.parseWithId(it.itemId))
                    true
                }
            }))
            .publish()
            .refCount()
    }

    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null)
            switchFragment(frontFragment, FrontFragment.TAG)
    }

    override fun onBackPressed() {
        // TODO fragment backstackも考慮するように変更
        Timber.d("backstack count: ${supportFragmentManager.backStackEntryCount}")
        if (binding.drawer.isDrawerOpen(GravityCompat.START))
            binding.drawer.closeDrawer(GravityCompat.START)
        else if (switchFragment(frontFragment, FrontFragment.TAG))
            Timber.d("back to front page")
        else
            super.onBackPressed()

        updateView()
    }

    fun switchFragment(fragment: Fragment, tag: String): Boolean {
        if (fragment.isAdded) return false

        val manager = supportFragmentManager

        manager.beginTransaction().also { ft ->
            val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.contentFrame)
            if (currentFragment != null)
                ft.detach(currentFragment)
            if (fragment.isDetached)
                ft.attach(fragment)
            else
                ft.add(R.id.contentFrame, fragment, tag)
        }
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()

        manager.executePendingTransactions()

        updateView()

        return true
    }

    private fun updateView() {
        val currentFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.contentFrame)
        if (currentFragment is BaseFragment) {
            currentFragment.getPage().also {
                binding.toolbar.title = getString(it.title)
                binding.navView.menu?.findItem(it.id)
                    .apply {
                        if (this == null)
                            binding.navView.menu?.setGroupCheckable(R.id.mainMenu, false, false)
                    }?.isChecked = true
                binding.bottomMenu.menu.findItem(it.id)
                    .apply {
                        if (this == null)
                            binding.bottomMenu.menu.setGroupCheckable(R.id.mainMenu, false, false)
                    }?.isChecked = true
            }
        }
    }

    enum class Page(@IdRes val id: Int, @StringRes val title: Int) {
        FRONT(R.id.navHome, R.string.app_name), // other page
        Github(R.id.navGithub, R.string.menu_github),
        Qiita(R.id.navQiita, R.string.menu_qiita),
        ;

        companion object {
            fun parseWithId(id: Int): Page {
                return values().find { it.id == id } ?: FRONT
            }
        }
    }

    private fun <T : Fragment> lazyFragment(tag: String, non: () -> T): Lazy<T> =
        lazy { supportFragmentManager.findFragmentByTag(tag) as? T ?: non() }
}
