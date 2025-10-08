package nl.maas.framework.torrent.transmission.response

import com.google.gson.Gson
import nl.maas.framework.torrent.transmission.io.TransmissionTorrent

class TransmissionResponse(private val body: String) {

    private lateinit var response: TransmissionTorrentResponse


    fun getFullString(): String = body

    fun getTorrentsList(): List<TransmissionTorrent> {
        loadResponse()
        return response.arguments.torrents.toList()
    }

    fun successful(): Boolean {
        loadResponse()
        return response.result.equals("success")
    }

    fun referenceTag(): Long {
        loadResponse()
        return response.tag
    }

    private fun loadResponse() {
        if (!this::response.isInitialized) {
            response = Gson().fromJson<TransmissionTorrentResponse>(
                body,
                TransmissionTorrentResponse::class.java
            )
        }
    }

}