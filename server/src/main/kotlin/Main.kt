import io.fusionauth.jwks.JSONWebKeySetHelper
import io.fusionauth.jwks.domain.JSONWebKey
import io.fusionauth.jwt.domain.Algorithm
import io.fusionauth.jwt.domain.JWT
import io.fusionauth.jwt.rsa.RSAVerifier
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

private val SERVER_SECRETS = SecretsResponse(
    apiKey = "VERY_SECRET_API_KEY",
    apiPassword = "VERY_SECRET_API_PASSWORD",
)

private const val FIREBASE_PROJECT_NUMBER = "442808137136"

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    configureRouting()
    configureSerialization()
}

fun Application.configureRouting() {
    routing {
        secretsRouting()
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
}

fun Route.secretsRouting() {
    route("/secrets") {
        get {
            val token = call.request.headers["X-Firebase-AppCheck"] ?: run {
                call.respond(HttpStatusCode.Unauthorized, "No required header")
                return@get
            }

            val jwt = try {
                val keys =
                    JSONWebKeySetHelper.retrieveKeysFromJWKS("https://firebaseappcheck.googleapis.com/v1/jwks")

                val verifiers = keys.map {
                    RSAVerifier.newVerifier(JSONWebKey.parse(it))
                }

                verifiers.firstNotNullOf { verifier ->
                    runCatching {
                        JWT.getDecoder().decode(token, verifier)
                    }.getOrNull()
                }
            } catch (e: Exception) {
                println("Error decoding token: ${e.message}")
                call.respond(HttpStatusCode.Unauthorized, "Error during token decoding")
                return@get
            }
            if (
                jwt.header.algorithm != Algorithm.RS256 ||
                jwt.header.type != "JWT" ||
                jwt.issuer != "https://firebaseappcheck.googleapis.com/$FIREBASE_PROJECT_NUMBER" ||
                jwt.isExpired ||
                (jwt.audience as List<*>).none { it != "projects/$FIREBASE_PROJECT_NUMBER" }
            ) {
                call.respond(HttpStatusCode.Unauthorized, "Error during token validation")
                return@get
            }

            call.respond(HttpStatusCode.OK, SERVER_SECRETS)
        }
    }
}
