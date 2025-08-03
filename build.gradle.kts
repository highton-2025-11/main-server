val ktor_version = "3.2.3"

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.example"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.17.0")
    implementation("org.jetbrains.exposed:exposed-core:0.46.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.46.0")
    implementation("org.jetbrains.exposed:exposed-jodatime:0.46.0")

    implementation("io.ktor:ktor-client-core:2.3.4")

    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("io.ktor:ktor-client-cio:2.3.4")
    implementation(libs.ktor.serialization.jackson)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation("com.mysql:mysql-connector-j:8.0.33")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("io.ktor:ktor-client-cio-jvm:3.2.3")
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
