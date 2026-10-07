package nl.maas.framework.torrent.rest.client

import nl.maas.framework.torrent.transmission.io.TransmissionTorrent
import nl.maas.framework.torrent.transmission.request.*
import nl.maas.framework.torrent.transmission.response.TransmissionResponse
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*

class TransmissionClient(val url: URI, val user: String, val pass: String, val passEncoded: Boolean = false) {
    var sessionID: String = ""

    fun fetchAll(): List<TransmissionTorrent> {
        fetchSessionID()
        val response = send(TransmissionFetchTorrentBody())
        val torrents = response.getTorrentsList()
        return torrents
    }

    fun addTorrent(url: String): Boolean {
        fetchSessionID()
        val response = send(TransmissionAddTorrentBody(url))
        return response.successful()
    }

    fun removeTorrent(vararg torrent: TransmissionTorrent): Boolean {
        fetchSessionID()
        val response = send(TransmissionRemoveTorrentBody(*torrent.map { it.id }.toLongArray()))
        return response.successful()
    }

    protected fun fetchSessionID() {
        if (sessionID.isBlank()) {
            val requestBody = TransmissionSessionRequestBody()
            val response = send(requestBody)
            val responseBody = response.getFullString()
            sessionID = responseBody.substringAfter("X-Transmission-Session-Id: ").substringBefore("</code>")
        }
    }

    private fun send(requestBody: TransmissionBody): TransmissionResponse {
        try {
            val client = HttpClient.newBuilder().authenticator(
                BaseAuthenticator(
                    user,
                    if (passEncoded) String(Base64.getDecoder().decode(pass)) else pass
                )
            ).build()
            val request =
                HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(requestBody.toJSon())).uri(url)
                    .header("Content-Type", "application/json").header("X-Transmission-Session-Id", sessionID).build()
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            return TransmissionResponse(response.body())
        } catch (e: Exception) {
            return TransmissionResponse(e.localizedMessage)
        }
    }
}