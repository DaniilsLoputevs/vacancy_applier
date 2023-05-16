package jobs.core

import jobs.personal.Config

interface VacancyApplyPipeline<CONFIG : Config> {
    fun execute(config: CONFIG, session: Session): PipelineResult

    fun name(): String = this.javaClass.simpleName

}