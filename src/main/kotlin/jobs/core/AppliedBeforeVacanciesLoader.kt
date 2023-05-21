package jobs.core

import java.io.File
import java.io.FileWriter

class AppliedBeforeVacanciesLoader(
    appliedBeforeDirPath: String,
    pipelineName: String,
    configName: String,
) {
    private val appliedBeforeVacanciesFilePath = "${appliedBeforeDirPath}/${pipelineName}_${configName}.txt"

    /**
     * @see [java.io.BufferedReader.readLine]()
     */
    fun readAll(): List<ApplicationResult> {
        val file = File(appliedBeforeVacanciesFilePath)
        return if (!file.exists()) emptyList()
        else file.useLines {
            it
                .map { fileLine -> fileLine.split(",") }
                .map { nameAndLink ->
                    ApplicationResult(
                        link = nameAndLink[0],
                        name = nameAndLink[1],
                    )
                }
                .toList()
        }
    }

    fun writeAppendAll(updatedAppliedBeforeVacancies: List<ApplicationResult>) =
        FileWriter(appliedBeforeVacanciesFilePath, true).use { writer ->
            updatedAppliedBeforeVacancies.forEach { writer.write("${it.link},${it.name}${"\r\n"}") }
        }

}