plugins {
    id("java")
    id("org.springframework.boot") version "3.2.6" // Последняя стабильная версия
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.7.3")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}