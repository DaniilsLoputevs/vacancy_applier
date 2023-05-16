package jobs.core

import jobs.personal.Config
import java.util.*

class PipelineResult(
    val pipeline: VacancyApplyPipeline<*>,
    val config: Config,
    val session: Session,
) {
    private val processedVacancies =
        EnumMap<ApplicationResult.Status, MutableList<ApplicationResult>>(ApplicationResult.Status::class.java)
            .apply { ApplicationResult.Status.values().forEach { status -> this[status] = mutableListOf() } }

    operator fun get(status: ApplicationResult.Status): List<ApplicationResult> = processedVacancies[status]!!
    operator fun plusAssign(result: ApplicationResult): Unit =
        Unit.also { processedVacancies[result.applyStatus]!!.add(result) }
}
