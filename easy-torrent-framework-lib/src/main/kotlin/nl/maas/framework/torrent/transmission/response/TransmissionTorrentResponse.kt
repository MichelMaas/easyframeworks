package nl.maas.framework.torrent.transmission.response

open class TransmissionTorrentResponse {
    var arguments: ResponseArguments = ResponseArguments()
    lateinit var result: String
    var tag: Long = 0
}