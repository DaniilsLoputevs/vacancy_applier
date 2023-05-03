package jobs.personal

open class Config(
    /**
     * Список Базовых/Корневых Поисковых Ссылок.
     * Для HH.ru - почти весь поисковой запрос(ключевые слова, фильтры, регионы, параметры поиска)
     * передаётся в Query Parameters.
     *
     * Список URL по которым человек в Браузере видит страницу с Кучей вакансий и может открыть следующую страницу.
     *
     * Возможно для других платформ этот параметр будет не актуален. Предполагает что это не так.
     */
    open val baseSearchLinks: List<String>,

    /**
     * Текст сопроводительного письма.
     * HH.ru использует именно такой формат, когда/если будут другие платформы,
     * возможно появиться параметр coverLetterFilePath
     */
    open val coverLetter: String,

    /**
     * Список слов в Названии вакансии, встретив которые, можно понять что вакансия для нас не подходит.
     * Например: [".Net", "C#", "JS", ... ]
     */
    open val uselessVacancyNames: Set<String>,

    /**
     * Выбор какой браузер использовать.
     * На вашем компьютере должен быть установлен выбранный браузер.
     * На выбор: [ GOOGLE_CHROME, MOZILLA_FIREFOX, SAFARI, EDGE ]
     */
    open val browser: BrowserType
) {
    /**
     * Имя конфига, используется для создания/записи/чтения файлов свяазных с этим Конфигом.
     * Можно считать что это configId.
     */
    val name: String = this.javaClass.simpleName
}

enum class BrowserType { GOOGLE_CHROME, MOZILLA_FIREFOX, MICROSOFT_EDGE, SAFARI  }


/* Platform Config Classes */


abstract class ConfigHH(
    /**
     * Информация о Логине на платформе HH.ru
     * [LoginDetailsHH] это Базовый тип, для всех реализаций Конфиг для логина.
     * Важно! Тут могут быть кардинально разные класс и их структура.
     * Стоит обрабатывать этот параметр через паттерн стратегия + when.
     * Пример:
     * ```
     * when (config.loginDetails){
     *  is loginDetailsTypeOne -> loginHHTypeOne
     *  is loginDetailsTypeTwo -> loginHHTypeTwo
     *  }
     * ```
     */
    val loginDetails: LoginDetailsHH,

    /**
     * Список вакансий на Которые НЕ НУЖНО откликаться, например:
     * Вака раза 2+ попадается в поиске и там Вопросы и Релокация.
     * Что бы, не гасить быстродействие, такие ваки можно Ручками добавлять в этот список.
     *
     * ВНИМАНИЕ!!! значения ссылок должны быть в Короткой форме, без Query Parameters.
     *
     * Key - ссылка на вакансию.
     * Val - описание Почему эта ссылка в Исключениях.
     */
    val excludeVacancyLinks: Map<String, String>,

    override val baseSearchLinks: List<String>,
    override val coverLetter: String,
    override val uselessVacancyNames: Set<String>,
    override val browser: BrowserType,
) : Config(baseSearchLinks, coverLetter, uselessVacancyNames, browser)

sealed interface LoginDetailsHH

class EmailPasswordHH(
    val email: String,
    val password: String,
    val loginUrl: String = "https://hh.ru/account/login",
    val captchaWaitingSec: Long,
) : LoginDetailsHH
