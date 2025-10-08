package nl.maas.wicket.framework.objects.behavior

import org.apache.wicket.Component
import org.apache.wicket.markup.head.IHeaderResponse
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem
import org.apache.wicket.protocol.http.WebApplication
import org.apache.wicket.request.resource.JavaScriptResourceReference
import kotlin.reflect.KClass

class JSLoader private constructor(
    val scope: KClass<out Component>,
    val application: WebApplication,
    val response: IHeaderResponse,
    val resourceLocation: String,
    val filename: String = resourceLocation.substringAfterLast("/")
) {

    companion object {
        fun load(
            scope: KClass<out Component>,
            application: WebApplication,
            response: IHeaderResponse,
            resourceLocation: String
        ) {
            JSLoader(scope, application, response, resourceLocation).load()
        }
    }

    protected fun load() {
        val reference =
            JavaScriptResourceReference(scope.java, resourceLocation)
        application.mountResource("js/$filename", reference)
        response.render(JavaScriptReferenceHeaderItem.forReference(reference))
    }

}