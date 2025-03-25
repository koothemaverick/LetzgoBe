plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.letzgo"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	runtimeOnly("org.postgresql:postgresql")
	implementation ("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("com.amazonaws:aws-java-sdk-s3:1.12.772")
	implementation ("com.google.maps:google-maps-services:2.2.0")

	// netty
	implementation("org.springframework.boot:spring-boot-starter-reactor-netty")
	developmentOnly("io.netty:netty-all:4.1.100.Final")

	// jjwt
	implementation("io.jsonwebtoken:jjwt-api:0.12.6")
	implementation("io.jsonwebtoken:jjwt-impl:0.12.6")
	implementation("io.jsonwebtoken:jjwt-jackson:0.12.6")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.google.api-client:google-api-client:2.2.0")

	// redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// aws s3
	implementation(platform("software.amazon.awssdk:bom:2.24.0"))
	implementation("software.amazon.awssdk:s3")
	implementation("com.vladmihalcea:hibernate-types-60:2.21.1")
	implementation("io.github.cdimascio:dotenv-java:2.2.0")

	// rabbitMQ
	implementation("org.springframework.boot:spring-boot-starter-websocket")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	testImplementation("org.springframework.amqp:spring-rabbit-test")

	// MongoDB
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
