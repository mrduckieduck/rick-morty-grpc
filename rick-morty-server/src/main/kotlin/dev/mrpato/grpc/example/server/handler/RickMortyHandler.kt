package dev.mrpato.grpc.example.server.handler

import com.google.protobuf.Empty
import dev.mrpato.grpc.example.proto.CharacterProto
import dev.mrpato.grpc.example.proto.RickMortyCharactersGrpcKt
import dev.mrpato.grpc.example.proto.RickMortyServiceProto
import dev.mrpato.grpc.example.server.repository.RickMortyCharacter
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class RickMortyHandler : RickMortyCharactersGrpcKt.RickMortyCharactersCoroutineImplBase() {

    override suspend fun getAllCharacters(request: Empty): CharacterProto.Characters {
        val response = CharacterProto.Characters.newBuilder()
        transaction {
            RickMortyCharacter.selectAll().map {
                val item = CharacterProto.Character.newBuilder()
                    .setId(it[RickMortyCharacter.id])
                    .setName(it[RickMortyCharacter.name])
                    .setSpecies(it[RickMortyCharacter.species])
                    .setGender(it[RickMortyCharacter.gender])
                    .setVotes(it[RickMortyCharacter.votes])
                    .build()
                response.addCharacters(item)
            }
        }
        return response.build()
    }

    override suspend fun getCharacter(request: RickMortyServiceProto.GetCharacterRequest): CharacterProto.Character {
        return getCharacter(request.characterId)
    }

    override suspend fun voteCharacter(request: RickMortyServiceProto.VoteCharacterRequest): CharacterProto.Character {
        transaction {
            val character = RickMortyCharacter.select { RickMortyCharacter.id eq request.characterId }.single()
            println("Vote for ${character[RickMortyCharacter.name]} -> ${request.vote}")
            RickMortyCharacter.update({ RickMortyCharacter.id eq request.characterId }) {
                it[votes] = vote(request.vote, character[votes])
            }
        }

        return getCharacter(request.characterId)
    }

    private fun getCharacter(id: Long): CharacterProto.Character {
        val characterProto = CharacterProto.Character.newBuilder()
        transaction {
            val character = RickMortyCharacter.select { RickMortyCharacter.id eq id }.single()
            characterProto.id = character[RickMortyCharacter.id]
            characterProto.name = character[RickMortyCharacter.name]
            characterProto.species = character[RickMortyCharacter.species]
            characterProto.gender = character[RickMortyCharacter.gender]
            characterProto.votes = character[RickMortyCharacter.votes]
        }
        return characterProto.build()
    }

    private fun vote(vote: Boolean, currentVotes: Int): Int {
        if (currentVotes == 0 && !vote) {
            return 1
        }
        return if (vote) currentVotes + 1 else currentVotes - 1
    }

}
