package nl.maas.wicket.framework.components.base

import nl.maas.wicket.framework.components.elemental.DatePickerButton
import org.apache.wicket.AttributeModifier
import org.apache.wicket.Component
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.Button
import org.apache.wicket.markup.html.form.FormComponent
import org.apache.wicket.markup.html.image.ExternalImage
import org.apache.wicket.markup.html.image.Image
import org.apache.wicket.markup.html.link.AbstractLink
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Fragment
import org.apache.wicket.markup.html.panel.Panel
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.jvmName


class DynamicPanel(id: String) : Panel(id) {

    companion object {
        const val ROW_CONTENT_ID = "content"
    }

    val pageConfig: MutableMap<String, IntArray> = mutableMapOf()
    var rows: MutableMap<String, Pair<WebMarkupContainer, Array<WebMarkupContainer>>> = mutableMapOf()
    var contents: Map<Pair<Int, Component>, String> = mapOf()

    override fun onBeforeRender() {
        super.onBeforeRender()
        setUpPanel()
        addContents()
    }

    private fun setUpPanel() {
        val rows = object : ListView<String>("rows", pageConfig.keys.toMutableList()) {
            override fun populateItem(row: ListItem<String>) {
                row.addOrReplace(createRow(row.modelObject))
            }

            private fun createRow(rowName: String): WebMarkupContainer {
                val webMarkupContainer = WebMarkupContainer("row")
                webMarkupContainer.outputMarkupId = true
                val columns = (0..pageConfig[rowName]!!.size - 1).map { index ->
                    object : WebMarkupContainer("column") {
                        init {
                            outputMarkupId = true
                            add(AttributeModifier.append("class", "col-${pageConfig[rowName]!![index]}"))
                        }
                    }
                }
                rows.put(rowName, webMarkupContainer to columns.toTypedArray())
                val cols = object : ListView<WebMarkupContainer>("columns", columns) {
                    override fun populateItem(column: ListItem<WebMarkupContainer>) {
                        column.addOrReplace(column.modelObject)
                    }
                }
                webMarkupContainer.addOrReplace(cols)
                return webMarkupContainer
            }
        }
        addOrReplace(rows)
    }

    private fun addContents() {
        contents.forEach { row ->
            rows[row.value]!!.second[row.key.first].addOrReplace(row.key.second)
        }
    }

    fun addRow(name: String, vararg columnSizes: Int): DynamicPanel {
        require(columnSizes.sumOf { it } <= 12, { "Column sizes should add up to 12" })
        pageConfig.put(name, columnSizes)
        columnSizes.forEachIndexed { index, i -> addOrReplaceToColumn(name, index, Label::class, "") }
        return this
    }

    fun addRows(vararg rows: Pair<String, IntArray>): DynamicPanel {
        rows.forEach { addRow(it.first, *it.second) }
        return this
    }

    /**
     *  Adds a component of the type given for #clazz
     *
     *  parameters:
     *  @param rowName Name of the row
     *  @param columnIndex Index of the column with in the row starting with 0
     *  @param clazz The class of the {@link Component} to add or replace in the column
     *  @param parameters A vararg of all parameters to pass on to the {@link Component} starting after the 'id' parameter, which should be first in all Wicket components.
     */
    fun <T : Component> addOrReplaceToColumn(
        rowName: String,
        columnIndex: Int,
        clazz: KClass<T>,
        vararg parameters: Any
    ): DynamicPanel {
        val content =
            clazz.constructors.find {
                it.parameters.size == parameters.size + 1 && parameters.all { value ->
                    parameterTakesValue(it.parameters[parameters.indexOf(value) + 1], value)
                }
            }!!.call(ROW_CONTENT_ID, *parameters)
        contents = contents.filterNot { it.key.first.equals(columnIndex) && it.value.equals(rowName) }
            .plus((columnIndex to pickFragment(content)) to rowName)
        return this
    }

    /**
     * This method allows you to present a prepared {@link Component} to a column
     * Make sure to use #ROW_CONTENT_ID as #id for your component!
     *
     * @param  rowName the name of the row
     * @param columnIndex the index of the column in the row
     * @param component the component to add to the column (with #ROW_CONTENT_ID as #id)
     */
    fun addOrReplaceComponentToColumn(
        rowName: String,
        columnIndex: Int,
        component: Component
    ): DynamicPanel {
        contents = contents.filterNot { it.key.first.equals(columnIndex) && it.value.equals(rowName) }
            .plus((columnIndex to pickFragment(component)) to rowName)
        return this
    }

    fun addOrReplaceComponentsToRow(rowName: String, vararg component: Component): DynamicPanel {
        require(pageConfig.contains(rowName), { "Row $rowName not found!" })
        require(
            pageConfig[rowName]!!.size.equals(component.size),
            { "Number of columns in row $rowName does not match supplied number of components" })
        component.forEachIndexed { index, component -> addOrReplaceComponentToColumn(rowName, index, component) }
        return this
    }

    private fun pickFragment(component: Component): Fragment {
        return when (component) {
            is AbstractLink, is Button -> ButtonFragment(component)
            is DatePickerButton -> InputFragment(component)
            is FormComponent<*> -> if (component.label != null) {
                InputFragment(
                    component,
                    component.label.`object`
                )
            } else {
                InputFragment(component)
            }

            is Image -> ImageFragment(component)
            is ExternalImage -> ImageFragment(component)
            else -> DefaultFragment(component)
        }
    }

    private fun parameterTakesValue(
        parameter: KParameter,
        value: Any
    ): Boolean {
        var takesValue = false
        if (parameter.isVararg) {
            takesValue = (parameter.type.classifier as KClass<*>).jvmName.equals("[Lkotlin.${value::class.simpleName};")
        } else {
            takesValue = (parameter.type.classifier as KClass<*>).isInstance(value)
        }
        return takesValue
    }

    fun getRow(name: String): WebMarkupContainer {
        require(rows.keys.contains(name))
        return rows[name]!!.first
    }

    fun getColumn(name: String, columnIndex: Int): WebMarkupContainer {
        require(rows.keys.contains(name))
        require(rows[name]!!.second.size > columnIndex)
        return rows[name]!!.second[columnIndex]
    }

    private inner class InputFragment(
        val component: Component,
        val label: String = ""
    ) : Fragment("fragment", "inputFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(component, Label("inputLabel", label))
        }
    }

    private inner class ImageFragment(
        val component: Component
    ) : Fragment("fragment", "imageFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(component)
        }
    }

    private inner class DefaultFragment(
        val component: Component
    ) : Fragment("fragment", "defaultFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(component)
        }
    }

    private inner class ButtonFragment(
        val component: Component
    ) : Fragment("fragment", "buttonFragment", this) {
        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(component)
        }
    }
}