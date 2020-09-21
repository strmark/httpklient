plugins {
    `maven-publish`
    kotlin("jvm") version "1.4.0"
    id("org.jetbrains.dokka") version "1.4.0"
}

group = "com.github.lion7"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    compileOnly("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.11.2")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    test {
        useJUnitPlatform()
    }
}
