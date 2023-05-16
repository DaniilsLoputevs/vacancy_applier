package jobs.tools

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintStream
import java.util.*


/**
 * Not working if project run from Tests.
 */
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


class PrintStreamProxyWriteToFile(printStream: PrintStream, private val file: File) : PrintStream(printStream) {

    override fun println(x: String?): Unit = (x ?: "null").also(::fileAppendLine).run { super.println(this) }
    override fun println(x: Any?): Unit = x.toString().also(::fileAppendLine).run { super.println(this) }
    override fun printf(format: String, vararg args: Any?): PrintStream =
        String.format(format, *args).also(::fileAppend).let { super.printf(format, *args) }


    private fun fileAppendLine(content: String) = file.appendText(content + System.lineSeparator())
    private fun fileAppend(content: String) = file.appendText(content)
}