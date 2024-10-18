import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import java.io.FileInputStream
import java.io.FileOutputStream

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
}


kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.bundles.ktor.server)

        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources) // SEE https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
            implementation(compose.components.uiToolingPreview)
            implementation(projects.shared)
            implementation(projects.yunextBleUtil)
            //implementation(project(":yunext-context"))
            //implementation(libs.yunext.context)
            implementation(libs.bundles.yunext)
            //implementation(projects.yunextCommon)
//            implementation(projects.yunextBle)


        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.swing)

        }

        iosMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)

        }
    }
}

android {
    namespace = "com.yunext.kotlin.kmp.ble.util"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "om.yunext.kotlin.kmp.ble.util"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        // fix 12 files found with path 'META-INF/INDEX.LIST'. Adding a packagingOptions block may help, please refer to
        packagingOptions {
            pickFirst("META-INF/INDEX.LIST")
            pickFirst("META-INF/io.netty.versions.properties")
        }
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
    dependencies {
        debugImplementation(compose.uiTooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.yunext.kotlin.kmp.sample.lib"
            packageVersion = "1.0.0"
        }
    }
}

//compose.resources{
//    publicResClass = true
//    packageOfResClass = "om.yunext.kotlin.kmp.lib.sample"
//    generateResClass = always
//}


project.beforeEvaluate {
    println("-x>>>>>>> ${project.name} beforeEvaluate-------")
}

//val finalTaskName = "finalTask"
//// 定义一个自定义任务
//tasks.register("finalTask") {
//    doLast {
//        println("======> finalTask 所有任务执行完毕后的自定义任务。")
//        // 在这里添加你希望在所有任务执行完毕后执行的代码
//        FileInputStream("release/composeApp-release.apk").use { input ->
//            FileOutputStream("src/androidMain/assets/composeApp-release.apk").use { out ->
//                out.write(input.readBytes())
//            }
//        }
//    }
//}
//
//project.afterEvaluate {
//    println("======> ${project.name} afterEvaluate-------")
//    // 创建一个任务，该任务依赖于所有已知的任务
//    tasks.register("allTasksComplete") {
//        // 依赖于所有已知的任务（除了finalTask）
//        dependsOn(tasks.names.filter { it != finalTaskName  && it!="allTasksComplete"})
//        doLast {
//            println("======>allTasksComplete 所有任务都已完成。现在执行${finalTaskName}。")
//        }
//    }
//
//    // 将finalTask设置为依赖于allTasksComplete任务
//    tasks.named(finalTaskName).configure {
//        dependsOn("allTasksComplete")
//    }
//}




///
val p = TestPlugin()

class TestPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("自定义插件 ¥")
        project.task("myTask") {
            doFirst {
                println("自定义task doFirst")
            }
            doLast {

            }
        }
    }
}


