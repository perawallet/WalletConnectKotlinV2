import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.internal.api.DefaultAndroidSourceDirectorySet

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

tasks {
    register("javadocJar", Jar::class) {
        dependsOn(named("dokkaHtml"))
        archiveClassifier.set("javadoc")
        from("${layout.buildDirectory}/dokka/html")
    }

    register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(
            (project.extensions.getByType<BaseExtension>().sourceSets.getByName("main").kotlin.srcDirs("kotlin") as DefaultAndroidSourceDirectorySet).srcDirs,
            (project.extensions.getByType<BaseExtension>().sourceSets.getByName("release").kotlin.srcDirs("kotlin") as DefaultAndroidSourceDirectorySet).srcDirs
        )
    }
}

(project as ExtensionAware).extensions.configure<LibraryExtension>("android") {
    publishing.singleVariant("release")
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                afterEvaluate { from(components["release"]) }
                artifact(tasks.getByName("javadocJar"))
                artifact(tasks.getByName("sourcesJar"))

                groupId = "com.walletconnect"
                artifactId = requireNotNull(project.extra[KEY_PUBLISH_ARTIFACT_ID]).toString()
                version = requireNotNull(project.extra[KEY_PUBLISH_VERSION]).toString()
            }

            if (project.hasProperty("group") && project.property("group") == "com.github.perawallet") {
                register<MavenPublication>("jitpack") {
                    afterEvaluate { from(components["release"]) }
                    artifact(tasks.getByName("javadocJar"))
                    artifact(tasks.getByName("sourcesJar"))

                    groupId = "com.github.perawallet"
                    artifactId = requireNotNull(project.extra[KEY_PUBLISH_ARTIFACT_ID]).toString()
                    version = project.property("version").toString()
                }
            }
        }
    }
}
