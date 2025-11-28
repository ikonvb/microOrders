plugins {
    alias { libs.plugins.java.plugin }
    alias { libs.plugins.spring.boot.plugin }
    alias { libs.plugins.spring.dependency.plugin }
}

group = "org.bkv.orders"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.jackson.databind)
    compileOnly(libs.annotation.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // ---- SPRING BOOT CORE ----
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.validation)

    // ---- SPRING WEB (REST API) ----
    implementation(libs.spring.boot.starter.web)

    // ---- SPRING KAFKA ----
    implementation(libs.spring.boot.starter.kafka)

    // ---- SPRING DATA JPA + POSTGRES ----
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
}