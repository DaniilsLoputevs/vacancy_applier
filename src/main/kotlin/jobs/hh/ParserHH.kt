package jobs.hh

import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import jobs.core.ApplicationResult
import jobs.core.PlatformParser
import jobs.core.VacancyPageParser
import jobs.tools.TimeMarker
import jobs.tools.tryAnyException
import java.util.concurrent.CompletableFuture

class ScrapItHHParser(hhBaseSearchUrl: String) : PlatformParser<HHPageParser> {
    /**
     * Опция HH.ru "Кол-во вакансий на странице: [20, 50, 100]"
     * В принципе, можно доставать по 50, загрузка первой странице происходит асинхронно в другом потоке
     * и страница будет Готов к моменту её надобности.(закончилась предыдущая, нужно следующая если есть)
     * Зато меньше Запросов в сеть и меньше качать ненужных сопроводительных данных.
     * Например, на 1 000 вакансий:
     * по 20  -> 1 000 / 20  = 50 (запросов в сеть)
     * по 50  -> 1 000 / 50  = 20 (запросов в сеть) - выглядит Оптимальным кол-во запросов на кол-во вакансий
     * по 100 -> 1 000 / 100 = 10 (запросов в сеть)
     */
    val hhBaseSearchUrlWithoutPage = hhBaseSearchUrl.replace("items_on_page=20", "items_on_page=50")
    val pageCountSize by lazy {
        skrape(HttpFetcher) {
            request { url = hhBaseSearchUrl }
            response { htmlDocument { findAll("a[data-qa='pager-page']").last().text.let(Integer::parseInt) } }
        }
    }

    override operator fun iterator(): Iterator<HHPageParser> = object : Iterator<HHPageParser> {
        var pageCursorIndex = 0
        var currentPageFuture: CompletableFuture<HHPageParser> = updateCurrentPage()


        override fun hasNext(): Boolean = pageCursorIndex < pageCountSize

        override fun next(): HHPageParser {
            if (!hasNext()) throw NoSuchElementException()
            TimeMarker.addMark("HHPage iterator next() - update future GET")
            val currPage = currentPageFuture.get()
            currentPageFuture = updateCurrentPage()
            return currPage
        }

        private fun updateCurrentPage(): CompletableFuture<HHPageParser> {
            TimeMarker.addMark("HHPage iterator next() - update future SUPPLY")
            return if (hasNext())
                CompletableFuture.supplyAsync {
                    return@supplyAsync HHPageParser("$hhBaseSearchUrlWithoutPage&page=${pageCursorIndex++}")
                }.exceptionally {
                    it.printStackTrace(System.out)
                    return@exceptionally null
                }
            else CompletableFuture.completedFuture(null)
        }
    }
}

class HHPageParser(val hhPageUrl: String) : VacancyPageParser {
    private val vacancies = tryAnyException({ takeAllH3ElementsOnPage(hhPageUrl) }, { emptyList() })
        .asSequence()
        /* filter useless elements from list like "do you want to receive an email notification?"
        * useless elements don't have a children elements. */
        .filter { it.children.isNotEmpty() }
        .map {
            ApplicationResult(
                it.text,
                it.children[0].children[0].attribute("href")
                    /* before : "https://hh.ru/vacancy/80504067?from=vacancy_search_list&query=Java"
                    *  after  : "https://hh.ru/vacancy/80504067" - hh link compact format */
                    .let { link -> link.substring(0, link.indexOf("?")) },
            )
        }

    private fun takeAllH3ElementsOnPage(hhPageUrl: String) = skrape(HttpFetcher) {
        request { url = hhPageUrl }
        return@skrape response { htmlDocument { findAll("h3") } }
    }

    override operator fun iterator(): Iterator<ApplicationResult> = vacancies.iterator()
}