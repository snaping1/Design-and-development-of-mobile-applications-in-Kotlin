package com.example.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.timestamp

object Users : IntIdTable("users") {
    val username = varchar("username", 100).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
}

object Prizes : IntIdTable("prizes") {
    val year = integer("year")
    val category = varchar("category", 50)
    val motivation = text("motivation").nullable()
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)

    init {
        uniqueIndex("prizes_year_category_unique", year, category)
    }
}

object Laureates : IntIdTable("laureates") {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val birthDate = date("birth_date").nullable()
    val deathDate = date("death_date").nullable()
}

object PrizeLaureates : Table("prize_laureates") {
    val prizeId = reference("prize_id", Prizes, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val laureateId = reference("laureate_id", Laureates, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val share = integer("share").nullable()
    val affiliation = varchar("affiliation", 255).nullable()

    override val primaryKey = PrimaryKey(prizeId, laureateId)
}

object UserFavorites : Table("user_favorites") {
    val userId = reference("user_id", Users, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val prizeId = reference("prize_id", Prizes, onDelete = org.jetbrains.exposed.sql.ReferenceOption.CASCADE)
    val addedAt = timestamp("added_at").defaultExpression(CurrentTimestamp)

    override val primaryKey = PrimaryKey(userId, prizeId)
}
