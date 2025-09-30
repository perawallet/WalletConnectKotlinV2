plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.google.ksp)
    id("jacoco-report")
    id("maven-publish")
}

android {
    namespace = "com.walletconnect.sign"
    compileSdk = COMPILE_SDK

    defaultConfig {
        minSdk = MIN_SDK

        aarMetadata {
            minCompileSdk = MIN_SDK
        }

        buildConfigField(type = "String", name = "SDK_VERSION", value = "\"${SIGN_VERSION}\"")
        buildConfigField("String", "PROJECT_ID", "\"${System.getenv("WC_CLOUD_PROJECT_ID") ?: ""}\"")
        buildConfigField("Integer", "TEST_TIMEOUT_SECONDS", "${System.getenv("TEST_TIMEOUT_SECONDS") ?: 10}")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments += mutableMapOf("clearPackageData" to "true")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "${rootDir.path}/gradle/proguard-rules/sdk-rules.pro")
        }
    }

    lint {
        abortOnError = true
        ignoreWarnings = true
        warningsAsErrors = false
    }

    compileOptions {
        sourceCompatibility = jvmVersion
        targetCompatibility = jvmVersion
    }

    kotlinOptions {
        jvmTarget = jvmVersion.toString()
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.time.ExperimentalTime"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"

        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }

        registerManagedDevices()
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
        create("SignDatabase") {
            packageName.set("com.walletconnect.sign")
            schemaOutputDirectory.set(file("src/main/sqldelight/databases"))
            verifyMigrations.set(true)
            verifyDefinitions.set(true)
        }
    }
}

dependencies {
    api(project(":core:android"))

    ksp(libs.moshi.ksp)
    implementation(libs.bundles.sqlDelight)
    implementation(libs.sqlCipher)
    implementation(libs.sqliteFramework)
    implementation(libs.relinker)

    testImplementation(libs.bundles.androidxTest)
    testImplementation(libs.robolectric)
    testImplementation(libs.json)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.bundles.scarlet.test)
    testImplementation(libs.bundles.sqlDelight.test)
    testImplementation(libs.koin.test)

    androidTestUtil(libs.androidx.testOrchestrator)
    androidTestImplementation(libs.bundles.androidxAndroidTest)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("android") {
                from(components["release"])

                groupId = "com.github.perawallet"
                artifactId = "sign"
                version = SIGN_VERSION

                pom {
                    name.set("Sign")
                    description.set("WalletConnect Sign Protocol SDK")
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
