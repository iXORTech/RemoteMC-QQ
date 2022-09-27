package tech.ixor.utils

import java.io.File
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.*
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import tech.ixor.I18N

class FileDownloader {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun downloadFile(urlString: String, pathname: String, downloadDescription: String) {
        val client = HttpClient()
        val file = File(pathname)
        runBlocking {
            val httpResponse: HttpResponse = client.get(urlString) {
                onDownload { bytesSentTotal, contentLength ->
                    logger.info(I18N.logging_fileDownloader_downloading(downloadDescription))
                    logger.info(I18N.logging_fileDownloader_downloadingTo(pathname, bytesSentTotal, contentLength))
                }
            }
            val responseBody: ByteArray = httpResponse.content.toByteArray()
            if (!file.parentFile.exists()) {
                logger.info(I18N.logging_fileDownloader_dirNotExist(downloadDescription))
                file.parentFile.mkdirs()
                logger.info(I18N.logging_fileDownloader_dirDone())
            }
            file.writeBytes(responseBody)
            logger.info(I18N.logging_fileDownloader_saved(downloadDescription, pathname))
        }
    }
}
