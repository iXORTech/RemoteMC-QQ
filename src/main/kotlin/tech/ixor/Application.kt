package tech.ixor

import org.slf4j.LoggerFactory
import de.comahe.i18n4k.i18n4k
import de.comahe.i18n4k.config.I18n4kConfigDefault
import tech.ixor.entity.ConfigEntity
import tech.ixor.utils.VersionUtil
import tech.ixor.plugins.configureRouting
import tech.ixor.plugins.configureSerialization
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import love.forte.simboot.core.*
import java.util.*

@SimbootApplication(botResources = ["bots/*.bot*"], classesPackages = ["tech.ixor.listeners"])
class Application

private val logger = LoggerFactory.getLogger("RemoteMC-QQ")

suspend fun main(vararg args: String) {
    // Load Config
    val config = ConfigEntity().loadConfig()

    // Load i18n
    val i18n4kConfig = I18n4kConfigDefault()
    i18n4k = i18n4kConfig
    i18n4kConfig.locale = Locale(config.language)

    logger.info(I18N.starting())
    logger.info("${I18N.selectedLanguage} ${I18N.language}")

    // Load Version
    VersionUtil.loadVersionProperties()
    logger.info("${I18N.version} ${VersionUtil.getVersion()}")
    val stage = VersionUtil.getProperty("stage")
    if (stage.contains("dev") || stage.contains("alpha") || stage.contains("beta")) {
        logger.warn("${I18N.experimental}")
    } else if (stage.contains("rc")) {
        logger.warn("${I18N.releaseCandidate}")
    }

    // Start the Ktor server
    embeddedServer(CIO, port = config.ktor.port, host = "127.0.0.1") {
        configureRouting()
        configureSerialization()
    }.start(wait = false)
    // Start the Simbot application
    SimbootApp.run(Application::class, args = args).join()
}
