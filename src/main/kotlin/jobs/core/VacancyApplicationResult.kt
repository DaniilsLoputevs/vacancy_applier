package jobs.core


class VacancyApplicationResult(
    var name: String,
    var link: String,
    var applyStatus: Status = Status.UNKNOWN,
    var pageIndex: Int = -1,
) {
    var applyOrder: Int = -1
    var applyException: Throwable? = null
    var applyExecutionTimeSec: Long = -1

    fun exceptionToString(): String {
        return if (applyException == null) "null"
        else try {
            applyException!!.javaClass.simpleName + " " +
                    applyException.toString().let { it.substring(it.indexOf("->")) }
        } catch (e: Exception) {
            applyException.toString()
        }
    }

    enum class Status {
        APPLIED_NOW,
        EXCEPTION,
        QUESTIONS,
        APPLIED_BEFORE,
        UNKNOWN,
        SKIP
    }

}