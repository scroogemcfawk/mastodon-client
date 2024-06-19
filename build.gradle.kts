repositories {
    mavenCentral {
        content {
            excludeGroup("org.luwrain.*")
            excludeGroup("social.bigbone.*")
        }
    }
    jcenter()
    maven {
        url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://download.luwrain.org/maven2/")
    }
}

plugins {
    kotlin("jvm") version "1.9.24"
    java
    application
}

dependencies {
    implementation("social.bigbone:bigbone:2.0.0-20231228.221438-27")
    implementation("org.luwrain:luwrain:2.0.0pre1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test:2.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
}

tasks.compileJava {
    options.release = 17
}

kotlin {
    jvmToolchain(17)
}

//tasks.jar {
//    archiveBaseName = "luwrain-app-mastodon"
//    manifest {
//        attributes(
//            "Manifest-Version" to "1.0",
//            "Build-Jdk-Spec" to 17,
//            "Name" to "ord/luwrain",
//            "Extensions" to "org.luwrain.app.mastodon.Extension"
//        )
//    }
//}
