import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    `maven-publish`
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    id("org.ajoberstar.grgit") version "4.1.0"
}

group = "com.github.lion7"
version = grgit.describe()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.glassfish.jaxb:jaxb-runtime:2.3.3")
    compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.12.3")
    compileOnly("io.opentracing:opentracing-api:0.33.0")

    implementation("commons-io:commons-io:2.8.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

tasks {
    wrapper {
        gradleVersion = "7.0"
        distributionType = DistributionType.ALL
    }

    test {
        useJUnitPlatform()
    }

    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)

    }
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
            artifact(tasks["javadocJar"])
        }
    }
}
