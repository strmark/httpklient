import org.gradle.api.tasks.wrapper.Wrapper.DistributionType

plugins {
    `maven-publish`
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.ajoberstar.grgit") version "5.0.0"
}

group = "com.github.lion7"
version = grgit.describe()

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.glassfish.jaxb:jaxb-runtime:3.0.2")
    compileOnly("jakarta.xml.ws:jakarta.xml.ws-api:3.0.1")
    compileOnly("com.fasterxml.jackson.core:jackson-databind:2.13.2.1")
    compileOnly("io.opentracing:opentracing-api:0.33.0")
    compileOnly("javax.xml.bind:jaxb-api:2.3.1")
    compileOnly("javax.xml.ws:jaxws-api:2.3.1")
    implementation("commons-io:commons-io:2.11.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
}

tasks {
    wrapper {
        gradleVersion = "7.4.1"
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
