package jobs.personal


/**
 * Здесь указаны примеры значение конфига, больше информации о Параметрах конфига можно увидеть в файле [Config]
 */

object ExampleConfigHH : ConfigHH<LoginDetailsHH>(
    coverLetter = "текст вашего Сопроводительного письма",
    loginDetails = LoginDetailsHH(
        email = "my_hh_email@gmail.com",
        password = "my_hh_super_secret_password_123^",
        captchaWaitingSec = 120,
    ),

    /**
     *  Список Базовых/Корневых Поисковых Ссылок.
     *  Для HH.ru - почти весь поисковой запрос(ключевые слова, фильтры, регионы, параметры поиска)
     *  передаётся в Query Parameters.
     *  Список URL по которым человек в Браузере видит страницу с Кучей вакансий и может открыть следующую страницу.
     *
     *  Короче, идёте на сайт HH.ru, заносите все-все параметры Поиска вакансий,
     *  нажимаете найти и после загрузке страницы, копируете ссылку на странице сюда.(в двойных кавычках, как в примере).
     *
     *  Можно одну и более. Закончится обработка всех вакансий по одной ссылке, перейдет к вакансиям на следующей ссылке.
     */
    baseSearchLinks = listOf(
        "https://hh.ru/search/vacancy?text=Java+Kotlin&from=suggest_post&salary=&employment=full&schedule=remote&schedule=fullDay&clusters=true&professional_role=96&no_magic=true&ored_clusters=true&items_on_page=20&enable_snippets=true",
        "https://hh.ru/search/vacancy?area=97&area=48&area=1001&area=28&area=9&area=40&area=5&employment=full&schedule=remote&search_field=name&search_field=company_name&search_field=description&text=Java+developer&no_magic=true&L_save_area=true&items_on_page=20",
        ),

    /**
     * Этот параметр Опционален!
     * ВНИМАНИЕ!!! значения ссылок должны быть в Короткой форме, без Query Parameters
     *  */
    excludeVacancyLinks = mapOf(
        "https://hh.ru/vacancy/79078190" to "Android Kotlin Developer - Questions - Android pro",
        "https://hh.ru/vacancy/79078208" to "Senior Android Developer - Questions - relocate",
    ),

    /**
     * Список слов в Названии вакансии, встретив которые, можно понять что вакансия для нас не подходит.
     * Например: ищем мы вакансии на Java
     * Если такие слова есть в названии вакансии, то на такую вакансию мы не будем подавать заявку.
     * Пример списка слов:
     * [".Net", "C#", "JS", ... ]
     */
    uselessVacancyNames = setOf(
        "Front-end", "Frontend",
        "Database Developer",
        ".Net", ".NET", "C#",
    ),
)