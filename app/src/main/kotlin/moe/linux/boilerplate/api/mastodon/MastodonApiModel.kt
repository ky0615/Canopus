package moe.linux.boilerplate.api.mastodon

import com.google.gson.annotations.SerializedName

data class AppsResponse(
    var id: Long,
    var redirect_uri: String,
    var client_id: String,
    var client_secret: String
)

data class AccessToken(
    @SerializedName("access_token")
    var accessToken: String
)
