package dev.neeno.catalabot.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            jackson {
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }

        routing {
            post("/new-message") {
                val post = call.receive<Update>()
                call.respond(Reply(chatId = post.message.chat.id, text = post.message.text))
            }
        }
    }

    server.start(wait = true)
}