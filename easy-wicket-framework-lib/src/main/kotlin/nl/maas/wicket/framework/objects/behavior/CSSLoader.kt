package nl.maas.wicket.framework.objects.behavior

import org.apache.wicket.markup.head.CssReferenceHeaderItem
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.request.resource.CssResourceReference

class CSSLoader private constructor(
    val application: WebApplication,
    val response: IHeaderResponse,
    val resourceLocation: String,
    val filename: String = resourceLocation.substringAfterLast("/")
) {

    companion object {
        fun load(application: WebApplication, response: IHeaderResponse, resourceLocation: String) {
            CSSLoader(application, response, resourceLocation).load()
        }
    }

    protected fun load() {
        val style = CssResourceReference(this::class.java, resourceLocation)
        application.mountResource("css/${filename}", style)
        response.render(CssReferenceHeaderItem.forUrl("css/${filename}"))
    }

}