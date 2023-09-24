plugins {
    id("java")
    id("dev.architectury.loom") version("1.2-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version ("1.8.10")
}

group = "me.rufia"
version = "0.4.12-12+arm32x.1"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.21")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:0.89.0+1.20.1")
    modImplementation(fabricApi.module("fabric-command-api-v2", "0.83.0+1.19.4"))
    modImplementation("com.cobblemon:fabric:1.4.0+1.20.1-SNAPSHOT")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}