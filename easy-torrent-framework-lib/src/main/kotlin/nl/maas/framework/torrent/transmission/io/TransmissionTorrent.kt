package nl.maas.framework.torrent.transmission.io

import nl.maas.framework.torrent.domain.Torrent
import nl.maas.framework.torrent.domain.TorrentStatus

class TransmissionTorrent constructor() :
    Torrent() {

    var status: Int = 0
    var percentComplete: Double = 0.0
        set(value) {
            percentComplete = value.times(10000)
        }
    var rateDownload: Long = 0
    var rateUpload: Long = 0

    override fun getStatusEnum(): TorrentStatus {
        return when (status) {
            0 -> TorrentStatus.STOPPED
            4 -> TorrentStatus.DOWNLOADING
            6 -> TorrentStatus.DONE
            else -> TorrentStatus.WAITING
        }
    }

}