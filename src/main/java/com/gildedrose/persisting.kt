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


fun File.loadItems(): StockList = useLines { lines ->
    lines.toStockList()
}
private fun Item.toLine() = "$name\t$sellByDate\t$quality"

fun Sequence<String>.toStockList(): StockList {
    val (header, body) = partition { it.startsWith("#") }
    return StockList(
        lastModified = lastModifiedFrom(header) ?: Instant.EPOCH,
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

