import kotlinx.serialization.Serializable

@Serializable
data class SecretsResponse(
    val apiKey: String,
    val apiPassword: String,
)
