package moe.linux.boilerplate.api.mastodon

data class AppsResponse(
    var id: Long,
    var redirect_uri: String,
    var client_id: String,
    var client_secret: String
)