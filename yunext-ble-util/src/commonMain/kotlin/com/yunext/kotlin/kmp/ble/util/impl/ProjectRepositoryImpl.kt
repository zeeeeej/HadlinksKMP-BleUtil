package com.yunext.kotlin.kmp.ble.util.impl

import com.yunext.kotlin.kmp.ble.master.DeviceNamePlatformMasterScanFilter
import com.yunext.kotlin.kmp.ble.master.PlatformMasterScanFilter
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.domain.ProjectRepository
import com.yunext.kotlin.kmp.common.util.currentTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class DefaultProject(
    override val id: String,
    override val name: String,
//    override val scanFilters: List<PlatformMasterScanFilter>,
    override val secret: String,
    val createTime: Long = currentTime(),
    val editTime: Long = currentTime(), override val defaultDeviceName: String
) : Project

internal fun Project.asDefaultProject() = DefaultProject(
    id = this.id,
    name = this.name, /*scanFilters = this.scanFilters,*/
    secret = this.secret,
    defaultDeviceName = this.defaultDeviceName
)

class ProjectRepositoryImpl : ProjectRepository {

    companion object {
        @OptIn(ExperimentalStdlibApi::class)
        internal val p1 = DefaultProject(
            "angel01",
            "安吉尔测试1",
//            listOf(DeviceNamePlatformMasterScanFilter("_angel")),
            "123456",
            createTime = currentTime(),
            editTime = currentTime(),
            defaultDeviceName = "angel_${
                Random.Default.nextBytes(4).toHexString()}"
        )
        internal val p2 = DefaultProject(
            "qinyuan01",
            "qinyuan测试1",
//            listOf(DeviceNamePlatformMasterScanFilter("B#QY#")),
            "123456", createTime = currentTime(), editTime = currentTime(), "B#QY#URQ6690#686B60"
        )
        @OptIn(ExperimentalStdlibApi::class)
        internal val p3 = DefaultProject(
            "water",
            "饮水机",
//            listOf(DeviceNamePlatformMasterScanFilter("_angel")),
            "123456",
            createTime = currentTime(),
            editTime = currentTime(),
            defaultDeviceName = "water_${
                Random.Default.nextBytes(4).toHexString()}"
        )
    }

    private val map: MutableMap<String, DefaultProject> = mutableMapOf()


    init {
        map[p1.id] = p1
        map[p2.id] = p2
        map[p3.id] = p3
    }

    override suspend fun add(project: Project): Boolean {
        return withContext(Dispatchers.Default) {
            val id = project.id
            val name = project.name
            check(!map.containsKey(id)) {
                "id：$id 已存在"
            }
            check(!map.any { (k, v) -> v.name == name }) {
                "name：$name 已存在"
            }
            map[id] = project.asDefaultProject()
            return@withContext true
        }

    }

    override suspend fun edit(project: Project): Boolean {
        return withContext(Dispatchers.Default) {
            val id = project.id
            val old = map[id] ?: return@withContext false
            map[id] = project.asDefaultProject().copy(editTime = currentTime())
            return@withContext true
        }
    }

    override suspend fun delete(id: String): Boolean {
        return withContext(Dispatchers.Default) {
            if (!map.containsKey(id)) {
                return@withContext true
            }
            map.remove(id)
            return@withContext true
        }
    }

    override suspend fun all(): List<Project> {
        return withContext(Dispatchers.Default) {
            val list = map.map { (k, v) ->
                v
            }
            return@withContext list.sortedWith { p1, p2 ->
                if (p1.createTime >= p2.createTime) {
                    if (p1.editTime >= p2.editTime) {
                        -1
                    } else 1
                } else {
                    if (p1.editTime >= p2.editTime) {
                        -1
                    } else 1
                }
            }
        }
    }

    override suspend fun find(id: String): Project? {
        return withContext(Dispatchers.Default) {
            check(!map.containsKey(id)) {
                "id${id} 不存在"
            }
            return@withContext map[id]
        }
    }

    override suspend fun clear(): Boolean {
        return withContext(Dispatchers.Default) {
            map.clear()
            return@withContext true
        }
    }
}