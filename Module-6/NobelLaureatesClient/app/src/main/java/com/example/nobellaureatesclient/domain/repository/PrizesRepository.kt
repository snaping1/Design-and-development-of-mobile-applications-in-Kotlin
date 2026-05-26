package com.example.nobellaureatesclient.domain.repository

import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize

interface PrizesRepository {
    suspend fun getPrizes(year: Int?, category: NobelCategory): Result<List<NobelPrize>>
    suspend fun getPrizeDetails(year: Int, category: NobelCategory): Result<NobelPrize>
}
