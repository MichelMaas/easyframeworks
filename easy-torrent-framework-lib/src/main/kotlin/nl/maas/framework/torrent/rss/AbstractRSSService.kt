package nl.maas.framework.torrent.rss

import com.apptasticsoftware.rssreader.Item
import com.apptasticsoftware.rssreader.RssReader
import java.nio.file.Paths

abstract class AbstractRSSService {

    fun readFeed(url: String): List<Item> {
        val toFile = Paths.get(url).toFile()
        return RssReader().read(toFile.inputStream()).toList()
    }
}