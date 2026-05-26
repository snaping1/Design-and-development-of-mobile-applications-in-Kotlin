package com.example.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init(environment: ApplicationEnvironment) {
        val dbConfig = environment.config.config("database")
        val hikariConfig = HikariConfig().apply {
            driverClassName = dbConfig.property("driver").getString()
            jdbcUrl = dbConfig.property("jdbcUrl").getString()
            username = dbConfig.property("user").getString()
            password = dbConfig.property("password").getString()
            maximumPoolSize = dbConfig.property("maximumPoolSize").getString().toInt()
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        val dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(Users, Prizes, Laureates, PrizeLaureates, UserFavorites)
        }

        val seedOnStartup = dbConfig.propertyOrNull("seedOnStartup")?.getString()?.toBoolean() ?: false
        if (seedOnStartup) {
            SeedData.seedIfEmpty()
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
