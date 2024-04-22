plugins {
	java
	id("org.springframework.boot") version "3.2.3"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.sonarqube") version "5.0.0.4638"
	id("jacoco")
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
	testImplementation("org.jacoco:org.jacoco.agent:0.8.9")
	implementation("org.springframework.security:spring-security-web:6.2.4")
	implementation("org.springframework.security:spring-security-config:6.2.4")
	testImplementation("org.springframework.security:spring-security-test:6.0.0")
	implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
}
jacoco {
	toolVersion = "0.8.9"  // specify the version you want to use
}
tasks.jacocoTestReport {
	dependsOn("test")  // ensures that the test task is run before generating the report
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
		property("sonar.login", "sqp_5b08838331b3c75e02deb42791f8c40d7081f695")
		property("sonar.jacoco.reportPaths", buildDir.resolve("reports/jacoco/test/jacocoTestReport.xml").absolutePath)
		property("sonar.coverage.jacoco.xmlReportPaths", "${buildDir}/reports/jacoco/test/jacocoTestReport.xml")

	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
