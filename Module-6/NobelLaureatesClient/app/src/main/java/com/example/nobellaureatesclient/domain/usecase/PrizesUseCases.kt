package com.example.nobellaureatesclient.domain.usecase

import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize
import com.example.nobellaureatesclient.domain.repository.PrizesRepository
import javax.inject.Inject

class GetPrizesUseCase @Inject constructor(
    private val repository: PrizesRepository,
) {
    suspend operator fun invoke(year: Int?, category: NobelCategory): Result<List<NobelPrize>> =
        repository.getPrizes(year, category)
}

class GetPrizeDetailsUseCase @Inject constructor(
    private val repository: PrizesRepository,
) {
    suspend operator fun invoke(year: Int, category: NobelCategory): Result<NobelPrize> =
        repository.getPrizeDetails(year, category)
}
