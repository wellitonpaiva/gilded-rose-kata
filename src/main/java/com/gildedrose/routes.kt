package com.gildedrose

import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.template.HandlebarsTemplates
import org.http4k.template.ViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

private val dateFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)

private val handlebars = HandlebarsTemplates().HotReload("src/main/java")
fun routes(stock: () -> StockList, calendar: () -> LocalDate = LocalDate::now) =
    routes(
        "/" bind Method.GET to { _ ->
            val now = calendar()
            val stockList = stock()
            Response(Status.OK).body(handlebars(
                StockListViewModel(
                    now = dateFormat.format(now),
                    items = stockList.map { it.toMap(now) }
                )

            ))
        }
    )

private data class StockListViewModel(
    val now: String,
    val items: List<Map<String, String>>
) : ViewModel

private fun Item.toMap(now: LocalDate): Map<String, String> = mapOf(
    "name" to name,
    "sellByDate" to dateFormat.format(sellByDate),
    "sellByDays" to this.daysUntilSellBy(now).toString(),
    "quality" to quality.toString()
)

private fun Item.daysUntilSellBy(now: LocalDate): Long = ChronoUnit.DAYS.between(now, this.sellByDate)
