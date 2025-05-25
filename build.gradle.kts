plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "org.example"

version = "1.0-SNAPSHOT"

application { mainClass.set("org.example.MainKt") }

tasks.named<JavaExec>("run") { standardInput = System.`in` }

repositories { mavenCentral() }

dependencies {
    implementation("io.klogging:klogging-jvm:0.9.4")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    testImplementation("io.kotest:kotest-assertions-core:6.0.0.M4")
    testImplementation(kotlin("test"))
}

tasks.test {
    testLogging.showStandardStreams = true
    testLogging.showExceptions = true
    testLogging.exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    useJUnitPlatform()
}

kotlin { jvmToolchain(23) }
