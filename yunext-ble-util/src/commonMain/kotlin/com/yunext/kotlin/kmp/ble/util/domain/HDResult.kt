package com.yunext.kotlin.kmp.ble.util.domain

sealed interface HDResult<out T> {
    data class Success<T>(val data: T) : HDResult<T>
    data class Fail(val throwable: Throwable) : HDResult<Nothing>
}

fun <R> runCatchingHD(block: () -> R): HDResult<R> {
    return try {
        HDResult.Success(block())
    } catch (e: Throwable) {
        HDResult.Fail(e)
    }
}