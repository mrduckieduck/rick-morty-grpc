package dev.mrpato.grpc.example.client.api

import dev.mrpato.grpc.example.client.handler.RickMortyServiceHandler
import dev.mrpato.grpc.example.client.model.RickMortyCharacter

class RickMortyEndpoint {

    private val protoHandler = RickMortyServiceHandler()

    suspend fun getAll(): List<RickMortyCharacter> {
        return protoHandler.getAllCharacters()
            .map {
                RickMortyCharacter(it.id, it.name, it.species, it.gender, it.votes)
            }
    }

    suspend fun get(id: Long): RickMortyCharacter {
        val response = protoHandler.getCharacter(id)
        return RickMortyCharacter(response.id, response.name, response.species, response.gender, response.votes)
    }

    suspend fun vote(id: Long, vote: Boolean) {
        protoHandler.vote(id, vote)
    }

}