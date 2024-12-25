import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
//    id("module.publication")
//    `maven-publish`
    alias(libs.plugins.maven.publish)

    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
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

    sourceSets {
        commonMain.dependencies {
            // put your Multiplatform dependencies here

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)

//            implementation(libs.ktor.client.core)

//            implementation(libs.ktor.client.cio)
//            implementation(libs.ktor.client.core)

//            api(libs.kotlinx.datetime)
//            api(libs.kotlinx.coroutines.core)
//            api(libs.ktor.client.core)
//            api(libs.ktor.client.cio)
//            api(libs.ktor.client.content.negotiation)
//            api(libs.ktor.serialization.kotlinx.json)
//            api(libs.ktor.serialization.kotlinx.protobuf)
//            implementation(libs.ktor.client.logging)
//            implementation(libs.ktor.client.core.wasm)
//            implementation(libs.ktor.client.core)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.ui)
            implementation(compose.components.resources) // SEE https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-images-resources.html
            implementation(compose.components.uiToolingPreview)
//            implementation(projects.yunextCommon)
//            implementation(projects.yunextContext)
            implementation(libs.bundles.yunext)
//            implementation(projects.yunextBle)
            implementation(libs.bundles.navigation)

            api(libs.kable)

        }

        iosMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktor.client.cio)
            api(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktor.client.okhttp)
            api(libs.ktor.client.cio)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.activity)
            implementation(libs.androidx.core.ktx)
            implementation(libs.bundles.androidx.lifecycle)

        }

        jvmMain.dependencies {
            api(libs.ktor.client.core)
            api(libs.ktor.client.okhttp)
            api(libs.ktor.client.cio)
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
