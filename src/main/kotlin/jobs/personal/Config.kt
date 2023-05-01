package jobs.personal

interface Config {

    /**
     * Список Базовых/Корневых Поисковых Ссылок.
     * Для HH.ru - почти весь поисковой запрос(ключевые слова, фильтры, регионы, параметры поиска)
     * передаётся в Query Parameters.
     *
     * Список URL по которым человек в Браузере видит страницу с Кучей вакансий и может открыть следующую страницу.
     *
     * Возможно для других платформ этот параметр будет не актуален. Предполагает что это не так.
     */
    val baseSearchLinks: List<String>

    /**
     * Текст сопроводительного письма.
     * HH.ru использует именно такой формат, когда/если будут другие платформы,
     * возможно появиться параметр coverLetterFilePath
     */
    val coverLetter: String

    /**
     * Список слов в Названии вакансии, встретив которые, можно понять что вакансия для нас не подходит.
     * Например: [".Net", "C#", "JS", ... ]
     */
    val uselessVacancyNames: Set<String>


    /**
     * Имя конфига, используется для создания/записи/чтения файлов свяазных с этим Конфигом.
     * Можно считать что это configId.
     */
    fun name(): String = this.javaClass.simpleName
}

/**
 * Класс для общих значений между совершенно разными конфигами.
 */
abstract class BaseConfig : Config


/* Platform Config Classes */


abstract class ConfigHH<LOGIN_DETAILS> : BaseConfig() {

    /**
     * Информация о Логине на платформу
     * Generic т.к. для Потенциально для разных платформ оно может быть Разным.
     */
    abstract val loginDetails: LOGIN_DETAILS

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
    abstract val excludeVacancyLinks: Map<String, String>

}

class LoginDetailsHH(
    val email: String,
    val password: String,
    val loginUrl: String = "https://hh.ru/account/login",
    val captchaWaitingSec: Long,
)

