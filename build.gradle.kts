@file:Suppress("LocalVariableName")

plugins {
    id("java")
    id("dev.architectury.loom") version "1.2-SNAPSHOT"
    id("architectury-plugin") version "3.4-SNAPSHOT"
    kotlin("jvm") version "1.8.10"
}

group = "me.rufia"
version = "0.4.12-12+arm32x.1"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    @Suppress("UnstableApiUsage")
    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://maven.wispforest.io")
}

dependencies {
    val minecraft_version: String by project
    minecraft("net.minecraft:minecraft:${minecraft_version}")
    val yarn_mappings: String by project
    mappings("net.fabricmc:yarn:${yarn_mappings}:v2")
    val loader_version: String by project
    modImplementation("net.fabricmc:fabric-loader:${loader_version}")

//    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.89.0+1.20.1")
//    modImplementation(fabricApi.module("fabric-command-api-v2", "0.83.0+1.19.4"))
    val cobblemon_version: String by project
    modImplementation("com.cobblemon:fabric:${cobblemon_version}")

    val owo_version: String by project
    annotationProcessor(modImplementation("io.wispforest:owo-lib:${owo_version}")!!)
    include("io.wispforest:owo-sentinel:${owo_version}")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}