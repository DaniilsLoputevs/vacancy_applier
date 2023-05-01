package jobs.tools

import jobs.core.VacancyApplicationResult

fun printVacancyResult(appliedResults: List<VacancyApplicationResult>) {
    for (status in VacancyApplicationResult.Status.values()) {
        println()
        println("### ${status.name} ###")
        appliedResults.copyFilterByStatus(status).printAsTable()
    }
    println()
}

fun List<VacancyApplicationResult>.copyFilterByStatus(status: VacancyApplicationResult.Status) = this
    .asSequence()
    .filter { it.applyStatus == status }
    .toList()


fun Collection<VacancyApplicationResult>.printAsTable() = PrintTable.of(this)
    .name("Vacancy Application Result")
    .columnElemIndex()
    .column("NAME", VacancyApplicationResult::name)
    .column("LINK", VacancyApplicationResult::link)
    .column("PAGE", VacancyApplicationResult::pageIndex)
    .column("STATUS", VacancyApplicationResult::applyStatus)
    .column("EXCEPTION", VacancyApplicationResult::exceptionToString)
    .column("TIME(SEC)", VacancyApplicationResult::applyExecutionTimeSec)
    .print()
