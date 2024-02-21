plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.sonarqube") version "5.0.0.4638"
	id("jacoco")
}

val sonarSecret = System.getenv("SONAR_LOGIN")
val jacocoAgentVersion = "0.8.9"
val springSecurityWebConfigVersion = "6.2.4"
val springSecurityTestJakartaApiVersion = "6.0.0"
val jsonWebTokenApiJacksonVersion = "0.11.5"
val jakartaXmlJaxbVersion = "3.0.1"
val javaAuthJwt = "4.4.0"
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
	gradlePluginPortal()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	implementation("com.auth0:java-jwt:$javaAuthJwt")
	implementation("io.jsonwebtoken:jjwt-impl:$jsonWebTokenApiJacksonVersion")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:$jakartaXmlJaxbVersion")
	implementation("org.springframework.security:spring-security-web:$springSecurityWebConfigVersion")
	implementation("org.springframework.security:spring-security-config:$springSecurityWebConfigVersion")
	implementation("jakarta.servlet:jakarta.servlet-api:$springSecurityTestJakartaApiVersion")
	implementation("org.springframework.boot:spring-boot-starter-websocket")

	testImplementation("org.jacoco:org.jacoco.agent:$jacocoAgentVersion")
	testImplementation("org.springframework.security:spring-security-test:$springSecurityTestJakartaApiVersion")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2")

	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.glassfish.jaxb:jaxb-runtime:$jakartaXmlJaxbVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-api:$jsonWebTokenApiJacksonVersion")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jsonWebTokenApiJacksonVersion")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.projectlombok:lombok")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
	mainClass.set("unlimitedmarketplace.UnlimitedmarketplaceApplication")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
	toolVersion = jacocoAgentVersion  // specify the version you want to use
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)  // ensures that the test task is run before generating the report
	reports {
		xml.required.set(true)  // required by SonarQube
		html.required.set(true)  // for human-readable reports
	}
}

tasks.test {
	finalizedBy("jacocoTestReport")  // run jacocoTestReport after tests are complete
}

sonarqube {
	properties {
		property("sonar.projectKey", "unlimitedmarketplace_sonar")
		property("sonar.projectName", "unlimitedmarketplace_sonar")
		property("sonar.host.url", "http://localhost:9000")
		property("sonar.login", sonarSecret)
		property("sonar.jacoco.reportPaths", buildDir.resolve("reports/jacoco/test/jacocoTestReport.xml").absolutePath)
		property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")
	}
}

