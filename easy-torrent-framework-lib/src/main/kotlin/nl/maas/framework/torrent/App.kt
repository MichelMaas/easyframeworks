package nl.maas.framework.torrent

import nl.maas.framework.torrent.rest.client.TransmissionClient
import nl.maas.framework.torrent.rss.AbstractRSSService
import java.net.URI
import java.util.*

/**
 * Hello world!
 *
 */
class App {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val client = TransmissionClient(
                URI.create("http://10.0.0.6:8181/transmission/rpc"),
                "michel",
                Base64.getEncoder().encodeToString("llae2215".toByteArray()),
                true
            )
            //            .fetchAll()
//                .forEach { println(it.toString()) }
            val items = object :
                AbstractRSSService() {}.readFeed("/home/michel/rss.xml")
            items.forEach { println(client.addTorrent(it.link.orElse("")).toString()) }
        }
    }
}
