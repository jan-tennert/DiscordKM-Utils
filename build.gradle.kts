val repositoryId: String? = System.getenv("SONATYPE_REPOSITORY_ID")
val sonatypeUsername: String? = System.getenv("SONATYPE_USERNAME")
val sonatypePassword: String? = System.getenv("SONATYPE_PASSWORD")

val ktorVersion: String by project
val korlibsVersion: String by project
val mordantVersion: String by project

plugins {
    val kotlinVersion = "1.5.31"

    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version "1.5.31"
}

group = "io.github.jan-tennert.discordkm"
version = "0.1"

repositories {
    mavenCentral()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
    }
}

signing {
    val signingKey = providers
        .environmentVariable("GPG_SIGNING_KEY")
        .forUseAtConfigurationTime()
    val signingPassphrase = providers
        .environmentVariable("GPG_SIGNING_PASSPHRASE")
        .forUseAtConfigurationTime()

    if (signingKey.isPresent && signingPassphrase.isPresent) {
        useInMemoryPgpKeys(signingKey.get(), signingPassphrase.get())
        val extension = extensions
            .getByName("publishing") as PublishingExtension
        sign(extension.publications)
    }
}

//val dokkaOutputDir = "H:/Programming/Other/DiscordKMDocs"
val dokkaOutputDir = "$buildDir/dokka"

tasks.dokkaHtml {
//    outputDirectory.set(file(dokkaOutputDir))
}

val deleteDokkaOutputDir by tasks.register<Delete>("deleteDokkaOutputDirectory") {
    delete(dokkaOutputDir)
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    dependsOn(deleteDokkaOutputDir, tasks.dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaOutputDir)
}

fun org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension.addPublishing() {
    publishing {
        repositories {
            maven {
                name = "Oss"
                setUrl {
                    "https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${System.getenv("SONATYPE_REPOSITORY_ID")}"
                }
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
            maven {
                name = "Snapshot"
                setUrl { "https://s01.oss.sonatype.org/content/repositories/snapshots/" }
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                    password = System.getenv("OSSRH_PASSWORD")
                }
            }
        }

        publications {
            withType<MavenPublication> {
                artifact(javadocJar)
                pom {
                    name.set("DiscordKM")
                    description.set("Provides some useful extensions for DiscordKM like Paginators and other dialogs")
                    url.set("https://github.com/jan-tennert/DiscordKM-Utils")
                    licenses {
                        license {
                            name.set("GPL-3.0")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.en.html")
                        }
                    }
                    issueManagement {
                        system.set("Github")
                        url.set("https://github.com/jan-tennert/DiscordKM-Utils/issues")
                    }
                    scm {
                        connection.set("https://github.com/jan-tennert/DiscordKM-Utils.git")
                        url.set("https://github.com/jan-tennert/DiscordKM-Utils")
                    }
                    developers {
                        developer {
                            name.set("TheRealJanGER")
                            email.set("jan.m.tennert@gmail.com")
                        }
                    }
                }
            }
        }
    }
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(LEGACY) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
        nodejs()
    }
    addPublishing()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.jan-tennert.discordkm:DiscordKM:0.4.2")
            }
        }
        val commonTest by getting
        val jvmMain by getting
        val jvmTest by getting
        val jsMain by getting
        val jsTest by getting
    }
}
