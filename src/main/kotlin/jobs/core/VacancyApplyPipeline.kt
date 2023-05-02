package jobs.core

import jobs.personal.Config
import java.io.File
import java.io.FileWriter

interface VacancyApplyPipeline<CONFIG> where CONFIG : Config {
    fun execute(config: CONFIG, session: Session): List<VacancyApplicationResult>

    fun name(): String = this.javaClass.simpleName

    fun readAppliedBeforeVacancies(configName: String, session: Session): List<VacancyApplicationResult> {
        val file = File(getFilePathAppliedBeforeVacancies(configName, session))
        return if (!file.exists()) emptyList()
        else file.useLines {
            it
                .map { fileLine -> fileLine.split(",") }
                .map { nameAndLink ->
                    VacancyApplicationResult(
                        link = nameAndLink[0],
                        name = nameAndLink[1],
                    )
                }
                .toList()
        }
    }

    fun writeAppliedBeforeVacancies(
        configName: String,
        session: Session,
        updatedAppliedBeforeVacancies: List<VacancyApplicationResult>
    ) {
        val filePath = getFilePathAppliedBeforeVacancies(configName, session)
        FileWriter(filePath, false).use { writer ->
            updatedAppliedBeforeVacancies.forEach { writer.write("${it.link},${it.name}${"\r\n"}") }
        }
    }

    private fun getFilePathAppliedBeforeVacancies(configName: String, session: Session): String {
        val fileName = "${this.name()}_${configName}"
        return "${session.appliedBeforeDirPath}/${fileName}.txt"
    }
}