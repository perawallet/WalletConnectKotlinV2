import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id(libs.plugins.javaLibrary.get().pluginId)
    id(libs.plugins.kotlin.jvm.get().pluginId)
    id("maven-publish")
    alias(libs.plugins.google.ksp)
}

java {
    sourceCompatibility = jvmVersion
    targetCompatibility = jvmVersion

    withSourcesJar()
    withJavadocJar()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions {
        jvmTarget = jvmVersion.toString()
    }
}

tasks.withType<Test> {
    systemProperty("SDK_VERSION", FOUNDATION_VERSION)
    systemProperty("TEST_RELAY_URL", System.getenv("TEST_RELAY_URL"))
    systemProperty("TEST_PROJECT_ID", System.getenv("TEST_PROJECT_ID"))
}

dependencies {
    api(libs.bundles.scarlet)
    api(platform(libs.okhttp.bom))
    api(libs.bundles.okhttp)
    implementation(libs.koin.jvm)
    api(libs.bundles.moshi)
    ksp(libs.moshi.ksp)
    api(libs.bouncyCastle)
    api(libs.mulitbase)

    testImplementation(libs.jerseyCommon)
    testImplementation(libs.coroutines.test)
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["java"])

            groupId = "com.github.perawallet"
            artifactId = "foundation"
            version = FOUNDATION_VERSION

            pom {
                name.set("Foundation")
                description.set("WalletConnect Foundation Library")
                url.set("https://github.com/perawallet/WalletConnectKotlinV2")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("walletconnect")
                        name.set("WalletConnect")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/perawallet/WalletConnectKotlinV2.git")
                    developerConnection.set("scm:git:ssh://github.com/perawallet/WalletConnectKotlinV2.git")
                    url.set("https://github.com/perawallet/WalletConnectKotlinV2")
                }
            }
        }
    }
}