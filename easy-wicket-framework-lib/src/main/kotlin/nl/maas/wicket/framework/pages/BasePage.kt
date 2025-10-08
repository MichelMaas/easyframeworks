package nl.maas.wicket.framework.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarButton
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents
import de.agilecoders.wicket.extensions.markup.html.bootstrap.spinner.SpinnerBehavior
import nl.maas.wicket.framework.components.elemental.BaseNavbarButton
import nl.maas.wicket.framework.components.elemental.HorizontalNavbarProvider
import nl.maas.wicket.framework.components.elemental.ToolTipNavBar
import nl.maas.wicket.framework.objects.BasePageProperties
import nl.maas.wicket.framework.objects.enums.ButtonType
import nl.maas.wicket.framework.objects.serializables.SerializableFileResourceReference
import nl.maas.wicket.framework.services.ModelCache
import nl.maas.wicket.framework.services.Translator
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.AttributeModifier
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog
import org.apache.wicket.markup.head.CssReferenceHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.html.GenericWebPage
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.protocol.http.WebApplication
import java.time.Duration
import java.time.LocalTime


abstract class BasePage<T : ModelCache> protected constructor(
    val modelCache: T,
    val translator: Translator,
    iconPath: String = "icon.png",
    brandPath: String = "brand.png",
    val brandName: String = "Brand",
    val applicationVersion: String = ""
) : GenericWebPage<Void?>() {

    constructor(options: BasePageProperties<T>) : this(
        options.modelCache,
        options.translator,
        options.iconPath,
        options.brandPath,
        options.brandName,
        options.applicationVersion
    )

    lateinit var navbar: Navbar
    private val laddaBehavior = SpinnerBehavior()

    val iconReference =
        SerializableFileResourceReference("favicon", iconPath)
    val brandReference =
        SerializableFileResourceReference("brand", brandPath)
    val start: LocalTime = LocalTime.now()

    override fun onInitialize() {
        super.onInitialize()
        outputMarkupId = true
        (application as WebApplication).mountResource("/images/icon.png", iconReference)
        add(laddaBehavior)
    }

    protected open fun newNavbar(markupId: String): Navbar {
        navbar =
            ToolTipNavBar(
                markupId,
                applicationVersion
            )
        navbar.position = Navbar.Position.TOP
        navbar.add(HorizontalNavbarProvider())
        navbar.setBrandName(Model.of(translator.translate(brandName)))
        navbar.setBrandImage(brandReference, Model.of(StringUtils.EMPTY))
        val navbarButtons: Array<NavbarButton<*>> = createNavBarButtons()
        navbar.addComponents(NavbarComponents.transform(Navbar.ComponentPosition.LEFT, *navbarButtons))
        navbar.outputMarkupId = true
        return navbar
    }

    abstract fun createNavBarButtons(): Array<NavbarButton<*>>

    fun findNavButton(type: ButtonType): BaseNavbarButton? {
        return navbar.filter { component -> component.javaClass.isAssignableFrom(BaseNavbarButton::class.java) }
            .map { it as BaseNavbarButton }.firstOrNull { type.equals(it.buttonType) }
    }


    private val modalDialog = ModalDialog("notifications")
    val loader = WebMarkupContainer("loader")

    override fun onBeforeRender() {
        super.onBeforeRender()
        loader.outputMarkupId = true
        addOrReplace(newNavbar("navbar"), modalDialog, loader, WebMarkupContainer("spinner"))
    }


    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        response.render(CssReferenceHeaderItem.forUrl("css/loader.css"))
        response.render(CssReferenceHeaderItem.forUrl("http://maxcdn.bootstrapcdn.com/font-awesome/4.1.0/css/font-awesome.min.css"))
    }

    override fun onAfterRender() {
        super.onAfterRender()
        val end = LocalTime.now()
        println("Render time for page ${this::class.simpleName} is: ${Duration.between(start, end)}")
    }

    abstract fun isButtonActive(button: BaseNavbarButton): Boolean

    private inner class NavtabsProvider : AttributeModifier("class", "nav nav-tabs nav-tabs-dark")


    protected fun showModal(target: AjaxRequestTarget, title: String, text: String) {
        modalDialog.add(Label("modalTitle", title), Label("modalContent", text))
        modalDialog.open(target)
    }

    fun ajaxStartLoader(target: AjaxRequestTarget) {
        val classAttr = loader.markupAttributes["class"].toString()
        loader.add(AttributeModifier.replace("class", classAttr.replace("d-none", StringUtils.EMPTY).trim()))
        target.add(loader)
    }

    fun ajaxStopLoader(target: AjaxRequestTarget) {
        loader.add(AttributeModifier.append("class", "d-none"))
        target.add(loader)
    }

}