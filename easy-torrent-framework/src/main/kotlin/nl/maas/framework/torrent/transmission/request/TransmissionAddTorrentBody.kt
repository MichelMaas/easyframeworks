package nl.maas.framework.torrent.transmission.request

import nl.maas.framework.torrent.transmission.METHODS

class TransmissionAddTorrentBody(fileLocation: String) : TransmissionBody() {

    init {
        setMethod(METHODS.TORRENT_ADD)
        setArgument("filename", fileLocation)
    }

}