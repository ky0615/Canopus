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
}
