package com.yunext.kotlin.kmp.ble.util.domain.tsl

sealed class TslException(
    message: String?,
    cause: Throwable? = null,
) : Throwable(message, cause)

class TslCmdException(message: String?) : TslException(message, null)
class TslIllegalStateException(message: String?) : TslException(message, null)
