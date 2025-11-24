package nl.maas.framework.torrent.transmission.request

import nl.maas.framework.torrent.transmission.METHODS

class TransmissionSessionRequestBody : TransmissionBody() {
    init {
        setArgument("fields", arrayOf("version"))
        setMethod(METHODS.SESSION_GET)
    }
}