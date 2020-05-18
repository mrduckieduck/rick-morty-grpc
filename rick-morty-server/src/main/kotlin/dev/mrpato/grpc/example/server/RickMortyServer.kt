@file:JvmName("RickMortyServer")

package dev.mrpato.grpc.example.server

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.mrpato.grpc.example.server.handler.RickMortyHandler
import io.grpc.Server
import io.grpc.ServerBuilder
import org.jetbrains.exposed.sql.Database

fun main() {
    initDB()
    val server: Server = ServerBuilder
        .forPort(50051)
        .addService(RickMortyHandler())
        .build()
    server.start()
    server.awaitTermination()
    println(">> Server started in the port ${server.port}")
}

private fun initDB() {
    val dbConfig = HikariConfig("/application.properties")
    dbConfig.schema = "public"
    Database.connect(HikariDataSource(dbConfig))
}