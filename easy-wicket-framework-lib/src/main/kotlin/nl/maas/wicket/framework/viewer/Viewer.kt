package nl.maas.wicket.framework.viewer


abstract class Viewer protected constructor() {

    protected var headless: Boolean = false

    abstract fun startBrowser(
        url: String = "http://localhost:8080"
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
