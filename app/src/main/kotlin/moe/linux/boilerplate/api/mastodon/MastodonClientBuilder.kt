package moe.linux.boilerplate.api.mastodon

import com.google.gson.Gson
import com.sys1yagi.mastodon4j.MastodonClient
import okhttp3.OkHttpClient


class MastodonClientBuilder(
    private val okHttpClientBuilder: OkHttpClient.Builder,
    private val gson: Gson) {

    fun build(instanceName: String) =
        MastodonClient.Builder(instanceName, okHttpClientBuilder, gson)
}
