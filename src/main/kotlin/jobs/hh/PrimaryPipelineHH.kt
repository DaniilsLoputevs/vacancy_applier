package jobs.hh

import jobs.core.*
import jobs.personal.ConfigHH
import jobs.personal.EmailPasswordHH
import jobs.tools.ConsoleScanner
import jobs.tools.TimeMarker
import jobs.tools.openNewBrowserWindow


object PrimaryPipelineHH : VacancyApplyPipeline<ConfigHH> {
    private const val TIME_MARK__APP_RUN = "### APP RUN ###"
    private const val TIME_MARK__APP_END = "### APP END ###"

    override fun execute(config: ConfigHH, session: Session): PipelineResult {
        println("PIPELINE :: RUN")
        val driver = openNewBrowserWindow(config.browser)
        TimeMarker.addMark(TIME_MARK__APP_RUN)

        when (config.loginDetails) {
            is EmailPasswordHH -> doLoginEmailAndPasswordHH(driver, config.loginDetails)
        }
        val result = PipelineResult(this, config, session)
        val vacancyApplier = VacancyApplierSafe(VacancyApplierHH(config.coverLetter))
        val consoleScanner = ConsoleScanner
        val appliedBeforeVacanciesLoader =
            AppliedBeforeVacanciesLoader(session.appliedBeforeDirPath, this.name(), config.name)

        println("PIPELINE# read store of applied_before vacancies :: RUN")
        val appliedBeforeVacanciesMap = appliedBeforeVacanciesLoader.readAll().associateBy { it.link }
        println("count => loaded vacancies from store of applied_before vacancies : ${appliedBeforeVacanciesMap.size}")
        println("PIPELINE# read store of applied_before vacancies :: END")


        println("PIPELINE# process vacancies :: RUN")
        var currVacancyIndex = 0
        var currPageIndex = 0
        /*
        * Если как-нибудь Гребаный Рандомный Exception и решит Прикольнуться и сломать боевой запуск!
        * Мы его Поймаем и Отшлёпаем по Попе!!!
        * он НЕ ЗА РУИНИТ printResult!!!
        */
        try {
            config.baseSearchLinks
                .asSequence()
                .map { ScrapItHHParser(it) }
                .flatten()
                .onEach { println("current page: ${currPageIndex++} - ${it.hhPageUrl}") } // log
                .flatten()

                .filterNot { appliedBeforeVacanciesMap.containsKey(it.link) }
                .filterNot { config.excludeVacancyLinks.containsKey(it.link) }
                .filterNot { tag -> config.uselessVacancyNames.any { tag.name.contains(it, true) } }

                .takeWhile { vacancyApplier.canContinueProcess }
                .takeWhile { consoleScanner.isContinue() } /* Not working if project run from Tests. */

                .onEach { it.pageIndex = currPageIndex }
                .onEach { println("current vacancy : ${currVacancyIndex++} - ${it.name}(${it.link})") } // log
                .onEach { vacancyApplier.process(driver, it) }
                .forEach(result::plusAssign) // terminate
        } catch (e: Exception) {
            println("ERR: Pipeline exit with exception")
            e.printStackTrace(System.out) // что бы не нарушать порядок логов + записать.
        }
        println("PIPELINE# process vacancies :: END")

        TimeMarker.addMark(TIME_MARK__APP_END)

        println("PIPELINE# print result :: RUN")
        println("### SHOW RESULT ###")
        println("count => loaded vacancies from store of applied_before vacancies : ${appliedBeforeVacanciesMap.size}")
        println("count => vacancies applied : ${vacancyApplier.successApplicationCounter}")
        TimeMarker.printMarks()
        TimeMarker.printBetweenMarks(TIME_MARK__APP_RUN, TIME_MARK__APP_END)
        TimeMarker.clear()
        PipelineResultPrinter().print(result)
        println("PIPELINE# show result :: END")


        println("PIPELINE# update store of applied_before vacancies :: RUN")
        val newAppliedBeforeVacancies = sequenceOf(
            result[ApplicationResult.Status.APPLIED_NOW],
            result[ApplicationResult.Status.APPLIED_BEFORE]
        ).flatten().toList()
        appliedBeforeVacanciesLoader.writeAppendAll(newAppliedBeforeVacancies)
        println("PIPELINE# update store of applied_before vacancies :: END")

        println("PIPELINE :: END")
        return result
    }

}