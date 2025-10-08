package nl.maas.wicket.framework.components.base

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.BootstrapDefaultDataTable
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.objects.TupleDataProvider
import nl.maas.wicket.framework.objects.behavior.CSSScriptLoader
import nl.maas.wicket.framework.objects.behavior.JSScriptsLoader
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.ajax.AjaxEventBehavior
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.markup.repeater.Item
import org.apache.wicket.model.IModel
import java.io.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.stream.Collectors

class DynamicDataTable private constructor(
    id: String,
    private var dataProvider: TupleDataProvider,
    private val rowsPerPage: Int,
    private val maxTextLength: Int = 70,
    private val onTupleClick: (AjaxRequestTarget, Tuple) -> Unit = { target, tuple -> },
    private val showClicked: Boolean = false,
    val translator: Translator,
) : Panel(id) {

    protected constructor(
        id: String,
        tuples: List<Tuple>,
        rowsPerPage: Int = 10,
        maxTextLength: Int = 70,
        onTupleClick: (AjaxRequestTarget, Tuple) -> Unit = { target, tuple -> },
        showClicked: Boolean = false,
        translator: Translator,
        vararg translateContent: String = arrayOf()
    ) : this(
        id,
        TupleDataProvider.toTupleDataProvider(tuples, translator, maxTextLength, *translateContent),
        rowsPerPage,
        maxTextLength,
        onTupleClick,
        showClicked,
        translator
    )

    companion object {
        private val dataTables: MutableSet<DynamicDataTable> = mutableSetOf()
        fun get(
            id: String = StringUtils.EMPTY,
            tuples: List<Tuple> = listOf(),
            rowsPerPage: Int = 10,
            maxTextLength: Int = 70,
            translator: Translator = TranslatorPlaceHolder(),
            vararg translateContent: String = arrayOf(),
            onTupleClick: (AjaxRequestTarget, Tuple) -> Unit = { target, tuple -> },
            showClicked: Boolean = false
        ): DynamicDataTable {
            val predicate =
                { chk: DynamicDataTable ->
                    chk.id.equals(id) && chk.dataProvider.tuplesEqual(tuples) && chk.rowsPerPage.equals(rowsPerPage)
                }
            if (dataTables.none { predicate(it) }) {
                dataTables.add(
                    DynamicDataTable(
                        id,
                        tuples,
                        rowsPerPage,
                        maxTextLength,
                        onTupleClick,
                        showClicked,
                        translator,
                        *translateContent
                    )
                )
            }
            return dataTables.first { predicate(it) }
        }
    }

    val tableContainer: WebMarkupContainer

    init {
        tableContainer = object : WebMarkupContainer("tableContainer") {
            init {
                outputMarkupId = true
            }

            override fun onBeforeRender() {
                super.onBeforeRender()
                val dataTable = InnerTable(dataProvider, rowsPerPage)
                addOrReplace(dataTable)
            }
        }
        addOrReplace(tableContainer)
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        val css = "#${tableContainer.markupId}{max-height: ${calculateHeight()}em; overflow: auto;}"
            .plus("#${tableContainer.markupId} .table {position: sticky;top: 0;width: 100%;}")
        CSSScriptLoader.load("table${this.markupId}", response, css)
    }

    private fun calculateHeight() = rowsPerPage.toBigDecimal().times(
        BigDecimal("2.2")
    ).setScale(0, RoundingMode.HALF_UP).toBigInteger()

    fun update(tuples: List<Tuple>, target: AjaxRequestTarget) {
        dataProvider = dataProvider.update(tuples)
        target.add(tableContainer)
    }

    fun striped(): DynamicDataTable {
        striped = true
        return this
    }

    /**
     * adds the "sm" style to table
     *
     * @return this instance for chaining
     */
    fun sm(): DynamicDataTable {
        sm = true
        return this
    }

    /**
     * adds the "bordered" style to table
     *
     * @return this instance for chaining
     */
    fun bordered(): DynamicDataTable {
        bordered = true
        return this
    }

    /**
     * adds the "hover" flag to table
     *
     * @return this instance for chaining
     */
    fun hover(): DynamicDataTable {
        hover = true
        return this
    }

    /**
     * adds the "dark" flag to table
     *
     * @return this instance for chaining
     */
    fun dark(): DynamicDataTable {
        setClass("table-dark")
        return this
    }

    fun light(): DynamicDataTable {
        setClass("table-light")
        return this
    }

    fun primary(): DynamicDataTable {
        setClass("table-primary")
        return this
    }

    fun secondary(): DynamicDataTable {
        setClass("table-secondary")
        return this
    }

    fun invertHeader(): DynamicDataTable {
        if (dataTableHasClass("table-light")) setHeaderClass("bg-dark text-light")
        if (dataTableHasClass("table-dark")) setHeaderClass("bg-light text-dark")
        if (dataTableHasClass("table-primary")) setHeaderClass("bg-secondary text-primary")
        if (dataTableHasClass("table-secondary") || dataTableHasClass(StringUtils.EMPTY)) setHeaderClass(
            "bg-primary text-secondary"
        )
        return this
    }

    private var tableClass = StringUtils.EMPTY
    private var headerClass = StringUtils.EMPTY
    private var striped = false
    private var sm = false
    private var bordered = false
    private var hover = false
    private var selected: String = StringUtils.EMPTY

    inner class InnerTable(dataProvider: TupleDataProvider, rowsPerPage: Int) :
        BootstrapDefaultDataTable<Tuple, java.io.Serializable>(
            "table",
            dataProvider.columns as List<IColumn<Tuple, Serializable>>,
            dataProvider,
            256600
        ) {



        init {
            outputMarkupId = true
            if (this@DynamicDataTable.hover) hover()
            if (this@DynamicDataTable.sm) sm()
            if (this@DynamicDataTable.striped) striped()
            if (this@DynamicDataTable.bordered) bordered()
        }

        override fun renderHead(response: IHeaderResponse) {
            super.renderHead(response)
            JSScriptsLoader.load(
                response, "$('#${markupId}').addClass('$tableClass')",
                "$('#${markupId}').find('thead').removeClass().addClass('sticky-top $headerClass')"
            )
            if (!selected.isNullOrBlank() && showClicked) {
                val selectedId = streamChildren().collect(Collectors.toList()).filter { Item::class.isInstance(it) }
                    .filter { Tuple::class.isInstance(it.defaultModel.`object`) }
                    .filter { (it.defaultModel.`object` as Tuple).toFilterString().equals(selected) }
                    .firstOrNull()?.markupId ?: StringUtils.EMPTY
                JSScriptsLoader.load(
                    response,
                    "$('#${markupId}').find('#${selectedId}').addClass('table-success')",
                    "$('#${markupId}').find('#${selectedId}').focus()"
                )
                selected = StringUtils.EMPTY
            }
        }

        override fun newRowItem(id: String, index: Int, model: IModel<Tuple>): Item<Tuple> {
            val item = super.newRowItem(id, index, model)
            item.add(object : AjaxEventBehavior("click") {
                override fun onEvent(target: AjaxRequestTarget) {
                    selected = model.`object`.toFilterString()
                    onTupleClick(target, model.`object`)
                }

            })
            return item
        }


    }

    fun setClass(type: String) {
        tableClass = type
    }

    fun dataTableHasClass(type: String): Boolean {
        return tableClass.equals(type, true)
    }

    fun setHeaderClass(type: String) {
        headerClass = type
    }
}