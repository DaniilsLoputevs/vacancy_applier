package jobs.tools

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.PrintStream
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
class PrintStreamProxyWriteToFile(printStream: PrintStream, private val file: File) : PrintStream(printStream) {


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


    private fun fileAppendLine(content: String) = file.appendText(content + System.lineSeparator())
}