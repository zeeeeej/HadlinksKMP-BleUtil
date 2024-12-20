package com.yunext.kotlin.kmp.ble.util.domain


interface Project {
    val id: String
    val name: String
//    val scanFilters: List<PlatformMasterScanFilter>
    val secret: String
}


interface ProjectRepository {
    suspend fun add(project: Project): Boolean
    suspend fun edit(project: Project): Boolean
    suspend fun delete(id: String): Boolean
    suspend fun all(): List<Project>
    suspend fun find(id: String): Project?
    suspend fun clear(): Boolean
}