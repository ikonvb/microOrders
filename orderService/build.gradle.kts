import com.google.protobuf.gradle.id

plugins {
    alias { libs.plugins.java.plugin }
    alias { libs.plugins.protobuf.plugin }
    alias { libs.plugins.spring.boot.plugin }
    alias { libs.plugins.spring.dependency.plugin }
}

group = "org.bkv.orders"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/release") }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}


dependencies {

    implementation(libs.jackson.databind)
    implementation(libs.jwt.api)
    runtimeOnly(libs.jwt.impl)
    runtimeOnly(libs.jwt.jackson)

    compileOnly(libs.annotation.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // ---- GRPC ----
    implementation(libs.grpc.transport)
    implementation(libs.grpc.model)
    implementation(libs.grpc.stub)


    // ---- SPRING BOOT CORE ----
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.validation)

    // ---- SPRING WEB (REST API) ----
    implementation(libs.spring.boot.starter.web)

    // ---- gRPC CLIENT ----
    implementation(libs.spring.boot.grpc.client)

    // ---- SPRING KAFKA ----
    implementation(libs.spring.boot.starter.kafka)

    // ---- SPRING SECURITY ----
    implementation(libs.spring.boot.starter.security)

    // ---- SPRING DATA JPA + POSTGRES ----
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)

    // ---- TESTS ----
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.spring.security.test)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.test.webmvc)
    testImplementation(platform(libs.junit.bom))
}


java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
protobuf {
    protoc { artifact = "com.google.protobuf:protoc:3.25.3" }
    plugins {
        id("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:1.63.0" }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins { id("grpc") }
        }
    }
}


sourceSets {
    val main by getting {
        java {
            // Добавляем папки сгенерированных proto-классов
            setSrcDirs(
                srcDirs + listOf(
                    file("build/generated/source/proto/main/java"),
                    file("build/generated/source/proto/main/grpc")
                )
            )
        }
    }
}

tasks.test {
    useJUnitPlatform()
}