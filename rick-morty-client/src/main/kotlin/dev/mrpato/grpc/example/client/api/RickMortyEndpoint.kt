package dev.mrpato.grpc.example.client.api

import dev.mrpato.grpc.example.client.handler.GreetingServiceHandler
import dev.mrpato.grpc.example.client.handler.RickMortyServiceHandler
import dev.mrpato.grpc.example.client.model.RickMortyCharacter
import dev.mrpato.grpc.example.client.utils.createJoinRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlin.random.Random

class RickMortyEndpoint {

    private val protoHandler = RickMortyServiceHandler()
    private val greetingService = GreetingServiceHandler()

    @ExperimentalCoroutinesApi
    suspend fun init(): GreetingServiceHandler.JoinResult {
        val joinRequestFlow = channelFlow {
            val request = createJoinRequest {
                name = "Client"
                id = Random.Default.nextInt(1, 100)
            }
            send(request)
            awaitClose {}
        }
        return greetingService.join(joinRequestFlow)
    }

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