package nl.maas.wicket.framework.components.elemental

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import nl.maas.wicket.framework.objects.enums.ButtonType
import nl.maas.wicket.framework.pages.BasePage
import org.apache.wicket.model.Model
import org.apache.wicket.request.mapper.parameter.PageParameters

class BaseNavbarButton private constructor(val buttonType: ButtonType, pageParameters: PageParameters) :
    NavbarButton<Void>(buttonType.pageClass.java, pageParameters, Model.of(buttonType.label)) {

    companion object {
        private fun toPageParameters(vararg params: Pair<String, Any>): PageParameters {
            val pageParameters = PageParameters()
            params.forEach { pageParameters.add(it.first, it.second) }
            return pageParameters
        }
    }

    constructor(buttonType: ButtonType, vararg params: Pair<String, Any>) : this(
        buttonType,
        toPageParameters(*params)
    )

    init {
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onBeforeRender() {
        super.onBeforeRender()
        isEnabled = findParent(BasePage::class.java).isButtonActive(this)
    }

    fun enable(): BaseNavbarButton {
        isEnabled = true
        return this
    }

    fun disable(): BaseNavbarButton {
        isEnabled = false
        return this
    }


}