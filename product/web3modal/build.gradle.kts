plugins {
    id("com.android.library")
    id(libs.plugins.kotlin.android.get().pluginId)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.paparazzi)
    id("jacoco-report")
    id("maven-publish")
}

android {
    namespace = "com.walletconnect.web3.modal"
    compileSdk = COMPILE_SDK

    defaultConfig {
        minSdk = MIN_SDK

        aarMetadata {
            minCompileSdk = MIN_SDK
        }

        buildConfigField(type = "String", name = "SDK_VERSION", value = "\"${WEB_3_MODAL_VERSION}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        File("${rootDir.path}/gradle/consumer-rules").listFiles()?.let { proguardFiles ->
            consumerProguardFiles(*proguardFiles)
        }
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    tasks.withType(Test::class.java) {
        jvmArgs("-XX:+AllowRedefinitionToAddDeleteMethods")
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.bundles.androidxAppCompat)
    implementation(libs.bundles.accompanist)
    implementation(libs.coil)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.compose.lifecycle)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    androidTestImplementation(libs.androidx.compose.ui.test.junit)
    androidTestImplementation(libs.androidx.compose.navigation.testing)

    implementation(libs.androidx.datastore)
    implementation(libs.bundles.androidxLifecycle)
    ksp(libs.moshi.ksp)
    api(libs.bundles.androidxNavigation)
    implementation(libs.qrCodeGenerator)
    implementation(libs.coinbaseWallet)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.bundles.androidxTest)

    androidTestUtil(libs.androidx.testOrchestrator)
    androidTestImplementation(libs.bundles.androidxAndroidTest)

    // Internal module dependencies
    api(project(":core:android"))
    api(project(":protocol:sign"))
    api(project(":core:modal"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("android") {
                from(components["release"])

                groupId = "com.github.perawallet"
                artifactId = "web3modal"
                version = WEB_3_MODAL_VERSION

                pom {
                    name.set("Web3Modal")
                    description.set("WalletConnect Web3Modal SDK for Android with Compose UI")
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
