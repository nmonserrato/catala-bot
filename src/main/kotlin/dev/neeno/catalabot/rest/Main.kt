package dev.neeno.catalabot.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import dev.neeno.catalabot.Dictionary
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val dictionary = Dictionary.initializeFromFiles()
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val server = embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            jackson {
                disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            }
        }

        routing {
            post("/new-message") {
                val post = call.receive<Update>()
                println("received message $post")
                if ("digues me una paraula" == post.message.text) {
                    call.respond(Reply(chatId = post.message.chat.id, text = dictionary.randomWord()))
                } else {
                    call.respond(HttpStatusCode.OK, "")
                }
            }
        }
    }

    server.start(wait = true)
}