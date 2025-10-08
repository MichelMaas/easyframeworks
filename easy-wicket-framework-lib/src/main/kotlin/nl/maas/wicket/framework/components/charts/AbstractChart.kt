package nl.maas.wicket.framework.components.charts

import de.agilecoders.wicket.themes.markup.html.bootswatch.BootswatchTheme
import nl.maas.wicket.framework.objects.constants.Colors
import nl.maas.wicket.framework.panels.AbstractPanel
import nl.maas.wicket.framework.services.Translator
import nl.maas.wicket.framework.services.TranslatorPlaceHolder
import org.apache.wicket.markup.html.basic.Label

abstract class AbstractChart(
    id: String,
    protected val label: String,
    protected val translator: Translator = TranslatorPlaceHolder(),
    val bootswatchTheme: BootswatchTheme? = null
) : AbstractPanel(id) {

    protected val colors get() = Colors.getBootswatchThemeColors(bootswatchTheme)

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(Label("chartLabel", translator.translate(label)))
    }
}