import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.noarg") version "1.9.20"
}

noArg {
    annotation("com.kotlin.spring.management.annotations.systemCommon.NoArgsConstructor")
}

group = "com.kotlin.spring"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-configuration-processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    // SpringBoot Starter Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    // Spring AOP
    implementation("org.springframework.boot:spring-boot-starter-aop")
    // SpringBoot Dev Tools
    // Test Utils (Starter for testing Spring Boot applications with libraries including JUnit Jupiter, Hamcrest and Mockito)
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-test
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.5")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // JDBC Driver For SpringBoot Starter
    implementation("org.springframework.boot:spring-boot-starter-jdbc")

    // ThymeLeaf
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")


    // Kotlin Reflect
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    // Jackson Module Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Security (SpringBoot Starter Security 3.1.5)
    // https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-security
    implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
    implementation("org.springframework.security:spring-security-config")
    implementation("org.springframework.security:spring-security-web")
    // JJWT - API 0.12.3
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api
    implementation("io.jsonwebtoken:jjwt-api:0.12.3")
    // JJWT - Impl 0.12.3
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    // JJWT - Extension(Jackson Databind)
    // https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")




    // MyBatis Spring Boot Starter
    // https://mvnrepository.com/artifact/org.mybatis.spring.boot/mybatis-spring-boot-starter
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.2")
    // MyBatis TypeHandler
    // https://mvnrepository.com/artifact/org.mybatis/mybatis-typehandlers-jsr310
    implementation("org.mybatis:mybatis-typehandlers-jsr310:1.0.2")

    // MariaDB JavaClient 3.2.0
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc:mariadb-java-client:3.2.0")

    // Log4JDBC 1.2
    // https://mvnrepository.com/artifact/com.googlecode.log4jdbc/log4jdbc
    implementation("com.googlecode.log4jdbc:log4jdbc:1.2")

    // (Swagger) SpringDoc Starter - WebMVC 2.2.0
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-starter-webmvc-ui
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")



    // AspectJ Weaver 1.9.21
    // https://mvnrepository.com/artifact/org.aspectj/aspectjweaver
    implementation("org.aspectj:aspectjweaver:1.9.21")
    //AspectJ Runtime 1.9.21
    // https://mvnrepository.com/artifact/org.aspectj/aspectjrt
    implementation("org.aspectj:aspectjrt:1.9.21")



}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

