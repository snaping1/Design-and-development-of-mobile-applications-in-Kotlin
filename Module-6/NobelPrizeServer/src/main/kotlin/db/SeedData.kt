package com.example.db

import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

object SeedData {

    fun seedIfEmpty() {
        transaction {
            val existing = Prizes.selectAll().limit(1).count()
            if (existing > 0L) return@transaction

            val einsteinId = Laureates.insertAndGetIntId(
                first = "Albert",
                last = "Einstein",
                birth = LocalDate.of(1879, 3, 14),
                death = LocalDate.of(1955, 4, 18),
            )
            val curieId = Laureates.insertAndGetIntId(
                first = "Marie",
                last = "Curie",
                birth = LocalDate.of(1867, 11, 7),
                death = LocalDate.of(1934, 7, 4),
            )
            val becquerelId = Laureates.insertAndGetIntId(
                first = "Henri",
                last = "Becquerel",
                birth = LocalDate.of(1852, 12, 15),
                death = LocalDate.of(1908, 8, 25),
            )
            val pierreId = Laureates.insertAndGetIntId(
                first = "Pierre",
                last = "Curie",
                birth = LocalDate.of(1859, 5, 15),
                death = LocalDate.of(1906, 4, 19),
            )
            val hemingwayId = Laureates.insertAndGetIntId(
                first = "Ernest",
                last = "Hemingway",
                birth = LocalDate.of(1899, 7, 21),
                death = LocalDate.of(1961, 7, 2),
            )
            val mandelaId = Laureates.insertAndGetIntId(
                first = "Nelson",
                last = "Mandela",
                birth = LocalDate.of(1918, 7, 18),
                death = LocalDate.of(2013, 12, 5),
            )
            val deKlerkId = Laureates.insertAndGetIntId(
                first = "Frederik Willem",
                last = "de Klerk",
                birth = LocalDate.of(1936, 3, 18),
                death = LocalDate.of(2021, 11, 11),
            )

            val prize1921Physics = Prizes.insertAndGetIntId(
                year = 1921,
                category = "physics",
                motivation = "for his services to Theoretical Physics, and especially for his discovery of the law of the photoelectric effect",
            )
            val prize1903Physics = Prizes.insertAndGetIntId(
                year = 1903,
                category = "physics",
                motivation = "in recognition of the extraordinary services they have rendered by their joint researches on the radiation phenomena discovered by Professor Henri Becquerel",
            )
            val prize1911Chemistry = Prizes.insertAndGetIntId(
                year = 1911,
                category = "chemistry",
                motivation = "in recognition of her services to the advancement of chemistry by the discovery of the elements radium and polonium",
            )
            val prize1954Literature = Prizes.insertAndGetIntId(
                year = 1954,
                category = "literature",
                motivation = "for his mastery of the art of narrative, most recently demonstrated in The Old Man and the Sea",
            )
            val prize1993Peace = Prizes.insertAndGetIntId(
                year = 1993,
                category = "peace",
                motivation = "for their work for the peaceful termination of the apartheid regime, and for laying the foundations for a new democratic South Africa",
            )

            linkPrizeLaureate(prize1921Physics, einsteinId, 1, "Kaiser-Wilhelm-Institut (now Max-Planck-Institut) für Physik, Berlin, Germany")

            linkPrizeLaureate(prize1903Physics, becquerelId, 2, "École Polytechnique, Paris, France")
            linkPrizeLaureate(prize1903Physics, pierreId, 4, "École municipale de physique et de chimie industrielles, Paris, France")
            linkPrizeLaureate(prize1903Physics, curieId, 4, "École municipale de physique et de chimie industrielles, Paris, France")

            linkPrizeLaureate(prize1911Chemistry, curieId, 1, "Sorbonne University, Paris, France")

            linkPrizeLaureate(prize1954Literature, hemingwayId, 1, null)

            linkPrizeLaureate(prize1993Peace, mandelaId, 2, "African National Congress (ANC), South Africa")
            linkPrizeLaureate(prize1993Peace, deKlerkId, 2, "Republic of South Africa, South Africa")
        }
    }

    private fun Laureates.insertAndGetIntId(
        first: String,
        last: String,
        birth: LocalDate?,
        death: LocalDate?,
    ): Int {
        val entityId = Laureates.insert {
            it[firstName] = first
            it[lastName] = last
            it[birthDate] = birth
            it[deathDate] = death
        } get Laureates.id
        return entityId.value
    }

    private fun Prizes.insertAndGetIntId(
        year: Int,
        category: String,
        motivation: String?,
    ): Int {
        val entityId = Prizes.insert {
            it[Prizes.year] = year
            it[Prizes.category] = category
            it[Prizes.motivation] = motivation
        } get Prizes.id
        return entityId.value
    }

    private fun linkPrizeLaureate(prizeId: Int, laureateId: Int, share: Int?, affiliation: String?) {
        PrizeLaureates.insert {
            it[PrizeLaureates.prizeId] = prizeId
            it[PrizeLaureates.laureateId] = laureateId
            it[PrizeLaureates.share] = share
            it[PrizeLaureates.affiliation] = affiliation
        }
    }
}
