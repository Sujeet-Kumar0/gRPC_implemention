plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
}

java {
    sourceSets.getByName("main").resources.srcDir("src/main/proto")
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}