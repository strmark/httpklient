plugins {
    kotlin("jvm") version "1.4.0"
}

group = "com.github.lion7"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2") {
        exclude("org.jetbrains.kotlin")
    }
}