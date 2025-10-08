package nl.maas.wicket.framework.objects.behavior

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior
import de.agilecoders.wicket.core.util.Attributes
import de.agilecoders.wicket.core.util.Components
import org.apache.wicket.Component
import org.apache.wicket.markup.ComponentTag

class TableHeaderBehavior : TableBehavior() {

    val classNameProviders: MutableSet<ICssClassNameProvider> = mutableSetOf()

    fun createCssClassNames(): MutableSet<String> {
        return mutableSetOf("thead-dark")
    }

    override fun onComponentTag(component: Component, tag: ComponentTag) {
        Components.assertTag(component, tag, "thead")
        Attributes.addClass(tag, createCssClassNames())
    }

    fun headerType(type: String) {
        classNameProviders.add(object : ICssClassNameProvider {
            val type: String = type

            override fun cssClassName(): String {
                return if ("Basic".equals(this.type, true)) "thead" else "thead-${this.type.lowercase()}"
            }

        })
    }
}
