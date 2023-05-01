package jobs.hh

import jobs.core.Session
import jobs.core.VacancyApplicationResult
import jobs.core.VacancyApplierSafe
import jobs.core.VacancyApplyPipeline
import jobs.personal.ConfigHH
import jobs.personal.LoginDetailsHH
import jobs.tools.ConsoleScanner
import jobs.tools.TimeMarker
import jobs.tools.oneNewChromeBrowser
import jobs.tools.printVacancyResult


object PrimaryPipelineHH : VacancyApplyPipeline<ConfigHH<LoginDetailsHH>> {
    private const val TIME_MARK__APP_RUN = "### APP RUN ###"
    private const val TIME_MARK__APP_END = "### APP END ###"

    override fun execute(config: ConfigHH<LoginDetailsHH>, session: Session): List<VacancyApplicationResult> {
        println("PIPELINE :: RUN")
        val driver = oneNewChromeBrowser()
        TimeMarker.addMark(TIME_MARK__APP_RUN)
        var currVacancyIndex = 0
        var currPageIndex = 0

        doLoginEmailAndPasswordHH(driver, config.loginDetails)
        val vacancyApplier = VacancyApplierSafe(VacancyApplierHH(config.coverLetter))
        val consoleScanner = ConsoleScanner

        println("PIPELINE# read store of applied_before vacancies :: RUN")
        val appliedBeforeVacanciesMap = readAppliedBeforeVacancies(config.name(), session).associateBy { it.link }
        println("Загружено с диска, кол-во Вакансий со статусом APPLIED_NOW: ${appliedBeforeVacanciesMap.size}")
        println("PIPELINE# read store of applied_before vacancies :: END")


        println("PIPELINE# process vacancies :: RUN")
        /*
        * Если как-нибудь Гребаный Рандомный Exception и решит Прикольнуться и сломать боевой запуск!
        * Мы его Поймаем и Отшлёпаем по Жопе!!!
        * он НЕ ЗА РУИНИТ printResult!!!
        */
        val vacancyApplyResults = mutableListOf<VacancyApplicationResult>()
        try {
            config.baseSearchLinks
                .asSequence()
                .map { ScrapItHHParser(it) }
                .flatten()
                .onEach { println("current page: ${currPageIndex++} - ${it.hhPageUrl}") } // log
                .flatten()

                .onEach {
                    if (appliedBeforeVacanciesMap.containsKey(it.link))
                        it.applyStatus = VacancyApplicationResult.Status.APPLIED_BEFORE
                }
                .filterNot { config.excludeVacancyLinks.containsKey(it.link) }
                .filterNot { it.applyStatus == VacancyApplicationResult.Status.APPLIED_BEFORE }
                .filterNot { tag -> config.uselessVacancyNames.any { tag.name.contains(it, true) } }

                .takeWhile { vacancyApplier.canContinueProcess }
                .takeWhile { consoleScanner.isContinue() } // при запуске из Test это не работает.

                .onEach { it.pageIndex = currPageIndex }
                .onEach { println("current vacancy : ${currVacancyIndex++} - ${it.name}(${it.link})") } // log
                .onEach { vacancyApplier.process(driver, it) }
                .forEach(vacancyApplyResults::add) // terminate
        } catch (e: Exception) {
            println("ERR: Pipeline exit with exception")
            e.printStackTrace(System.out) // что бы не нарушать порядок логов + записать.
        }
        println("PIPELINE# process vacancies :: END")

        TimeMarker.addMark(TIME_MARK__APP_END)

        println("PIPELINE# show result :: RUN")
        println("### SHOW RESULT ###")
        println("Было загружено с диска и проигнорировано в обработке, Вакансий со статусом APPLIED_BEFORE: ${appliedBeforeVacanciesMap.size}")
        println("Было подано заявок на такое кол-во вакансий: ${vacancyApplier.successApplicationCounter - 1}") // -1 т.к. был increment
        TimeMarker.printMarks()
        TimeMarker.printBetweenMarks(TIME_MARK__APP_RUN, TIME_MARK__APP_END)
        TimeMarker.clear()
        printVacancyResult(vacancyApplyResults)
        println("PIPELINE# show result :: END")


        println("PIPELINE# update store of applied_before vacancies :: RUN")
        val foundedAppliedVacancies = vacancyApplyResults.toList()
            .filter {
                when (it.applyStatus) {
                    VacancyApplicationResult.Status.APPLIED_BEFORE -> true
                    VacancyApplicationResult.Status.APPLIED_NOW -> true
                    else -> false
                }
            }

        val allAppliedBeforeVacancies = (appliedBeforeVacanciesMap.values + foundedAppliedVacancies)
        writeAppliedBeforeVacancies(config.name(), session, allAppliedBeforeVacancies)
        println("PIPELINE# update store of applied_before vacancies :: END")

        println("PIPELINE :: END")
        return vacancyApplyResults
    }

}