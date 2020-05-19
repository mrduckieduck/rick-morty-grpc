@file:JvmName("RickMortyClient")

package dev.mrpato.grpc.example.client

import com.fasterxml.jackson.databind.SerializationFeature
import dev.mrpato.grpc.example.client.api.RickMortyEndpoint
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.route
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.util.KtorExperimentalAPI
import io.ktor.util.getOrFail
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

@ExperimentalCoroutinesApi
val endpoint = RickMortyEndpoint()

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun Application.module() {
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    install(Routing) {
        route("/rick-morty/characters") {
            get {
                call.respond(HttpStatusCode.OK, endpoint.getAll())
            }
            get("/{id}") {
                val id = call.parameters.getOrFail("id").toLong()
                call.respond(HttpStatusCode.OK, endpoint.get(id))
            }
            put("/{id}/voteup") {
                val id = call.parameters.getOrFail("id").toLong()
                endpoint.vote(id, true)
                call.respond(HttpStatusCode.Accepted, mapOf("vote" to true))

            }
            put("/{id}/votedown") {
                val id = call.parameters.getOrFail("id").toLong()
                endpoint.vote(id, false)
                call.respond(HttpStatusCode.Accepted, mapOf("vote" to false))
            }
        }
    }
}

@ExperimentalCoroutinesApi
@KtorExperimentalAPI
fun main() {
    val port = Random.Default.nextInt(8080, 8090)
    embeddedServer(Netty, port, watchPaths = listOf("RickMortyClient"), module = Application::module).start()
    GlobalScope.launch {
        endpoint.init()
            .joinStream
            .collect { greet -> println(">> Broadcast Message: ${greet.value}") }

    }
    println(">> Client started on the port [:$port]!")
}