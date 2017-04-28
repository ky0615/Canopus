package moe.linux.boilerplate

import junit.framework.Assert
import moe.linux.boilerplate.api.mastodon.MastodonApiClient
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).

 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    val mstdn = MastodonApiClient("https://mstdn.jp")

    @Test
    fun addition_isCorrect() {
        mstdn
            .mastodonApiService
            .apps("canopus", mstdn.DEFAULT_REDIRECT, mstdn.DEFAULT_SCOPE)
            .subscribe({
                println("client id: $it")
                Assert.assertNotNull(it)
            })
        assertEquals(4, (2 + 2).toLong())
    }
}