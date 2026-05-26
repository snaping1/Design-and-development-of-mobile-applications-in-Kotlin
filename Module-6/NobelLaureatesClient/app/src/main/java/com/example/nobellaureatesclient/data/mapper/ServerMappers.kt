package com.example.nobellaureatesclient.data.mapper

import com.example.nobellaureatesclient.data.remote.dto.FavoriteDto
import com.example.nobellaureatesclient.data.remote.dto.LaureateDto
import com.example.nobellaureatesclient.data.remote.dto.PrizeDto
import com.example.nobellaureatesclient.data.remote.dto.PrizeWithLaureatesDto
import com.example.nobellaureatesclient.domain.model.Favorite
import com.example.nobellaureatesclient.domain.model.Laureate
import com.example.nobellaureatesclient.domain.model.NobelCategory
import com.example.nobellaureatesclient.domain.model.NobelPrize

fun PrizeDto.toDomain(): NobelPrize = NobelPrize(
    id = id,
    year = year,
    category = NobelCategory.fromApiCode(category),
    motivation = motivation,
)

fun PrizeWithLaureatesDto.toDomain(): NobelPrize = NobelPrize(
    id = id,
    year = year,
    category = NobelCategory.fromApiCode(category),
    motivation = motivation,
    laureates = laureates.map { it.toDomain() },
)

fun LaureateDto.toDomain(): Laureate = Laureate(
    id = id,
    firstName = firstName,
    lastName = lastName,
    birthDate = birthDate,
    deathDate = deathDate,
    share = share,
    affiliation = affiliation,
)

fun FavoriteDto.toDomain(): Favorite = Favorite(
    prizeId = prizeId,
    year = year,
    category = NobelCategory.fromApiCode(category),
    motivation = motivation,
    addedAt = addedAt,
)
