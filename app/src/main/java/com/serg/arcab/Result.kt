package com.serg.arcab

import java.io.Serializable

class Result<T>
private constructor(
        val status: Status,
        var data: T?,
        val message: String?
): Serializable {

    companion object {

        fun <T> success(data: T?): Result<T> {
            return Result(Status.SUCCESS, data, null)
        }

        fun <T> error(message: String?, data: T? = null): Result<T> {
            return Result(Status.ERROR, data, message)
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(Status.LOADING, data, null)
        }
    }

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    override fun toString(): String {
        return "Result(status=$status, data=$data, message=$message)"
    }
}