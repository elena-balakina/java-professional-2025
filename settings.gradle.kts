rootProject.name = "java-professional-2025"

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
        id("name.remal.sonarlint") version sonarlint
        id("com.diffplug.spotless") version spotless
    }
}

include("L01-gradle")
include("L04-generics")
include("L06-annotations")
include("L08-gc")
include("L10-byteCodes")
include("L12-solid")
include("L15-structuralPatterns")
include("L15-structuralPatterns:homework")
include("L16-io")
include("L16-io:homework")
include("L18-jdbc")
include("L18-jdbc:demo")
include("L18-jdbc:homework")
include("L21-jpql")
include("L21-jpql:class-demo")
include("L21-jpql:homework-template")
include("L22-cache")
include("L22-jpql-with-cache")
include("L24-webServer")
include("L25-di")
include("L25-di:class-demo")
include("L25-di:homework-template")
include("L28-springDataJdbc")
include("L31-executors")
include ("L32-concurrentCollections:ConcurrentCollections")
include ("L32-concurrentCollections:QueueDemo")
include ("L34-multiprocess:grpc-demo")
include ("L38-webflux-chat:client-service")
include ("L38-webflux-chat:datastore-service")