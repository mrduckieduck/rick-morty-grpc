package dev.mrpato.grpc.example.server.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object RickMortyCharacter : Table("rick_morty_character") {
    val id: Column<Long> = long("id").autoIncrement()
    val name: Column<String> = varchar("name", 100)
    var species: Column<String> = varchar("species", 100)
    var gender: Column<String> = varchar("gender", 1)
    val votes: Column<Int> = integer("votes")
    override val primaryKey = PrimaryKey(id, name = "rickmorty_char_pkey")
}