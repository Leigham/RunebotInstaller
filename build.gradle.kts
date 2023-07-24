

plugins {
    id("java")
}

group = "org.runebot"
version = "1.0-SNAPSHOT"

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

tasks.test {
    useJUnitPlatform()
}