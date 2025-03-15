plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
}

group = "com.medhir"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

repositories {
    maven {
        name = "reposiliteRepositoryReleases"
        url = uri("https://repo.home.medhir.in/releases")
    }
    maven {
        name = "reposiliteRepositoryReleases"
        url = uri("http://10.0.3.90:9021/releases")
        isAllowInsecureProtocol = true
    }

}

dependencies {
    // Spring Boot Dependencies
    implementation(libs.springboot.starter.web){
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation(libs.springboot.starter.undertow)
    implementation(libs.springboot.starter.mongodb)
    implementation(libs.springboot.starter.validation)
    implementation(libs.springboot.starter.security)
    implementation(libs.springboot.starter.webflux)
    implementation(libs.springboot.starter.outh.server)
    implementation(libs.springboot.starter.outh.client)

    // JWT Dependencies
    implementation(libs.jwt.api)
    implementation(libs.jwt.impl)
    implementation(libs.jwt.jackson)

    // Lombok
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
