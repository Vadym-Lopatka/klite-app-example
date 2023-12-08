import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.20"
}
val mainClassName = "LauncherKt"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    val kliteVersion = "master-SNAPSHOT" // you can put a released tag or commit hash here
    implementation("com.github.codeborne.klite:klite-server:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-openapi:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-slf4j:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-i18n:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-json:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-jackson:$kliteVersion")
    implementation("com.github.codeborne.klite:klite-jdbc:$kliteVersion")
    implementation("org.postgresql:postgresql:42.7.1")

    testImplementation("com.github.codeborne.klite:klite-jdbc-test:$kliteVersion")
    testImplementation("ch.tutteli.atrium:atrium-fluent:1.1.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.1")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(listOf("src"))
        resources.setSrcDirs(listOf("src")).exclude("**/*.kt")
        resources.srcDirs("db")
    }
    test {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(listOf("test"))
        resources.setSrcDirs(listOf("test")).exclude("**/*.kt")
    }
}

tasks.test {
    useJUnitPlatform()
    // enable JUnitAssertionImprover from klite.jdbc-test
    jvmArgs("--enable-preview", "-Djunit.jupiter.extensions.autodetection.enabled=true", "--add-opens=java.base/java.lang=ALL-UNNAMED")
}

tasks.register<Copy>("deps") {
    into("$buildDir/libs/deps")
    from(configurations.runtimeClasspath)
}

tasks.jar {
    dependsOn("deps")
    doFirst {
        manifest {
            attributes(
                "Main-Class" to mainClassName,
                "Class-Path" to File("$buildDir/libs/deps").listFiles()?.joinToString(" ") { "deps/${it.name}"}
            )
        }
    }
}

tasks.register<JavaExec>("run") {
    mainClass.set(mainClassName)
    classpath = sourceSets.main.get().runtimeClasspath
}
