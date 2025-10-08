package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.components.elemental.TooltipLabel
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.head.CssReferenceHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.request.resource.CssResourceReference
import org.apache.wicket.request.resource.JavaScriptResourceReference
import java.io.Serializable
import java.math.BigInteger

open class DynamicTableComponent(
    id: String,
    val tuples: MutableList<Tuple>,
    val translator: Translator = TranslatorPlaceHolder(),
    val translateData: Boolean = true
) : Panel(id) {

    val tupleTranslator: Translator

    init {
        if (tuples.isEmpty()) {
            tuples.add(Tuple(mapOf(Pair("NOTHING", StringUtils.EMPTY))))
        }
        require(tuples.all { it.equals(tuples.first()) })
        tuples.sortedBy { it.columns.values.first().toString() }
        tupleTranslator = if (translateData) translator else TranslatorPlaceHolder()
    }

    override fun renderHead(response: IHeaderResponse) {
        val table =
            JavaScriptResourceReference(findPage()::class.java, "/static/js/bootstrap-table.min.js")
        (application as WebApplication).mountResource("js/table.js", table)
        val sticky =
            JavaScriptResourceReference(findPage()::class.java, "/static/js/bootstrap-table-sticky-header.min.js")
        (application as WebApplication).mountResource("js/sticky.js", sticky)
        val tableStyle = CssResourceReference(findPage()::class.java, "static/css/bootstrap-table.min.css")
        (application as WebApplication).mountResource("css/table.css", tableStyle)
        val stickyStyle = CssResourceReference(findPage()::class.java, "static/css/bootstrap-table-sticky-header.css")
        (application as WebApplication).mountResource("css/sticky.css", stickyStyle)

        response.render(
            JavaScriptReferenceHeaderItem.forReference(sticky)
        )
        response.render(
            JavaScriptReferenceHeaderItem.forReference(table)
        )
        response.render(CssReferenceHeaderItem.forReference(tableStyle))
        response.render(CssReferenceHeaderItem.forReference(stickyStyle))
    }


    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(HeaderRepeater(), TupleRepeater())
    }

    private inner class HeaderRepeater : ListView<String>("columnHeader", tuples.first().columns.keys.toList()) {
        override fun populateItem(item: ListItem<String>) {
            item.add(
                Label(
                    "columnLabel",
                    translator.translate(item.modelObject)
                )
            )
        }

    }

    private inner class TupleRepeater : ListView<Tuple>("tuple", tuples) {
        override fun populateItem(item: ListItem<Tuple>) {
            item.add(ColumnRepeater(item.modelObject.columns.values.toList()))
            item.add(object : AjaxEventBehavior("click") {
                override fun onEvent(target: AjaxRequestTarget) {
                    this@DynamicTableComponent.onTupleClick(target, item.modelObject)
                }
            })
        }
    }

    private inner class ColumnRepeater(val columns: List<Serializable>) : ListView<Serializable>("column", columns) {
        override fun populateItem(item: ListItem<Serializable>) {
            item.add(
                TooltipLabel(
                    "content",
                    tupleTranslator.translate(
                        item.modelObject.toString()
                    ),
                    BigInteger.valueOf(150).div(columns.size.toBigInteger()).toInt()
                )
            )
        }
    }

    open fun onTupleClick(target: AjaxRequestTarget, tuple: Tuple) {

    }

}