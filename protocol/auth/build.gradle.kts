plugins {
    id("com.android.library")
    id(libs.plugins.kotlin.android.get().pluginId)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.google.ksp)
    id("jacoco-report")
    id("maven-publish")
}

android {
    namespace = "com.walletconnect.auth"
    compileSdk = COMPILE_SDK

    defaultConfig {
        minSdk = MIN_SDK

        buildConfigField(type = "String", name = "SDK_VERSION", value = "\"${AUTH_VERSION}\"")
        buildConfigField("String", "PROJECT_ID", "\"${System.getenv("WC_CLOUD_PROJECT_ID") ?: ""}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "${rootDir.path}/gradle/proguard-rules/sdk-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    lint {
        abortOnError = true
        ignoreWarnings = true
        warningsAsErrors = false
    }

    kotlinOptions {
        jvmTarget = jvmVersion.toString()
    }

    buildFeatures {
        buildConfig = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

sqldelight {
    databases {
        create("AuthDatabase") {
            packageName.set("com.walletconnect.auth")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            generateAsync.set(true)
            verifyMigrations.set(true)
            verifyDefinitions.set(true)
        }
    }
}

dependencies {
    api(project(":core:android"))

    ksp(libs.moshi.ksp)
    implementation(libs.bundles.sqlDelight)
    api(libs.web3jCrypto)

    testImplementation(libs.bundles.androidxTest)
    testImplementation(libs.robolectric)
    testImplementation(libs.json)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.bundles.scarlet.test)
    testImplementation(libs.bundles.sqlDelight.test)

    androidTestUtil(libs.androidx.testOrchestrator)
    androidTestImplementation(libs.bundles.androidxAndroidTest)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.perawallet"
                artifactId = "auth"
                version = AUTH_VERSION

                pom {
                    name.set("Auth")
                    description.set("WalletConnect Auth Protocol SDK")
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
}
