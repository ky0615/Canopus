package moe.linux.boilerplate.api.mastodon

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import moe.linux.boilerplate.api.AbstructApiClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.net.URLEncoder

class MastodonApiClient(val baseUrl: String) : AbstructApiClient() {
    val DEFAULT_REDIRECT = "urn:ietf:wg:oauth:2.0:oob"
    val DEFAULT_SCOPE = "read write follow"

    val mastodonApiService: MastodonApiService by lazy {
        Retrofit.Builder()
            .client(OkHttpClient.Builder()
                .addInterceptor {
                    val original = it.request()
                    Timber.d("mastodon: url: ${original.url()}")

                    it.proceed(original)
                }.build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .build()
            .create(MastodonApiService::class.java)
    }

    fun apps(clientName: String, redirectUris: String = DEFAULT_REDIRECT, scopes: String = DEFAULT_SCOPE) =
        mastodonApiService
            .apps(clientName, redirectUris, scopes)
            .bindThread()

    fun getAuthorizationUrl(clientId: String, redirectUri: String = DEFAULT_REDIRECT, scope: String = DEFAULT_SCOPE)
        = "$baseUrl/oauth/authorize?redirect_uri=${redirectUri.urlEncode()}&response_type=code&client_id=${clientId.urlEncode()}&&scope=${scope.urlEncode()}"

    fun String.urlEncode() = URLEncoder.encode(this, "UTF-8") ?: throw IllegalArgumentException("cause by $this")
}
