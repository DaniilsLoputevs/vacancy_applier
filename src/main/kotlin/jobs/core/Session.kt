package jobs.core

import jobs.personal.Config
import jobs.tools.PrintStreamProxyWriteToFile
import jobs.tools.sureExistsFilePath
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Session(private val workDirAbsolutePath: String, isDebug: Boolean = false) : AutoCloseable {
    private val originalSystemOut: PrintStream = System.out // todo - check

    val logDirPath = sureExistsFilePath("${workDirAbsolutePath}/logs")
    val appliedBeforeDirPath = sureExistsFilePath("${workDirAbsolutePath}/applied_before")

    /** Primary constructor body */
    init {
        val maybeDebugPrefix = (if (isDebug) "debug_" else "")
        val startDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        val logFileName = "${maybeDebugPrefix}session_${startDateTime}.txt"
        val logFile = File("${logDirPath}/${logFileName}")

        System.setOut(PrintStreamProxyWriteToFile(System.out, logFile))
    }

    override fun close() {
        System.setOut(originalSystemOut)
    }

}

fun <T> Session.exe(pipeline: VacancyApplyPipeline<T>, config: T) where T : Config =
    pipeline.execute(config, this)


fun session(workDirAbsolutePath: String, isDebug: Boolean = false, dsl: Session.() -> Unit) {
    Session(workDirAbsolutePath, isDebug).use { it.dsl() }
}
