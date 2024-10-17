import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
//    id("module.publication")
//    `maven-publish`
    alias(libs.plugins.maven.publish)
}

val appVersion = project.findProperty("app_version")?.toString()?:throw GradleException("no app_version in gradle.properties")


group = "io.github.zeeeeej"
version = appVersion


kotlin {
    targetHierarchy.default()
    jvm()
    androidTarget {
        publishLibraryVariants("release")
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
//    linuxX64()

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
                implementation(libs.kotlinx.datetime)
                implementation(libs.yunext.context)
                implementation(libs.kotlin.reflect)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = "org.jetbrains.kotlinx.multiplatform.library.template"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
//dependencies {
//    implementation(libs.androidx.ui.desktop) // ? 做什么的
//}


mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)
    signAllPublications()

    coordinates("io.github.zeeeeej", "yunext-ble-util", appVersion)

    pom {
        name.set("HadlinksKMP")
        description.set("HadlinksKMP")
        inceptionYear.set("2024")
        url.set("https://github.com/zeeeeej/HadlinksKMP")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("zeeeeej.xpl")
                name.set("zeeeeej")
                url.set("https://github.com/zeeeeej/HadlinksKMP")
            }
        }

        scm {
            url.set("https://github.com/zeeeeej/HadlinksKMP")
            connection.set("scm:git:git://gitlab.com/zeeeeej/HadlinksKMP.git")
            developerConnection.set("scm:git:ssh://git@gitlab.com:zeeeeej/HadlinksKMP.git")
        }
    }
}
