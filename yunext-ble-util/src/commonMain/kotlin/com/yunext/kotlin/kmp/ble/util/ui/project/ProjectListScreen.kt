package com.yunext.kotlin.kmp.ble.util.ui.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import color
import com.yunext.kotlin.kmp.ble.util.domain.Project
import com.yunext.kotlin.kmp.ble.util.ui.Screen
import com.yunext.kotlin.kmp.ble.util.ui.common.HDCommonPageWithTitle
import com.yunext.kotlin.kmp.ble.util.util.DEFAULT_DP
import randomZhongGuoSe

internal fun NavHostController.navigatorProjectListScreen() {
    this.navigate(route = Screen.ProjectList.name , builder = {
        // 启用动画、清除之前的路由等
//        popUpTo(this@navigatorProjectListScreen.graph.startDestinationId) {
//            this.inclusive = false
//        }
        launchSingleTop = true
    })
}


@Composable
internal fun ProjectListScreen(
    modifier: Modifier = Modifier,
    controller: NavHostController,
    onBack: () -> Unit,
    project: Project? = null,
    projectList: List<Project> = emptyList(),
    onSelected: (Project) -> Unit,
    onEdit: (Project) -> Unit,

    ) {

    HDCommonPageWithTitle(modifier = modifier, title = "所有的项目", onBack = onBack) {
        Column(Modifier.fillMaxSize()) {
            Text("所有的项目", modifier.padding(horizontal = DEFAULT_DP))
            ProjectList(
                Modifier.fillMaxWidth(),
                projectList = projectList,
                project = project,
                onAdd = {
                    controller.navigatorProjectAddScreen(null)
                }, onSelected = onSelected, onEdit = onEdit
            )
        }
    }
}

@Composable
private fun ProjectList(
    modifier: Modifier, project: Project? = null,
    projectList: List<Project> = emptyList(),
    onAdd: () -> Unit,
    onSelected: (Project) -> Unit,
    onEdit: (Project) -> Unit,
) {
    LazyColumn(modifier) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .padding(DEFAULT_DP)
                    .shadow(4.dp, RoundedCornerShape(DEFAULT_DP))
                    .clip(RoundedCornerShape(DEFAULT_DP))
                    .background(randomZhongGuoSe().color.copy(alpha = 1f))
                    .padding(DEFAULT_DP)
                    .clickable {
                        onAdd()
                    }, contentAlignment = Alignment.Center
            ) {
                Image(Icons.Default.AddCircle, null, modifier = Modifier.size(72.dp))
            }
        }
        items(projectList, { it.id }) {
            ProjectItem(
                project = it,
                selected = { project?.id == it.id },
                onSelected = {
                    onSelected(it)
                }, onEdit = {
                    onEdit(it)
                })
        }
    }

}

@Composable
private fun ProjectItem(
    project: Project,
    selected: () -> Boolean,
    onSelected: () -> Unit,
    onEdit: () -> Unit
) {
    Box(Modifier
        .fillMaxWidth().aspectRatio(16 / 9f)

        .composed {
            if (selected()) this
                .padding(DEFAULT_DP)
                .shadow(4.dp, RoundedCornerShape(DEFAULT_DP))
                .border(width = 4.dp, brush = Brush.horizontalGradient((1..20).map {
                    randomZhongGuoSe().color
                }), shape = RoundedCornerShape(DEFAULT_DP))
                .clip(RoundedCornerShape(DEFAULT_DP))
                .background(randomZhongGuoSe().color.copy(alpha = 1f))
                .clickable { onSelected() }
                .padding(DEFAULT_DP)
            else this
                .padding(DEFAULT_DP)
                .shadow(4.dp, RoundedCornerShape(DEFAULT_DP))
                .clip(RoundedCornerShape(DEFAULT_DP))
                .background(randomZhongGuoSe().color.copy(alpha = 1f))
                .clickable { onSelected() }
                .padding(DEFAULT_DP)
        }) {
        Column(Modifier.fillMaxSize()) {
            Text(project.id)
            Text(project.name)
            Text(project.secret)
        }

        Image(Icons.Default.Edit, null, modifier = Modifier.align(Alignment.TopEnd).clickable {
            onEdit()
        })


    }


}