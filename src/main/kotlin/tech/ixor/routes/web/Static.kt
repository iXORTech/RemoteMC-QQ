package tech.ixor.routes.web

import io.ktor.routing.*
import io.ktor.http.content.*

fun Route.static() {
    static("/") {
        staticBasePackage = "web"
        static("assets") {
            resources("style")
        }
    }
}
