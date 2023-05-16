package jobs.core

import jobs.tools.PrintTable
import jobs.tools.toStringCompact
import org.openqa.selenium.WebDriverException


class ApplicationResult(
    var name: String,
    var link: String,
    var applyStatus: Status = Status.UNKNOWN,
    var pageIndex: Int = -1,
) {
    var applyOrder: Int = -1
    var applyException: Throwable? = null
    var applyExecutionTimeSec: Long = -1

    fun exceptionToString(): String {
        return try {
            when (applyException) {
                null -> "null"
                is WebDriverException -> (applyException as WebDriverException).toStringCompact
                else -> applyException.toString()
            }
        } catch (e: Exception) {
            applyException.toString()
        }
    }

    enum class Status {
        APPLIED_NOW,
        EXCEPTION,
        QUESTIONS,
        APPLIED_BEFORE,
        UNKNOWN,
        SKIP
    }

}

fun Collection<ApplicationResult>.printAsTable() = PrintTable.of(this)
    .name("Vacancy Application Result")
    .columnElemIndex()
    .column("NAME", ApplicationResult::name)
    .column("LINK", ApplicationResult::link)
    .column("PAGE", ApplicationResult::pageIndex)
    .column("STATUS", ApplicationResult::applyStatus)
    .column("EXCEPTION", ApplicationResult::exceptionToString)
    .column("TIME(SEC)", ApplicationResult::applyExecutionTimeSec)
    .print()