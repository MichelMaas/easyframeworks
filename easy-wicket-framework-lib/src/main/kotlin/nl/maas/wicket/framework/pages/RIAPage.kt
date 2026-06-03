package nl.maas.wicket.framework.pages

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.NavbarComponents
import nl.maas.wicket.framework.components.base.Loader
import nl.maas.wicket.framework.components.base.RIANavbarButton
import nl.maas.wicket.framework.components.elemental.HorizontalNavbarProvider
import nl.maas.wicket.framework.components.elemental.RIANavbar
import nl.maas.wicket.framework.components.elemental.VerticalNavbarProvider
import nl.maas.wicket.framework.objects.RiaPageProperties
import nl.maas.wicket.framework.objects.behavior.CSSLoader
import nl.maas.wicket.framework.objects.enums.NavbarOrientation
import nl.maas.wicket.framework.objects.enums.NavbarType
import nl.maas.wicket.framework.objects.serializables.SerializableFileResourceReference
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.services.ModelCache
import nl.maas.wicket.framework.services.Translator
import org.apache.commons.lang3.StringUtils
import org.apache.wicket.Component
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.attributes.AjaxCallListener
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes
import org.apache.wicket.extensions.ajax.markup.html.AjaxLazyLoadPanel
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.MetaDataHeaderItem
import org.apache.wicket.markup.html.GenericWebPage
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.model.Model
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.resource.FileSystemResourceReference
import java.util.*
import kotlin.reflect.KClass


abstract class RIAPage<T : ModelCache>(
    private val title: String = "Your RIA App",
    startPanel: RIAPanel,
    private val modelCache: T,
    private val translator: Translator,
    private val iconPath: String = "icon.png",
    private val brandPath: String = "brand.png",
    private val brandName: String = "Brand",
    private val applicationVersion: String = "",
    private val navbarType: NavbarType = NavbarType.LIGHT,
    private val navbarOrientation: NavbarOrientation = NavbarOrientation.HORIZONTAL
) : GenericWebPage<T>() {

    constructor(options: RiaPageProperties<T>) : this(
        options.appName,
        options.startPanel,
        options.modelCache,
        options.translator,
        options.iconPath,
        options.brandPath,
        options.brandName,
        options.applicationVersion,
        options.navbarType,
        options.navbarOrientation
    )

    companion object {
        val PANEL_ID = "PanelToLoad"
    }

    val iconReference =
        SerializableFileResourceReference("favicon", iconPath)
    val brandReference =
        SerializableFileResourceReference("brand", brandPath)

    private var panelToLoad: RIAPanel = startPanel
    private lateinit var wrapperContainer: WebMarkupContainer
    private lateinit var navBar: RIANavbar
    private val loader: Loader = Loader("loader", false)

    private val refreshContainer = object : WebMarkupContainer("outerWrapper") {
        init {
            outputMarkupId = true
        }

        override fun onBeforeRender() {
            super.onBeforeRender()
            addOrReplace(setupWrapperContainer())
        }
    }

    private val buttons: MutableSet<RIANavbarButton> = mutableSetOf()

    init {
        setupWrapperContainer()
    }

    override fun onInitialize() {
        super.onInitialize()
        add(Label("title", translator.translate(title)))
    }

    override fun renderHead(response: IHeaderResponse) {
        super.renderHead(response)
        CSSLoader.load(application as WebApplication, response, "/static/css/sidebar.css")
        CSSLoader.load(application as WebApplication, response, "/static/css/buttons.css")
        renderFavIcon(response)
    }

    private fun renderFavIcon(response: IHeaderResponse) {

        val faviconUrl = urlFor(iconReference, null).toString()

        println("Favicon URL = $faviconUrl")
        response.render(
            MetaDataHeaderItem
                .forLinkTag("icon", faviconUrl)
                .addTagAttribute("type", "image/vnd.microsoft.icon")
                .addTagAttribute("sizes", "any")
        )

        response.render(
            MetaDataHeaderItem
                .forLinkTag("icon", urlFor(iconReference, null).toString())
                .addTagAttribute("type", "image/x-icon")
        )
    }

    private fun setupWrapperContainer(): WebMarkupContainer {
        return object : WebMarkupContainer("wrapper") {
            init {
                outputMarkupId = true
                addOrReplace(getLazyloader(PANEL_ID, this, panelToLoad))
            }
        }
    }

    fun registerPanels(panel: KClass<RIAPanel>, vararg parameters: Any) {
        buttons.add(
            RIANavbarButton(
                panel.simpleName!!,
                translator,
                this,
                panel.constructors.first { it.parameters.size.equals(parameters.size) }.call(parameters)
            )
        )
    }

    fun registerPanel(name: String, panel: RIAPanel) {
        buttons.add(RIANavbarButton(name, translator, this, panel))
    }

    fun registerPanels(vararg panel: Pair<String, RIAPanel>) {
        panel.forEach { registerPanel(it.first, it.second) }
    }


    protected fun newNavbar(): RIANavbar {
        val navbar =
            RIANavbar(
                "navbar",
                applicationVersion
            )
        navbar.position = Navbar.Position.STICKY_TOP
        when (navbarOrientation) {
            NavbarOrientation.VERTICAL -> navbar.add(VerticalNavbarProvider(navbarType))
            else -> navbar.add(HorizontalNavbarProvider(navbarType))
        }
        navbar.setBrandName(Model.of(translator.translate(brandName)))
        navbar.setBrandImage(brandReference, Model.of(StringUtils.EMPTY))
//        val navbarButtons: Array<NavbarButton<*>> = createNavBarButtons()
        navbar.addComponents(NavbarComponents.transform(Navbar.ComponentPosition.LEFT, *buttons.toTypedArray()))
        navbar.outputMarkupId = true
        return navbar
    }

    protected open fun updateAjaxAttributes(attributes: AjaxRequestAttributes) {
        attributes.ajaxCallListeners.add(AjaxCallListener().onBeforeSend("start();").onComplete("finish();"))
    }

    internal fun updatePanel(panel: RIAPanel, target: AjaxRequestTarget) {
        loader.activate(target)
        refreshContainer.remove(panelToLoad)
        panelToLoad = panel
        target.add(*getRefreshTarget())
    }

    internal fun activateLoader(target: AjaxRequestTarget) {
        loader.activate(target)
    }

    internal fun isPanelCurrent(panel: RIAPanel): Boolean {
        return panelToLoad::class.isInstance(panel)
    }

    private fun getRefreshTarget(): Array<Component> {
        return arrayOf(navBar, refreshContainer)
    }

    override fun onBeforeRender() {
        super.onBeforeRender()
        navBar = newNavbar()
        addOrReplace(navBar, loader, refreshContainer)
    }

    private fun getLazyloader(id: String, container: WebMarkupContainer, panel: RIAPanel): Component {
        return object : AjaxLazyLoadPanel<RIAPanel>(id) {

            override fun getLoadingComponent(id: String): Component {
                return Label(id, "")
            }

            override fun onContentLoaded(content: RIAPanel, target: Optional<AjaxRequestTarget>) {
                super.onContentLoaded(content, target)
                this.addOrReplace(content)
                target.ifPresent {
                    it.add(container)
                    loader.deactivate(it)
                }
            }

            override fun getLazyLoadComponent(id: String): RIAPanel {
                return panel.changeId(id)
            }
        }
    }

    fun reload(target: AjaxRequestTarget) {
        target.add(this)
    }
}