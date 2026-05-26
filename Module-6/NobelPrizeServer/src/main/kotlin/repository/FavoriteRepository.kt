package com.example.repository

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.Prizes
import com.example.db.UserFavorites
import com.example.model.FavoriteResponse
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class FavoriteRepository {

    suspend fun listForUser(userId: Int): List<FavoriteResponse> = dbQuery {
        UserFavorites
            .join(Prizes, JoinType.INNER, additionalConstraint = { UserFavorites.prizeId eq Prizes.id })
            .selectAll()
            .where { UserFavorites.userId eq userId }
            .orderBy(UserFavorites.addedAt to SortOrder.DESC)
            .map {
                FavoriteResponse(
                    prizeId = it[Prizes.id].value,
                    year = it[Prizes.year],
                    category = it[Prizes.category],
                    motivation = it[Prizes.motivation],
                    addedAt = it[UserFavorites.addedAt].toString(),
                )
            }
    }

    suspend fun prizeExists(prizeId: Int): Boolean = dbQuery {
        Prizes.selectAll()
            .where { Prizes.id eq prizeId }
            .limit(1)
            .empty()
            .not()
    }

    suspend fun add(userId: Int, prizeId: Int): AddResult = dbQuery {
        val alreadyExists = UserFavorites.selectAll()
            .where { (UserFavorites.userId eq userId) and (UserFavorites.prizeId eq prizeId) }
            .limit(1)
            .any()
        if (alreadyExists) return@dbQuery AddResult.AlreadyExists

        try {
            UserFavorites.insert {
                it[UserFavorites.userId] = userId
                it[UserFavorites.prizeId] = prizeId
            }
            AddResult.Added
        } catch (e: ExposedSQLException) {
            AddResult.AlreadyExists
        }
    }

    suspend fun remove(userId: Int, prizeId: Int): Boolean = dbQuery {
        val condition: Op<Boolean> = SqlExpressionBuilder.run {
            (UserFavorites.userId eq userId) and (UserFavorites.prizeId eq prizeId)
        }
        UserFavorites.deleteWhere { condition } > 0
    }
}

enum class AddResult { Added, AlreadyExists }
