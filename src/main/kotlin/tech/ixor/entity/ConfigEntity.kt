package tech.ixor.entity

import java.io.File

import com.sksamuel.hoplite.ConfigLoader
import org.slf4j.LoggerFactory
import tech.ixor.I18N
import tech.ixor.utils.FileDownloader

class ConfigEntity {
    private val logger = LoggerFactory.getLogger(javaClass)

    data class Ktor(val port: Int)

    data class Config(
        val language: String, val authKey: String, val ktor: Ktor
    )

    fun loadConfig(): Config {
        logger.info(I18N.logging_configEntity_loadingConfig())
        val pwd = System.getProperty("user.dir")
        val confFile = "$pwd/conf/config.yaml"
        if (!File(confFile).exists()) {
            logger.info(I18N.logging_configEntity_configFileNotFound())
            val fileDownloader = FileDownloader()
            fileDownloader.downloadFile(
                "https://cdn.jsdelivr.net/gh/iXORTech/RemoteMC-QQ/src/main/resources/conf/config.yaml",
                confFile,
                I18N.logging_configEntity_configFileDownloadDescription()
            )
        }
        return ConfigLoader().loadConfigOrThrow(confFile)
    }
}
