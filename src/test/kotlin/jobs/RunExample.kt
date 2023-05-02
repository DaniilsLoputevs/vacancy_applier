package jobs

import jobs.core.exe
import jobs.core.session
import jobs.hh.PrimaryPipelineHH
import jobs.personal.ExampleConfigHH
import org.junit.jupiter.api.Test

class RunExample {
    private val workDirPath = "C:/Users/user/Desktop/vacancy_applier"


    @Test fun runExampleHH() {
        session(workDirPath) {
            exe(PrimaryPipelineHH, ExampleConfigHH)
        }
    }

}