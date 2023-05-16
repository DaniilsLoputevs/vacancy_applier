package jobs.core

import jobs.personal.Config
import jobs.tools.PrintStreamProxyWriteToFile
import jobs.tools.sureExistsFilePath
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Private primary constructor -> use
 */
class Session(
    private val workDirAbsolutePath: String,
    isDebug: Boolean = false,
    pipelinesExeRunDsl: Session.() -> Unit
) : AutoCloseable {

    private val originalSystemOut: PrintStream = System.out

    val logDirPath = sureExistsFilePath("${workDirAbsolutePath}/logs")
    val appliedBeforeDirPath = sureExistsFilePath("${workDirAbsolutePath}/applied_before")

    /** Primary constructor body */
    init {
        val maybeDebugPrefix = (if (isDebug) "debug_" else "")
        val startDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
        val logFileName = "${maybeDebugPrefix}session_${startDateTime}.txt"
        val logFile = File("${logDirPath}/${logFileName}")

        System.setOut(PrintStreamProxyWriteToFile(System.out, logFile))

        this.use { pipelinesExeRunDsl() }
    }


    /**
     * this Session scope execute pipeline with config as argument.
     */
    fun <T : Config> exe(pipeline: VacancyApplyPipeline<T>, config: T) = pipeline.execute(config, this)

    override fun close() = System.setOut(originalSystemOut)

}
