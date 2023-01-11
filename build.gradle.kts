import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // https://github.com/gradle/gradle/issues/22797
plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
    id("jacoco-report-aggregation")
}

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output.set(rootProject.layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

tasks.dokkaHtmlMultiModule.configure {
    outputDirectory.set(buildDir.resolve("dokkaMultiModuleOutput"))
}

allprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    detekt {
        source = objects.fileCollection().from(
            DetektExtension.DEFAULT_SRC_DIR_JAVA,
            DetektExtension.DEFAULT_TEST_SRC_DIR_JAVA,
            DetektExtension.DEFAULT_SRC_DIR_KOTLIN,
            DetektExtension.DEFAULT_TEST_SRC_DIR_KOTLIN
        )

        parallel = true
        ignoreFailures = true
        buildUponDefaultConfig = true
        baseline = file("$rootDir/config/detekt/baseline.xml")
    }

    dependencies {
        detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.22.0")
    }

    tasks.withType<Detekt> {
        reports {
            xml.required.set(true)
            html.required.set(true)
            sarif.required.set(true)
        }

        basePath = rootProject.projectDir.absolutePath
        jvmTarget = "1.8"
        finalizedBy(detektReportMergeSarif)

        // Merge detekt report into sarif file for CodeQL scanning
        detektReportMergeSarif.configure {
            input.from(this@withType.sarifReportFile)
        }
    }

    tasks.withType<DetektCreateBaselineTask> {
        jvmTarget = "1.8"
    }

    // Experimental: use kotlin's K2 compiler
    tasks.withType<KotlinCompile> {
        kotlinOptions.useK2 = true
    }
}
