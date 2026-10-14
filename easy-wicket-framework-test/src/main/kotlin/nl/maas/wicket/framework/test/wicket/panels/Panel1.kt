package nl.maas.wicket.framework.test.wicket.panels

import nl.maas.framework.io.utils.AppDirectories
import nl.maas.framework.io.utils.FileUtils
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.panels.RIAPanel
import nl.maas.wicket.framework.test.wicket.caches.ModelCache
import org.apache.wicket.Component
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.spring.injection.annot.SpringBean
import kotlin.io.path.readText

class Panel1 : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache
    private val appName = "wicket-test"


    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createContent())
    }

    private fun createContent(): Component {
        return DynamicPanel("content").addRows(
            "Row" to intArrayOf(12), "Row2" to intArrayOf(12)
        )
            .addOrReplaceComponentToColumn("Row", 0, Label(DynamicPanel.ROW_CONTENT_ID, "Panel 1"))
            .addOrReplaceComponentToColumn(
                "Row2", columnIndex = 0, Label(
                    DynamicPanel.ROW_CONTENT_ID, FileUtils.findFileOrFolderInAnyOf(
                        "tmp.txt",
                        AppDirectories.appDir(appName),
                        AppDirectories.dataDir(appName),
                        AppDirectories.cacheDir(appName)
                    )?.readText()
                        ?: "Je hebt me niet gevonden!!!"
                )
            )
    }


    override fun isAvailable(): Boolean {
        return true
    }


}