package conventions

plugins {
    id("org.jetbrains.dokka")
    id("com.github.johnrengelman.shadow")
    `java-library`
}

java {
    withSourcesJar()
    withJavadocJar()
}

dependencies {
    runtimeOnly(kotlin("stdlib"))
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }

    build {
        finalizedBy(shadowJar)
    }
}