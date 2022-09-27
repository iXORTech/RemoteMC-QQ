import org.gradle.process.internal.ExecException
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

val i18n4k_version: String by project
val klaxon_version: String by project
val krontab_version: String by project
val hoplite_version: String by project
val logback_version: String by project
val ktor_version: String by project
val simbot_version: String by project
val simbot_mirai_version: String by project

val versionPropertiesFile = "${projectDir}/project.properties"

fun String.runCommand(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    try {
        project.exec {
            workingDir = currentWorkingDir
            commandLine = this@runCommand.split("\\s".toRegex())
            standardOutput = byteOut
        }
        return String(byteOut.toByteArray()).trim()
    } catch (e: ExecException) {
        return "0000000"
    }
}

fun getRevision(): String {
    return "git rev-parse --short=7 HEAD".runCommand()
}

fun getProperties(file: String, key: String): String {
    val fileInputStream = FileInputStream(file)
    val props = Properties()
    props.load(fileInputStream)
    return props.getProperty(key)
}

fun getVersion(): String {
    return getProperties(versionPropertiesFile, "version")
}

fun getStage(): String {
    return getProperties(versionPropertiesFile, "stage")
}

plugins {
    application
    kotlin("jvm") version "1.7.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("de.comahe.i18n4k") version "0.5.0"
}

group = "tech.ixor"
version = getVersion() + "-" + getStage() + "+" + getRevision()

i18n4k {
    sourceCodeLocales = listOf("en", "zh_CN")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
    }

    val projectProps by registering(WriteProperties::class) {
        outputFile = file("${projectDir}/src/main/resources/version.properties")
        encoding = "UTF-8"
        property("version", getVersion())
        property("stage", getStage())
        property("revision", getRevision())
    }

    var shadowJarVersion = getVersion() + "-" + getStage()
    shadowJar {
        if (getStage() == "dev" || getStage() == "alpha" || getStage() == "beta" || getStage() == "rc") {
            shadowJarVersion = shadowJarVersion + "+" + getRevision()
        }
        destinationDirectory.set(file("${projectDir}/build/distributions"))
        archiveVersion.set(shadowJarVersion)
        archiveClassifier.set("")
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("conf/config.yaml")
        from(projectProps)
    }
}

application {
    mainClass.set("tech.ixor.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // Dependencies
    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.5")
    // i18n
    implementation("de.comahe.i18n4k:i18n4k-core-jvm:$i18n4k_version")
    // JSON Parser
    implementation("com.beust:klaxon:$klaxon_version")
    // Scheduled Jobs
    implementation("dev.inmo:krontab:$krontab_version")
    // Config Loader
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hoplite_version")
    // Logback
    implementation("ch.qos.logback:logback-classic:$logback_version")
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    // Simple Robot
    implementation("love.forte.simbot.boot:simboot-core:$simbot_version")
    implementation("love.forte.simbot.component:simbot-component-mirai-boot:$simbot_mirai_version") // Mirai Component

    // Test Dependencies
    testImplementation(kotlin("test"))
}