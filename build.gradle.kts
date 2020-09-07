plugins {
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
}