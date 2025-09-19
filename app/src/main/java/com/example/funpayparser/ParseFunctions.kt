package com.example.funpayparser

import org.jsoup.Jsoup

fun lotsParser(url: String = "https://funpay.com/chips/99/"): List<Lot> {
    val html = Scraper.fetchHtml(url)
    println("fetching")

    if (html.isNullOrBlank()) {
        throw Exception("HTML не загружен")
    }

    val parsedHtml = Jsoup.parse(html)

    val lots = parsedHtml.select("a.tc-item").sortedBy {
        it.selectFirst(".tc-price")?.text()?.split(" ")
            ?.get(0)
            ?.toDouble()
    }
    val lotsClassed = mutableListOf<Lot>()
    for (lot in lots) {
        val price =
            lot.selectFirst(".tc-price")?.text()?.split(" ")?.get(0)?.toDoubleOrNull() ?: 9999.9
        val link = lot.attr("href")
        val quantity = lot.selectFirst(".tc-amount")?.attr("data-s")?.toLongOrNull() ?: -1
        val merchant = lot.selectFirst(".tc-user")?.selectFirst(".media-body")
        val merchantName = merchant?.selectFirst(".media-user-name")?.text() ?: ""
        val reviewsCount = merchant?.selectFirst(".media-user-reviews")?.text()?.toIntOrNull()
            ?: merchant?.selectFirst(".media-user-reviews")?.text()?.split(" ")
                ?.get(0)
                ?.toIntOrNull() ?: 0
        lotsClassed += Lot(link, price, quantity, merchantName, reviewsCount)
    }
    return lotsClassed
}