plugins {
    `java-platform`
    `maven-publish`
}

dependencies {
    constraints {
        api(project(":foundation"))
        api(project(":core:android"))
        api(project(":core:modal"))
        api(project(":protocol:sign"))
        api(project(":protocol:auth"))
        api(project(":protocol:notify"))
        api(project(":product:walletconnectmodal"))
        api(project(":product:web3modal"))
        api(project(":product:web3wallet"))
    }
}

publishing {
    publications {
        create<MavenPublication>("release") {
            from(components["javaPlatform"])

            groupId = "com.github.perawallet"
            artifactId = "android-bom"
            version = BOM_VERSION

            pom {
                name.set("Android BOM")
                description.set("WalletConnect Android Bill of Materials")
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
