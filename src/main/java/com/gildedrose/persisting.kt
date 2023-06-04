package com.gildedrose

import java.io.File
import java.io.IOException
import java.time.DateTimeException
import java.time.Instant
import java.time.LocalDate

fun StockList.saveTo(file: File) =
    file.writer().buffered().use { writer ->
        toLines().forEach { line -> writer.appendLine(line) }
    }

fun StockList.toLines(): Sequence<String> =
    sequenceOf("# LastModified: ${this.lastModified}") +
        items.map { it.toLine() }


fun File.loadItems(defaultLastModified: Instant = Instant.now()): StockList = useLines { lines ->
    lines.toStockList(defaultLastModified)
}
private fun Item.toLine() = "$name\t$sellByDate\t$quality"

fun Sequence<String>.toStockList(
    defaultLastModified: Instant
): StockList {
    val (header, body) = partition { it.startsWith("#") }
    return StockList(
        lastModified = lastModifiedFrom(header) ?: defaultLastModified,
        items = body.map { line -> line.toItem() }.toList()
    )
}

private fun lastModifiedFrom(
    header: List<String>
) = (header
    .lastOrNull{it.startsWith("# LastModified:")}
    ?.substring("# LastModified:".length)
    ?.trim()
    ?.toInstant())

private fun String.toInstant() = try {
    Instant.parse(this)
} catch (x: DateTimeException) {
    throw IOException("Could not parse LastModified header: " + x.message)
}

private fun String.toItem(): Item {
    val parts = this.split('\t')
    return Item(
        name = parts[0],
        sellByDate = LocalDate.parse(parts[1]),
        quality = parts[2].toUInt()
    )
}

