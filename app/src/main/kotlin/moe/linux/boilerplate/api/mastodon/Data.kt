package moe.linux.boilerplate.api.mastodon

import android.util.Base64
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.nio.charset.Charset

open class MastodonAccount() : RealmObject() {
    // id of the account, return from the Mastodon server
    open var accountId: Long = 0

    // username of the account, return from the Mastodon server
    open var username: String = ""

    open var instanceUrl: String = ""

    // encode instanceUrl and accountId with base64
    @PrimaryKey
    open var uid: String = ""
        get() {
            return MastodonAccount.makeUID(instanceUrl, accountId)
        }

    companion object {
        fun makeUID(instanceUrl: String, accountId: Long)
            = Base64.encodeToString("$instanceUrl/$accountId".toByteArray(Charset.defaultCharset()), Base64.DEFAULT)!!
    }
}
