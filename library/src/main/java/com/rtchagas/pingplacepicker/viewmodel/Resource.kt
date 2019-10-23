package com.rtchagas.pingplacepicker.viewmodel

/**
 * Resource holder provided to the UI
 */
class Resource<T> private constructor(val status: Status, val data: T?, val error: Throwable?) {

    /**
     * Possible status types of a response provided to the UI
     */
    enum class Status {
        LOADING,
        SUCCESS,
        ERROR,
        NO_DATA
    }

    companion object {

        fun <T> loading(): Resource<T> {
            return Resource(Status.LOADING, null, null)
        }

        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable?): Resource<T> {
            return Resource(Status.ERROR, null, error)
        }

        fun <T> noData(): Resource<T> {
            return Resource(Status.NO_DATA, null, null)
        }
    }
}
