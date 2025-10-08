package nl.maas.wicket.framework.objects

import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn
import java.io.Serializable

data class Tuple(val columns: Map<String, Serializable>, val sortColumn: String = columns.keys.first()) : Serializable {

    constructor(vararg args: Pair<String, Serializable>) : this(args.toMap())

    override fun toString(): String {
        return columns.map { "${it.key}: ${it.value}" }.joinToString("\n")
    }

    override fun equals(other: Any?): Boolean {
        require(this::class.isInstance(other))
        val tuple = other as Tuple
        return this.columns.keys.equals(tuple.columns.keys) && this.columns.all { it.value.equals(tuple.columns[it.key]) }
    }

    fun toFilterString(translator: Translator = TranslatorPlaceHolder(), vararg translate: String): String {
        return columns.filterNot { it.key.equals("description", true) }
            .map { if (translate.contains(it.key)) translator.translate("${it.value}") else "${it.value}" }
            .joinToString(StringUtils.SPACE)
    }

    fun getColums(): List<IColumn<String, Serializable>> {
        return emptyList()
    }

    fun getValueForColumn(columnName: String): Serializable {
        return columns[columnName]!!
    }
}
