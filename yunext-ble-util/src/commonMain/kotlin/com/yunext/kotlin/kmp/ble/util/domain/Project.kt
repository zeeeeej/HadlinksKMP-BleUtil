package com.yunext.kotlin.kmp.ble.util.domain

import com.yunext.kotlin.kmp.ble.util.impl.DefaultProject
import com.yunext.kotlin.kmp.ble.util.impl.asDefaultProject
import kotlinx.serialization.json.Json


interface Project {
    val id: String
    val name: String

    //    val scanFilters: List<PlatformMasterScanFilter>
    val secret: String
    val defaultDeviceName: String
}


fun Project?.encode(): String {
    val json = if (this == null) "{}" else Json.encodeToString(
        DefaultProject.serializer(), this.asDefaultProject()
    )
    println("json a = $json")
    @OptIn(ExperimentalStdlibApi::class) val data = json.encodeToByteArray().toHexString()
    return data
}


fun projectOf(data: String): DefaultProject? {
    @OptIn(ExperimentalStdlibApi::class) return try {
        val json = data.hexToByteArray().decodeToString()
        println("json z = $json")
        Json.decodeFromString(json)
    } catch (e: Exception) {
        null
    }
}


interface ProjectRepository {
    suspend fun add(project: Project): Boolean
    suspend fun edit(project: Project): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun all(): List<Project>
    suspend fun find(id: String): Project?
    suspend fun clear(): Boolean
}