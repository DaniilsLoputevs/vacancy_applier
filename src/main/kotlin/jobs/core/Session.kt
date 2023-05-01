package jobs.core

import jobs.tools.SystemOutInterceptor
import jobs.tools.sureExistsFilePath
import java.io.PrintStream

class Session(private val workDirAbsolutePath: String, isDebug : Boolean = false) : AutoCloseable {
    val originalSystemOut: PrintStream

    val logDirPath = sureExistsFilePath("${workDirAbsolutePath}/logs")
    val appliedBeforeDirPath = sureExistsFilePath("${workDirAbsolutePath}/applied_before")

    /** Primary constructor body */
    init {
        originalSystemOut = System.out
        System.setOut(SystemOutInterceptor(System.out, logDirPath, isDebug))
    }


    override fun close() {
        System.setOut(originalSystemOut)
    }
}
