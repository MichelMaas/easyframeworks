package nl.maas.framework.torrent.transmission.request

import nl.maas.framework.torrent.transmission.METHODS

class TransmissionFetchTorrentBody(vararg val IDs: Int) : TransmissionBody() {
    init {
        appendArguments("fields", "id", "name", "status", "percentComplete", "rateDownload", "rateUpload")
        if (IDs.isNotEmpty()) {
            appendArguments("ids", IDs)
        }
        setMethod(METHODS.TORRENT_GET)
    }
}