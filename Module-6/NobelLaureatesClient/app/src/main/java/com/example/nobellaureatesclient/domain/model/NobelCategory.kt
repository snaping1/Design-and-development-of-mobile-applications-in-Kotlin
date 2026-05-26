package com.example.nobellaureatesclient.domain.model

enum class NobelCategory(val apiCode: String, val displayName: String) {
    ALL("", "Все категории"),
    PHYSICS("physics", "Физика"),
    CHEMISTRY("chemistry", "Химия"),
    MEDICINE("medicine", "Медицина"),
    LITERATURE("literature", "Литература"),
    PEACE("peace", "Мир"),
    ECONOMICS("economics", "Экономика");

    companion object {
        fun fromApiCode(code: String?): NobelCategory =
            entries.firstOrNull { it.apiCode.equals(code, ignoreCase = true) } ?: ALL
    }
}
