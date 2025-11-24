package nl.maas.framework.torrent.domain

import com.google.gson.Gson

abstract class Torrent() {

    var id: Long = 0
    lateinit var name: String
    
    override fun toString(): String {
        return Gson().toJson(this)
    }

    abstract fun getStatusEnum(): TorrentStatus
}