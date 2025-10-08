package nl.maas.framework.torrent.transmission

enum class METHODS {
    SESSION_GET(),
    TORRENT_GET,
    TORRENT_ADD,
    TORRENT_REMOVE;

    fun toMethodString(): String {
        return name.lowercase().replace('_', '-')
    }
}