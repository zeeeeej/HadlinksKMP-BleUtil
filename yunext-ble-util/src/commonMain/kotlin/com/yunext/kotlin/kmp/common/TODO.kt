package com.yunext.kmp.common

@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.EXPRESSION,
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.LOCAL_VARIABLE
)
@Repeatable
annotation class TODO(
    val msg: String,
)

enum class AppType {
    BLE_4G,
    BLE4GSlave,
    HDVirtual
    ;
}

val appType = AppType.HDVirtual