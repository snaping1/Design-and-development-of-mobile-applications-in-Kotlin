package com.example.repository

import com.example.db.DatabaseFactory.dbQuery
import com.example.db.Laureates
import com.example.db.PrizeLaureates
import com.example.db.Prizes
import com.example.model.LaureateResponse
import com.example.model.PrizeResponse
import com.example.model.PrizeWithLaureatesResponse
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll

class PrizeRepository {

    suspend fun listAll(): List<PrizeResponse> = dbQuery {
        Prizes.selectAll()
            .orderBy(Prizes.year to SortOrder.DESC, Prizes.category to SortOrder.ASC)
            .map { it.toPrizeResponse() }
    }

    suspend fun findById(id: Int): PrizeResponse? = dbQuery {
        Prizes.selectAll()
            .where { Prizes.id eq id }
            .limit(1)
            .map { it.toPrizeResponse() }
            .firstOrNull()
    }

    suspend fun findByYearAndCategory(year: Int, category: String): PrizeWithLaureatesResponse? = dbQuery {
        val prizeRow = Prizes.selectAll()
            .where { (Prizes.year eq year) and (Prizes.category eq category) }
            .limit(1)
            .firstOrNull() ?: return@dbQuery null

        val prizeId = prizeRow[Prizes.id].value
        val laureates = fetchLaureates(prizeId)
        PrizeWithLaureatesResponse(
            id = prizeId,
            year = prizeRow[Prizes.year],
            category = prizeRow[Prizes.category],
            motivation = prizeRow[Prizes.motivation],
            laureates = laureates,
        )
    }

    suspend fun findLaureatesForPrize(year: Int, category: String): List<LaureateResponse>? = dbQuery {
        val prizeRow = Prizes.selectAll()
            .where { (Prizes.year eq year) and (Prizes.category eq category) }
            .limit(1)
            .firstOrNull() ?: return@dbQuery null
        fetchLaureates(prizeRow[Prizes.id].value)
    }

    private fun fetchLaureates(prizeId: Int): List<LaureateResponse> {
        return PrizeLaureates
            .join(Laureates, JoinType.INNER, additionalConstraint = { PrizeLaureates.laureateId eq Laureates.id })
            .selectAll()
            .where { PrizeLaureates.prizeId eq prizeId }
            .map {
                LaureateResponse(
                    id = it[Laureates.id].value,
                    firstName = it[Laureates.firstName],
                    lastName = it[Laureates.lastName],
                    birthDate = it[Laureates.birthDate]?.toString(),
                    deathDate = it[Laureates.deathDate]?.toString(),
                    share = it[PrizeLaureates.share],
                    affiliation = it[PrizeLaureates.affiliation],
                )
            }
    }

    private fun ResultRow.toPrizeResponse(): PrizeResponse = PrizeResponse(
        id = this[Prizes.id].value,
        year = this[Prizes.year],
        category = this[Prizes.category],
        motivation = this[Prizes.motivation],
    )
}
