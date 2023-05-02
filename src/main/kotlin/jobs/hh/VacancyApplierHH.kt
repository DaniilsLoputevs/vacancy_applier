package jobs.hh

import jobs.core.VacancyApplicationResult
import jobs.core.VacancyProcessor
import jobs.tools.openNewTab
import jobs.tools.tryWaitUntilClickableThenClick
import org.openqa.selenium.By
import org.openqa.selenium.remote.RemoteWebDriver


class VacancyApplierHH(private val coverLetter: String) : VacancyProcessor {
    override var successApplicationCounter: Int = 1

    /** HH.ru имеет суточный лимит на 200 Откликов. */
    override val canContinueProcess: Boolean get() = successApplicationCounter <= 200

    override fun process(driver: RemoteWebDriver, rsl: VacancyApplicationResult) {
        driver.get(rsl.link)
        when (isVacancyAppliedBefore(driver)) {
            false -> applyLogic(driver, rsl)
            true -> {
                println("APPLIED_BEFORE :: RUN")
                rsl.applyStatus = VacancyApplicationResult.Status.APPLIED_BEFORE
                println("APPLIED_BEFORE :: END")
            }
        }
        println("STATUS       : ${rsl.applyStatus}")
    }

    private fun applyLogic(driver: RemoteWebDriver, rsl: VacancyApplicationResult) {
        println("APPLIED_NOW :: RUN")
        driver.findElement(By.cssSelector("a[href*='/applicant/vacancy_response?vacancyId=']"))
            .click() // click on apply button

        // Если exception - наверно мы в одном из случаев:
        // 1 - Предупреждение о Другой локации НЕ ПОЯВИЛОСЬ - это нормально
        driver.tryWaitUntilClickableThenClick(1, By.cssSelector("button[data-qa='relocation-warning-confirm']"))
        // Если exception - наверно мы в одном из случаев:
        // 1 - textarea уже РАЗВЁРНУТА - это нормально, иногда она Открыта по умолчанию.
        driver.tryWaitUntilClickableThenClick(1, By.cssSelector("button[data-qa='vacancy-response-letter-toggle']"))

        driver.findElement(By.tagName("textarea")).sendKeys(coverLetter)
        driver.findElement(By.cssSelector("button[data-qa='vacancy-response-submit-popup']")).click()

        if (driver.currentUrl == rsl.link) {
            rsl.applyStatus = VacancyApplicationResult.Status.APPLIED_NOW
            rsl.applyOrder = successApplicationCounter++
            println("APPLIED_NOW :: END - APPLIED")
        } else {
            rsl.applyStatus = VacancyApplicationResult.Status.QUESTIONS
            driver.openNewTab() // оставляем открытой вкладку с вопросами или Непонятными ситуациями
            println("APPLIED_NOW :: END - QUESTIONS")
        }
    }

    /**
     * Если кнопка имеет текст "Смотреть отклик"|| "Смотреть отказ" - значит она её href будет ссылка на Чат.
     * Проверяем что на странице ЕСТЬ линк ведущий в Чат. Если у на "Откликнутся", такой ссылки на странице НЕ БУДЕТ.
     * driver.findElements(By.cssSelector("a[href*='/applicant/vacancy_response?vacancyId=']")) // Откликнутся
     * driver.findElements(By.cssSelector("a[href*='/applicant/negotiations/item']")) // Смотреть Отклик || Отказ
     */
    private fun isVacancyAppliedBefore(driver: RemoteWebDriver): Boolean =
        driver.findElements(By.cssSelector("a[href*='/applicant/negotiations/item']")).size > 0

}