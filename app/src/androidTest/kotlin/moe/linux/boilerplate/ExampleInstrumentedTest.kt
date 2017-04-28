package moe.linux.boilerplate

import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import junit.framework.Assert.assertNotNull
import moe.linux.boilerplate.api.mastodon.MastodonApiClient
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test, which will execute on an Android device.

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val mstdn = MastodonApiClient("https://mstdn.jp")

    @Test
    @Throws(Exception::class)
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()

//        assertEquals("moe.linux.boilerplate", appContext.packageName)
        mstdn
            .mastodonApiService
            .apps("canopus", mstdn.DEFAULT_REDIRECT, mstdn.DEFAULT_SCOPE)
            .subscribe({
                println("client id: $it")
                assertNotNull(it)
            })
    }
}
