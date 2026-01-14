import io.ktor.plugin.OpenApiPreview

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
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
        summary = "API for the clients implementing MCI Roombooking"
        description = "baseUrl: baaaaseURL"
        termsOfService = "https://example.com/terms/"
        contact = "contact@example.com"
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
}
