package nl.maas.wicket.framework.objects.behavior

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider
import de.agilecoders.wicket.core.util.Attributes
import org.apache.wicket.Component
import org.apache.wicket.markup.ComponentTag

class TableBehavior : de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior() {
    val classNameProviders: MutableSet<ICssClassNameProvider> = mutableSetOf()

    fun createCssClassNames(): MutableSet<String> {
        return classNameProviders.map { it.cssClassName() }.toMutableSet()
    }

    override fun onComponentTag(component: Component, tag: ComponentTag) {
        super.onComponentTag(component, tag)

        Attributes.addClass(tag, createCssClassNames())
    }

    fun type(type: String) {
        classNameProviders.add(object : ICssClassNameProvider {
            override fun cssClassName(): String {
                return if ("Basic".equals(type, true)) "table" else "table-${type.lowercase()}"
            }
        })
    }


}