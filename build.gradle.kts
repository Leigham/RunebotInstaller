plugins {
    id("java")
}

val versionArr = intArrayOf(0,13,13)
version = 'v' + versionArr.joinToString(".")
group = "org.runebot"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.runebot.Main"
    }
    from(sourceSets.main.get().output)
}
// Get the manifest URL of the current JAR file

tasks.register<Jar>("debug") {
    manifest {
        attributes["Main-Class"] = "org.runebot.Main"
        attributes["debug"] = "true"
    }
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveClassifier.set("debug")
}

tasks.register<Jar>("release") {
    manifest {
        attributes["Main-Class"] = "org.runebot.Main"
        attributes["debug"] = "false"
    }
    from(sourceSets.main.get().output)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    archiveClassifier.set("release")
}

tasks.register("buildJars") {
    dependsOn("clean", "debug", "release", "version")
}
tasks.register("version") {
// return the version, so can be used in github actions
    doLast {
        println(version)
    }
}

tasks.test {
    useJUnitPlatform()
}
