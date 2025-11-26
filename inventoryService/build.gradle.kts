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
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {

    implementation("io.github.lognet:grpc-spring-boot-starter:5.1.5")

    compileOnly(libs.annotation.api)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // ---- GRPC ----
    implementation(libs.grpc.transport)
    implementation(libs.grpc.model)
    implementation(libs.grpc.stub)

    // ---- SPRING DATA JPA + POSTGRES ----
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.postgresql)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
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

tasks.test {
    useJUnitPlatform()
}