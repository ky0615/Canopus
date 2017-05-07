package moe.linux.boilerplate.api.mastodon

import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MastodonApiService {
    @POST("/api/v1/apps")
    @FormUrlEncoded
    fun apps(@Field("client_name") clientName: String,
             @Field("redirect_uris") redirectUris: String,
             @Field("scopes") scopes: String): Observable<AppsResponse>

    @POST("/oauth/token")
    @FormUrlEncoded
    fun getToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("grant_type") grantType: String
    ): Observable<AccessToken>
}
