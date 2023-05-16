package jobs.core

import java.io.PrintStream

class PipelineResultPrinter {

    fun print(pipelineResult: PipelineResult, output: PrintStream = System.out) {
        output.println()
        for (status in ApplicationResult.Status.values()) {
            output.println()
            output.println("### ${status.name} ###")
            pipelineResult[status].printAsTable()
        }
        output.println()
    }

}