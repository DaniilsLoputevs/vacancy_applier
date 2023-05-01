package jobs.personal

interface Config<LOGIN_DETAILS> {
    /**
     * Информация о Логине на платформу
     * Generic т.к. для Потенциально для разных платформ оно может быть Разным.
     */
    val loginDetails: LOGIN_DETAILS

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
    fun configName() : String = this.javaClass.simpleName
}

/**
 * Класс для общих значений между совершенно разными конфигами.
 */
abstract class BaseConfig<LOGIN_DETAILS> : Config<LOGIN_DETAILS> {

}

