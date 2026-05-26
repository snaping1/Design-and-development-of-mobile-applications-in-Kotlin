package com.example.photocatalog.data.mapper

import com.example.photocatalog.data.remote.dto.PhotoDto
import com.example.photocatalog.domain.model.PhotoEntity

private const val THUMBNAIL_WIDTH = 400
private const val THUMBNAIL_HEIGHT = 400

fun PhotoDto.toEntity(): PhotoEntity = PhotoEntity(
    id = id,
    author = author,
    width = width,
    height = height,
    thumbnailUrl = "https://picsum.photos/id/$id/$THUMBNAIL_WIDTH/$THUMBNAIL_HEIGHT",
    downloadUrl = downloadUrl,
    sourceUrl = url
)

fun List<PhotoDto>.toEntityList(): List<PhotoEntity> = map { it.toEntity() }
