import io.ktor.plugin.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
}

group = "edu.mci"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

val openApiGeneratedPath = layout.buildDirectory.file("generated/openapi/open-api.json")

ktor {
    @OptIn(OpenApiPreview::class)
    openApi {
        title = "MCI Roombooking"
        version = "0.1"
        summary =
            "IMPORTANT: Timestamps start/end are in Strings in ISO-8601: 2026-01-14T12:34:56Z --- !they aren't objects! it's just wrongly parsed by the open api generator"
        description =
            "Authentication: All protected endpoints require a JWT Bearer token in the Authorization header. 1. Login at /auth/login. 2. Use token as Bearer in header. Base URL: https://roombooking-backend-l7kv.onrender.com"
        license = "Apache/1.0"

        // Location of the generated specification (defaults to openapi/generated.json)
        target = openApiGeneratedPath
    }
}



tasks.named<ProcessResources>("processResources") {
    dependsOn("buildOpenApi")
    from(openApiGeneratedPath) {
        into("openapi")
        rename { "open-api.json" }
    }
}


dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.h2)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.bcrypt)
}
