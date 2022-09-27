package tech.ixor.plugins

import io.ktor.application.*
import tech.ixor.routes.web.registerWebRoutes

fun Application.configureRouting() {
    registerWebRoutes()
}
