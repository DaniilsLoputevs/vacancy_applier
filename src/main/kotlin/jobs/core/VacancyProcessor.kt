package jobs.core

import jobs.tools.openNewTab
import org.openqa.selenium.remote.RemoteWebDriver
import java.util.concurrent.TimeUnit


interface VacancyProcessor {
    var successApplicationCounter: Int
    val canContinueProcess: Boolean
    fun process(driver: RemoteWebDriver, rsl: VacancyApplicationResult)
}

class VacancyApplierSafe(private val applier: VacancyProcessor) : VacancyProcessor by applier {

    override fun process(driver: RemoteWebDriver, rsl: VacancyApplicationResult) {
        val startTime = System.currentTimeMillis()
        try {
            applier.process(driver, rsl)
        } catch (exception: Throwable) {
            println("ERR: ${rsl.link} - ${exception.javaClass.simpleName}")
            exception.printStackTrace(System.out) // Пишем в обычный лог, что бы не терять Последовательность.
            rsl.applyException = exception
            rsl.applyStatus = VacancyApplicationResult.Status.EXCEPTION
            driver.openNewTab() // оставляем открытой вкладку с exception - что бы позже Ручками разобраться
        } finally {
            val endTime = System.currentTimeMillis()
            rsl.applyExecutionTimeSec = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime)
        }
    }
}
