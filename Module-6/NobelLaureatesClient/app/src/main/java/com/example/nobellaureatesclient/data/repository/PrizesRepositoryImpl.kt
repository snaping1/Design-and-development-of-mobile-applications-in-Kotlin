package com.example.nobellaureatesclient.data.repository

import com.example.nobellaureatesclient.data.mapper.toDomain
import com.example.nobellaureatesclient.data.remote.api.PrizesApi
import com.example.nobellaureatesclient.data.remote.runCatchingApi
import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.domain.repository.PrizesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrizesRepositoryImpl @Inject constructor(
    private val api: PrizesApi,
) : PrizesRepository {

    override suspend fun getPrizes(
        year: Int?,
        category: NobelCategory,
    ): Result<List<NobelPrize>> = runCatchingApi {
        api.list()
            .map { it.toDomain() }
            .filter { prize ->
                (year == null || prize.year == year) &&
                    (category == NobelCategory.ALL || prize.category == category)
            }
            .sortedWith(compareByDescending<NobelPrize> { it.year }.thenBy { it.category.displayName })
    }

    override suspend fun getPrizeDetails(
        year: Int,
        category: NobelCategory,
    ): Result<NobelPrize> = runCatchingApi {
        api.details(year, category.apiCode).toDomain()
    }
}
