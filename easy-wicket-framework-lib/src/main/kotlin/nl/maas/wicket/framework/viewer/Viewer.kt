package nl.maas.wicket.framework.viewer


abstract class Viewer protected constructor() {

    protected var headless: Boolean = false
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


    companion object {

        private val instance: Viewer by lazy {
            SeleniumViewer
        }

        @JvmStatic
        fun get(headless: Boolean = false): Viewer {
            instance.headless = headless
            return instance
        }
    }
}
