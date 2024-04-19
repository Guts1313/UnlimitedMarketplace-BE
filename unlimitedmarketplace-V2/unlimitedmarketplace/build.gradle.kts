plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.sonarqube") version "5.0.0.4638"

}

group = "semester3_angel_unlimitedmarketplace"
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")
	implementation("com.auth0:java-jwt:4.4.0")
	implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:3.0.1")
	runtimeOnly("org.glassfish.jaxb:jaxb-runtime:3.0.1")
	runtimeOnly("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
}
sonarqube {
	properties {
		property("sonar.projectKey", "unlimited_test_sonar")
		property("sonar.projectName", "Unlimited Test Sonar")
		property("sonar.host.url", "http://localhost:9000")
		property("sonar.login", "sqp_4bc9743afb616132b579396b5deb554f4744ec10")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
