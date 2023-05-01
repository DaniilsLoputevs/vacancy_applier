package jobs.tools

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


object ConsoleScanner {
    private var exit = false

    fun isContinue(): Boolean {
        return !exit
    }

    init {
        GlobalScope.launch {
            val scanner = Scanner(System.`in`)
            while (true) {
                when (scanner.nextLine()) {
                    "exit" -> {
                        exit = true
                        break
                    }

                    else -> {}
                }
            }
        }
    }
}


// https://stackoverflow.com/questions/4334808/how-could-i-read-java-console-output-into-a-string-buffer
open class SystemOutInterceptor(
    printStream: PrintStream,
    logDirPath: String,
    isDebug: Boolean
) : PrintStream(printStream) {

    private var logFileNameStrategy: () -> String = { "${maybeDebugPrefix}session_${startDateTime}.txt" }

    private val logFile by lazy { File("${logDirPath}/${logFileNameStrategy.invoke()}") }


    override fun println(x: String?) {
        fileAppendLine(x.toString())
        super.println(x)
    }

    override fun println(x: Any?) {
        fileAppendLine(x.toString())
        super.println(x)
    }

    override fun printf(format: String, vararg args: Any?): PrintStream {
        fileAppendLine(String.format(format, *args))
        return super.printf(format, *args)
    }

    private val maybeDebugPrefix = (if (isDebug) "debug_" else "")
    private val startDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))

    private fun fileAppendLine(content: String) = logFile.appendText(content + System.lineSeparator())
}