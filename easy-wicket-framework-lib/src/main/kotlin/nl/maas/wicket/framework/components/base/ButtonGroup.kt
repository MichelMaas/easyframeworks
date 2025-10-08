package nl.maas.wicket.framework.components.base

import org.apache.wicket.markup.html.link.AbstractLink

class ButtonGroup(id: String, vararg component: AbstractLink) :
    ComponentListView(id, *component) {
}