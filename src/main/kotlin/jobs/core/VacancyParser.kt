package jobs.core

interface PlatformParser<out VacancyPage> : Iterable<VacancyPage>

interface VacancyPageParser : Iterable<ApplicationResult>