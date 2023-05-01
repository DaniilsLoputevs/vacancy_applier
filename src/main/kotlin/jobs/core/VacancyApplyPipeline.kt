package jobs.core

import jobs.personal.Config
import java.io.File
import java.io.FileWriter

interface VacancyApplyPipeline<CONFIG> where CONFIG : Config<*> {
    fun execute(config: CONFIG, session: Session): List<VacancyApplicationResult>

    fun pipelineName() : String = this.javaClass.simpleName

    fun readAppliedBeforeVacancies(config: CONFIG, session: Session): List<VacancyApplicationResult> {
        val file = File(getFilePathAppliedBeforeVacancies(config, session))
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
        config: CONFIG,
        session: Session,
        updatedAppliedBeforeVacancies: List<VacancyApplicationResult>
    ) {
        val filePath = getFilePathAppliedBeforeVacancies(config, session)
        FileWriter(filePath, false).use { writer ->
            updatedAppliedBeforeVacancies.forEach { writer.write("${it.link},${it.name}${"\r\n"}") }
        }
    }

    private fun getFilePathAppliedBeforeVacancies(config: CONFIG, session: Session): String {
        val fileName = "${this.pipelineName()}_${config.configName()}"
        return "${session.appliedBeforeDirPath}/${fileName}.txt"
    }
}