//task 5
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
//task 7
import org.gradle.api.Project
import org.gradle.api.provider.Property


plugins {
    id("java")
    application //task 2
    id("com.gradleup.shadow") version "9.4.1" //task4
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Source: https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
    implementation("org.apache.commons:commons-lang3:3.20.0")
    // Source: https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.32")
    // Source: https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:2.0.17")

    implementation(project(":string-utils"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")


}

application {
    mainClass.set("org.example.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar { //task4
    manifest {
        attributes["Main-Class"] = "org.example.Main"
    }
}

abstract class PrintInfoTask : DefaultTask() { //task 5

    @TaskAction
    fun print() {
        println("======================================")
        println("This is my first user task!")
        println("Project: ${project.name}")
        println("Gradle version: ${project.gradle.gradleVersion}")
        println("======================================")
    }
}

tasks.register<PrintInfoTask>("printInfo") {
    group = "Custom"
    description = "Displays information about the project"
}


//task 5
//abstract class GenerateBuildPassportTask : DefaultTask() {
//
//    @TaskAction
//    fun generate() {
//        val userName = System.getenv("USERNAME")
//            ?: System.getenv("USER")
//            ?: "unknown"
//
//        val osName = System.getProperty("os.name")
//        val javaVersion = System.getProperty("java.version")
//        val buildTime = LocalDateTime.now()
//            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
//
//        val resourcesDir = project.file("src/main/resources")
//        if (!resourcesDir.exists()) {
//            resourcesDir.mkdirs()
//        }
//
//        val passportFile = project.file("src/main/resources/build-passport.properties")
//
//        val props = Properties()
//        props.setProperty("build.user", userName)
//        props.setProperty("build.os", osName)
//        props.setProperty("build.java", javaVersion)
//        props.setProperty("build.time", buildTime)
//        props.setProperty("build.message", "The build was completed successfully")
//
//        FileOutputStream(passportFile).use { out ->
//            props.store(out, "Build Passport")
//        }
//
//        println("Файл создан: ${passportFile.absolutePath}")
//    }
//}
//
//tasks.register<GenerateBuildPassportTask>("generateBuildPassport") {
//    group = "Custom"
//    description = "Creates build-passport.properties"
//}

abstract class GenerateBuildPassportTask : DefaultTask() {

    @get:Input
    abstract val gitCommitHash: Property<String>

    @TaskAction
    fun generate() {
        val userName = System.getenv("USERNAME")
            ?: System.getenv("USER")
            ?: "unknown"

        val osName = System.getProperty("os.name")
        val javaVersion = System.getProperty("java.version")
        val buildTime = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

        val counterFile = project.file("build-number.txt")
        val lastNumber = if (counterFile.exists()) {
            counterFile.readText().trim().toIntOrNull() ?: 0
        } else {
            0
        }
        val newNumber = lastNumber + 1
        counterFile.writeText(newNumber.toString())

        val resourcesDir = project.file("src/main/resources")
        if (!resourcesDir.exists()) {
            resourcesDir.mkdirs()
        }

        val passportFile = project.file("src/main/resources/build-passport.properties")

        val props = Properties()
        props.setProperty("build.user", userName)
        props.setProperty("build.os", osName)
        props.setProperty("build.java", javaVersion)
        props.setProperty("build.time", buildTime)
        props.setProperty("build.message", "The build was completed successfully")
        props.setProperty("build.number", newNumber.toString())
        props.setProperty("build.gitHash", gitCommitHash.get())

        FileOutputStream(passportFile).use { out ->
            props.store(out, "Build Passport")
        }

        println("Build passport created: ${passportFile.absolutePath}")
    }
}

tasks.register<GenerateBuildPassportTask>("generateBuildPassport") {
    group = "Custom"
    description = "Creates build-passport.properties"
    gitCommitHash.set(getGitCommitHash(project))
}


tasks.named("processResources") {
    dependsOn("generateBuildPassport")
}

//task 7
fun getGitCommitHash(project: Project): String {
    return try {
        project.providers.exec { //project.exec deleted from the new version
            commandLine("git", "rev-parse", "--short", "HEAD")
        }.standardOutput.asText.get().trim().ifEmpty { "unknown" }
    } catch (e: Exception) {
        "unknown"
    }
}