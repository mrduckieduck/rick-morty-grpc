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

@KtorExperimentalAPI
fun Application.module() {
    val endpoint = RickMortyEndpoint()
    install(DefaultHeaders)
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT) // Pretty Prints the JSON
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

@KtorExperimentalAPI
fun main() {
    embeddedServer(Netty, 8080, watchPaths = listOf("RickMortyClient"), module = Application::module).start()
    println(">> Client started!")
}