import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.shadow)
    alias(libs.plugins.dokka)
}

tasks {
    named<DokkaMultiModuleTask>("dokkaHtmlMultiModule") {
        outputDirectory.set(buildDir.resolve("dokkaMultiModuleOutput"))
    }

    withType<Jar> {
        dependsOn(named<ShadowJar>("shadowJar"))
    }
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

allprojects {
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.0")
    }

    detekt {
        parallel = true
        ignoreFailures = true
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    tasks.withType<DetektCreateBaselineTask>().configureEach {
        jvmTarget = "1.8"
    }
}

subprojects {
    tasks.withType<Detekt> {
        basePath = rootProject.projectDir.absolutePath
        jvmTarget = "1.8"

        reports {
            xml.required.set(true)
            html.required.set(true)
            sarif.required.set(true)
        }

        finalizedBy(detektReportMergeSarif)
    }

    // Merge detekt report into sarif file for CodeQL scanning
    detektReportMergeSarif.configure {
        input.from(tasks.withType<Detekt>().map { it.sarifReportFile })
    }
}
