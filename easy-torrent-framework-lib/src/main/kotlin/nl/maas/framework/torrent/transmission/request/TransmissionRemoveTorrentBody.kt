package nl.maas.framework.torrent.transmission.request

import nl.maas.framework.torrent.transmission.METHODS

class TransmissionRemoveTorrentBody(vararg id: Long) : TransmissionBody() {

    init {
        setMethod(METHODS.TORRENT_REMOVE)
        setArgument("ids", id.toTypedArray())
        setArgument("delete-local-data", "false")
    }
}