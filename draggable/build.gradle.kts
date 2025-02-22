plugins {
    id("maven-publish")
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

fun getLocalGroup() = "com.nphausg"
fun getLocalArtifactId() = "ui-draggable"
fun getLibraryVersion(): String { // Set a default value
    return project.findProperty("version") as String? ?: "0.0.1-alpha"
}

android {
    namespace = "com.nphausg.foundation.ui.draggle"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }
    buildFeatures {
        compose = true
    }
    // Rename AAR file dynamically
    libraryVariants.all {
        if (name == "release") {
            outputs.all {
                val output = outputFile
                val newFileName = "${getLocalArtifactId()}-${getLibraryVersion()}.aar"
                output?.renameTo(File(output.parent, newFileName))
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    // Adjust version as needed
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("renameAar") {
    dependsOn("assembleRelease")
    doLast {
        val outputDir = File("$buildDir/outputs/aar")
        // Change this to match your module name
        val oldFile = File(outputDir, "draggable-release.aar")
        val newFile = File(outputDir, "${getLocalArtifactId()}-${getLibraryVersion()}.aar")
        if (oldFile.exists()) {
            oldFile.renameTo(newFile)
            println("Renamed AAR: ${newFile.name}")
        } else {
            println("AAR file not found: ${oldFile.absolutePath}")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("draggable-publish") {
            groupId = getLocalGroup()
            artifactId = getLocalArtifactId()
            version = getLibraryVersion()
            afterEvaluate {
                val outputDir = File("$buildDir/outputs/aar")
                val renamedFile = File(outputDir, "${getLocalArtifactId()}-${getLibraryVersion()}.aar")
                artifact(renamedFile) {
                    builtBy(tasks.named("renameAar"))
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/nphausg/compose.draggable-unlocker")
            credentials {
                username = System.getenv("GPR_USERNAME") ?: project.findProperty("gpr.user") as String?
                password = System.getenv("GPR_TOKEN") ?: project.findProperty("gpr.token") as String?
            }
        }
    }
}