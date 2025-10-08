package nl.maas.wicket.framework.objects.constants

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import de.martinspielmann.wicket.chartjs.data.dataset.property.color.SimpleColor
import org.apache.commons.lang3.StringUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI


data class Colors private constructor(
    val families: List<String>,
    val hex: String,
    val name: String,
    val rgb: String
) : SimpleColor(name), Comparable<Colors>, java.io.Serializable {
    companion object {

        private val colors: List<Colors> =
            Gson().fromJson(
                BufferedReader(
                    InputStreamReader(
                        Colors::class.java.getResourceAsStream("/properties/colors.json"),
                        Charsets.UTF_8
                    )
                ),
                object : TypeToken<ArrayList<Colors>>() {}.getType()
            )


        fun get(): List<Colors> {
            return colors
        }

        fun getBootswatchThemeColors(theme: BootswatchTheme?): List<Colors> {
            var colors = get()
            theme?.let { provided ->
                val vars =
                    URI("https://bootswatch.com/5/${provided.name.lowercase()}/_variables.scss").toURL()
                        .readText(Charsets.UTF_8)
                val stringColors = processColors(vars)
                colors = stringColors.map { Colors(stringColors, it, it, it) }.sortedBy { it.hex }.plus(colors)
            }
            return colors
        }

        private fun processColors(vars: String): List<String> {
            val lines = vars.lines()
            val vars = lines.filter { it.startsWith("$") }
            val properColors = vars.filter {
                !(it.startsWith("\$white") || it.startsWith("\$black") || it.startsWith("\$gray")) && it.split(
                    StringUtils.SPACE
                ).filter { it.isNotBlank() }[1].startsWith("#")
            }
            return properColors.joinToString(StringUtils.SPACE).split(StringUtils.SPACE).filter { it.startsWith("#") }
        }
    }

    override fun toString(): String {
        return name
    }

    fun colorsLot(lotNumber: Int): Map<Int, List<Colors>> {
        val map = mutableMapOf<Int, List<Colors>>()
        val lotSize = Colors.get().size.div(lotNumber)
        for (lot in 0..(lotNumber - 1)) {
            val fromIndex = lot * lotSize
            val toIndex = fromIndex + lotSize
            map.put(lot, Colors.get().subList(fromIndex, toIndex).sorted())
        }
        return map
    }

    fun colorsForCatagories(
        numberOfCategories: Int,
        categorySize: Int
    ): ArrayList<List<Colors>> {
        val list = ArrayList<List<Colors>>()
        for (index in 0..numberOfCategories - 1) {
            val fromIndex = index * categorySize
            val toIndex = fromIndex + categorySize
            list.add(Colors.get().subList(fromIndex, toIndex).sorted())
        }
        return list
    }

    override fun compareTo(other: Colors): Int {
        return this.hex.compareTo(other.hex)
    }

    override fun getJson(): String {
        return return hex
    }
}