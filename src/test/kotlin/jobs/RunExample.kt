package jobs

import jobs.core.Session
import jobs.hh.PrimaryPipelineHH
import jobs.personal.ExampleConfigHH
import org.junit.jupiter.api.Test

class RunExample {
    private val workDirPath = "C:/Users/user/Desktop/vacancy_applier"


    @Test fun runExampleHH() {
        Session(workDirPath) {
            exe(PrimaryPipelineHH, ExampleConfigHH)
        }
    }

}