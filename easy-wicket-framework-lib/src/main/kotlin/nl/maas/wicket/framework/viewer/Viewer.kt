package nl.maas.wicket.framework.viewer


abstract class Viewer protected constructor(
    protected val headless: Boolean = false,
    var icon: String = "/icon.png"
) {

    private var closeRequestHandler: (() -> Unit)? = null

    fun onCloseRequest(handler: () -> Unit) {
        closeRequestHandler = handler
    }

    protected fun fireCloseRequest() {
        closeRequestHandler?.invoke()
    }

    abstract fun startBrowser(
        url: String = "http://localhost:8080", appName: String = "App"
    )

    abstract fun close()

    open fun reload() {
        // Implementaties kunnen dit ondersteunen.
    }

}
