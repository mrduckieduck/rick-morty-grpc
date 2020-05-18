package dev.mrpato.grpc.example.client.handler

import com.google.protobuf.Empty
import dev.mrpato.grpc.example.proto.CharacterProto
import dev.mrpato.grpc.example.proto.RickMortyCharactersGrpcKt
import dev.mrpato.grpc.example.proto.RickMortyServiceProto
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RickMortyServiceHandler {
    private val channel: ManagedChannel = createChanel()
    private val stub: RickMortyCharactersGrpcKt.RickMortyCharactersCoroutineStub = RickMortyCharactersGrpcKt.RickMortyCharactersCoroutineStub(channel)

    suspend fun getAllCharacters(): List<CharacterProto.Character> = coroutineScope {
        val response = async { stub.getAllCharacters(Empty.getDefaultInstance()) }
        return@coroutineScope response.await().charactersList
    }

    suspend fun getCharacter(id: Long): CharacterProto.Character = coroutineScope {
        val response = async {
            val request = RickMortyServiceProto.GetCharacterRequest.newBuilder()
                .setCharacterId(id)
                .build()
            stub.getCharacter(request)
        }
        return@coroutineScope response.await()
    }

    suspend fun vote(id: Long, vote: Boolean): CharacterProto.Character = coroutineScope {
        val response = async {
            val request = RickMortyServiceProto.VoteCharacterRequest.newBuilder()
                .setCharacterId(id)
                .setVote(vote)
                .build()
            stub.voteCharacter(request)
        }
        return@coroutineScope response.await()
    }

    private fun createChanel(): ManagedChannel {
        return ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()
    }
}