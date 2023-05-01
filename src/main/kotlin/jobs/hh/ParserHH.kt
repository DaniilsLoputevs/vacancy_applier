package jobs.hh

import jobs.core.PlatformParser
import jobs.core.VacancyApplicationResult
import jobs.core.VacancyPageParser
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import jobs.tools.TimeMarker
import java.util.concurrent.CompletableFuture

val HH_PAGE_USELESS_ELEMENT_TEXT_LIST = setOf(
    "По вашему запросу ещё будут появляться новые вакансии. Присылать вам?",
    "Быстрые фильтры",
    "Как вам результаты поиска?",
)

class ScrapItHHParser(hhBaseSearchUrl: String) : PlatformParser<HHPageParser> {
    val hhBaseSearchUrlWithoutPage = hhBaseSearchUrl
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
            return if (hasNext()) CompletableFuture.supplyAsync {
                TimeMarker.addMark("HHPage iterator updateCurrentPage() - RUN")
                val rsl = HHPageParser("${hhBaseSearchUrlWithoutPage}&page=${pageCursorIndex++}")
                TimeMarker.addMark("HHPage iterator updateCurrentPage() - RUN")
                return@supplyAsync rsl
            }
            else CompletableFuture.completedFuture(null)
        }
    }
}

class HHPageParser(val hhPageUrl: String) : VacancyPageParser {
    private val vacancies = takeAllH3ElementsOnPage(hhPageUrl)
        .asSequence()
        .filter { it.text !in HH_PAGE_USELESS_ELEMENT_TEXT_LIST }
        .map {
            VacancyApplicationResult(
                it.text,
                it.children[0].children[0].attribute("href")
                    // Обрезаем ссылку, что бы в итоговой таблице занимала меньше места в ширь.
                    .let { link -> link.substring(0, link.indexOf("?")) },
            )
        }

    private fun takeAllH3ElementsOnPage(hhPageUrl: String) = skrape(HttpFetcher) {
        request { url = hhPageUrl }
        return@skrape response { htmlDocument { findAll("h3") } }
    }

    override operator fun iterator(): Iterator<VacancyApplicationResult> = vacancies.iterator()
}