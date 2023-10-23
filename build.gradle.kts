plugins {
    id("java")
}

group = "develop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    // Lombok
    implementation("org.projectlombok:lombok:1.18.30")
    annotationProcessor ("org.projectlombok:lombok:1.18.30")
    // Logger
    implementation("ch.qos.logback:logback-classic:1.4.11")
    // Project Reactor
    implementation("io.projectreactor:reactor-core:3.5.10")
    // Ibatis
    implementation("org.mybatis:mybatis:3.5.13")
    // GSON
    implementation("com.google.code.gson:gson:2.10.1")
    // Mockito
    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")
    // R2
    implementation("io.r2dbc:r2dbc-h2:1.0.0.RELEASE")
    implementation("io.r2dbc:r2dbc-pool:1.0.0.RELEASE")
    // JWT
    implementation("com.auth0:java-jwt:4.2.1")
    // BCcrypt
    implementation("org.mindrot:jbcrypt:0.4")
}


tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    // version para compilar y ejecutar en Java 11, subir a 17
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "develop.server.Server"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
    useJUnitPlatform()
}