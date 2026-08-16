plugins {
    kotlin("jvm") version "1.9.23"
    //kotlin("jvm") version "2.3.10"
    `maven-publish`
    // see https://gitlab.com/Plunts/plantuml-gradle-plugin
    id("io.gitlab.plunts.plantuml") version "2.3.0"
    // see https://kotlinlang.org/docs/dokka-javadoc.html#generate-javadoc-documentation
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    // As suggested by https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
    // see https://vanniktech.github.io/gradle-maven-publish-plugin/
    id("com.vanniktech.maven.publish") version "0.31.0"
}

// CAUTION Until 2.0.1 (GitHub packages), the "group" was set to "ch.admin.eid".
//         For the sake of Maven Central publishing, it must now match the Maven Central namespace
group = "ch.admin.swiyu"
version = "2.8.2"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}
dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
    implementation("net.java.dev.jna:jna:5.17.0")
    implementation("com.google.code.gson:gson:2.10.1")
}

kotlin {
    jvmToolchain(21)
}

// To generate documentation in Javadoc, just run: ./gradlew :dokkaJavadocJar
val dokkaJavadocJar by tasks.registering(Jar::class) {
    //description = "A Javadoc JAR containing Dokka Javadoc"
    from(tasks.dokkaGeneratePublicationJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

// Just run: ./gradlew generateClassDiagrams
classDiagrams {
    diagram {
        // this server is the default, so skip this if you don't mind your diagrams being sent there
        //plantumlServer = null // optional, avoids surprises on configuration errors
        //renderClasspath = null

        //name("Visualise didresolver Package")

        // All properties are also available (and most likely more suited) in the defaults
        // For more information see official PlantUML documentation: https://plantuml.com/en/class-diagram
        style {
            //include("default_style.puml")
            //useIntermediatePackages()
            // see https://the-lum.github.io/puml-themes-gallery/themes/index.html
            //theme("sketchy")
            hide("empty members")
            show("stereotype")
        }

        //include(packages().withName("ch.admin.eid.didresolver"))
        include(classes().withNameLike("ch.admin.eid.did_sidekicks.DidDoc*").insideOfProject())
        include(classes().withNameLike("ch.admin.eid.did_sidekicks.DidMethod*").insideOfProject())
        include(classes().withNameLike("ch.admin.eid.did_sidekicks.Jwk*").insideOfProject())
        include(classes().withNameLike("ch.admin.eid.did_sidekicks.VerificationMethod*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.did_sidekicks.*Uniffi*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.did_sidekicks.*Interface*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.did_sidekicks.*Companion*").insideOfProject())
        include(classes().withNameLike("ch.admin.eid.didresolver.Did*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.didresolver.DidResolveException$[A-Z]*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.didresolver.*Kt").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.didresolver.*Uniffi*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.didresolver.*Interface*").insideOfProject())
        exclude(classes().withNameLike("ch.admin.eid.didresolver.*Companion*").insideOfProject())
        //include(methods().thatArePublic())
        //exclude(methods().withNameLike("*uniffi*"))
        exclude(interfaces())
        exclude(fields())
        writeTo(file("diagrams/didresolver.puml"))
        renderTo(file("diagrams/didresolver.svg"))
        //renderTo(file("diagrams/didresolver.png"))
    }
}

/*
publishing {
    publications {
        register<MavenPublication>("gpr") {
            groupId = "ch.admin.eid"
            artifactId = "didresolver"
            version = "2.6.0"
            from(components["kotlin"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/swiyu-admin-ch/didresolver-kotlin")
            credentials {
                // For the GitHub packages, create a personal access token having 'write:packages' scope
                // and then store it into gradle.properties:
                // gpr.user=<your_GitHub_user>
                // gpr.token=****************************************
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
            }
        }
        maven {
            name = "BitNexusMavenHostedRepo"
            url = uri("https://nexus.bit.admin.ch/repository/bj-swiyu-maven-hosted")
            credentials {
                // For the BIT Nexus Maven repo, login onto https://nexus.bit.admin.ch with your u808***** account.
                // Ensure your U-account already have writing grants for the repo (bj-swiyu-maven-hosted).
                // Create an access user token (visit https://nexus.bit.admin.ch/#user/usertoken)
                // and then store it into gradle.properties:
                // nexus.user=********
                // nexus.token=********************************************
                username = project.findProperty("nexus.user") as String? ?: System.getenv("NEXUS_USERNAME")
                password = project.findProperty("nexus.token") as String? ?: System.getenv("NEXUS_TOKEN")
            }
        }
    }
}
 */

// As suggested by https://www.jetbrains.com/help/kotlin-multiplatform-dev/multiplatform-publish-libraries.html
mavenPublishing {
    // when publishing to https://central.sonatype.com/
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    // PQEID: disabled for local mavenLocal() install (no GPG signatory configured here) -
    // this is a purely local build, not intended to be pushed/released.
    // signAllPublications()

    coordinates(group.toString(), "didresolver", version.toString())

    pom {
        name = "DID Resolver"
        description = "Language bindings for the swiyu DID resolver library in Kotlin"
        url = "https://github.com/swiyu-admin-ch/didresolver-kotlin"
        licenses {
            license {
                name = "MIT License"
                url = "http://www.opensource.org/licenses/mit"
            }
        }
        developers {
            developer {
                id = "vst-bit"
                name = "vst-bit (Swiyu Omni Developer)"
                organization = "Swiyu"
                organizationUrl = "https://github.com/swiyu-admin-ch"
            }
        }
        scm {
            url = "https://github.com/swiyu-admin-ch/didresolver-kotlin/tree/main"
            connection = "scm:git:git://github.com/swiyu-admin-ch/didresolver-kotlin.git"
            developerConnection = "scm:git:ssh://github.com:swiyu-admin-ch/didresolver-kotlin.git"
        }
    }
}
