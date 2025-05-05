import com.google.protobuf.gradle.id

plugins {
    java
    kotlin("jvm") version "2.0.0"
    id("com.google.protobuf") version "0.9.5"
}

group = "moe.best.kimoneri"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.1"
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                id("kotlin")
            }
        }
    }
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("com.google.guava:guava:33.4.6-jre")
    implementation("com.google.protobuf:protobuf-java:4.30.1")
    implementation("com.google.protobuf:protobuf-kotlin:4.30.1")
    implementation("net.dv8tion:JDA:5.3.2") {
        exclude(module = "opus-java")
    }
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
    testImplementation(kotlin("test"))
}

// Build a "Fat" JAR for direct deployment into a JRE container.
tasks.jar {
    manifest {
        attributes["Main-Class"] = "moe.best.kimoneri.MainKt"
    }
    from(configurations.runtimeClasspath.get().map { zipTree(it) })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}