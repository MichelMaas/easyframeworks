package nl.maas.filerenamer.frontend.wicket.panels

import nl.maas.filerenamer.frontend.wicket.caches.ModelCache
import nl.maas.framework.torrent.rest.client.TransmissionClient
import nl.maas.framework.torrent.transmission.io.TransmissionTorrent
import nl.maas.wicket.framework.components.base.CollapsablePanel
import nl.maas.wicket.framework.components.base.CollapsablePanelGroup
import nl.maas.wicket.framework.components.base.DynamicDataTable
import nl.maas.wicket.framework.components.base.DynamicPanel
import nl.maas.wicket.framework.components.charts.BarChart
import nl.maas.wicket.framework.components.charts.LineChart
import nl.maas.wicket.framework.objects.Tuple
import nl.maas.wicket.framework.panels.RIAPanel
import org.apache.wicket.Component
import org.apache.wicket.spring.injection.annot.SpringBean
import java.net.URI
import java.util.*

class Panel2 : RIAPanel() {

    @SpringBean
    private lateinit var modelCache: ModelCache

    override fun onBeforeRender() {
        super.onBeforeRender()
        addOrReplace(createContent())
    }

    private fun createContent(): Component {
        return CollapsablePanelGroup(
            DynamicPanel.ROW_CONTENT_ID,
            50,
            createCollapsablePanel1(),
            createCollapsablePanel2(),
            createCollapsablePanel3()
        )

    }

    private fun createCollapsablePanel2(): CollapsablePanel {
        val data = modelCache.createLineChartParams()
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Line chart",
            LineChart(CollapsablePanel.CONTENT_ID, "Line", data)
        )
    }

    private fun createCollapsablePanel1(): CollapsablePanel {
        val data = modelCache.createBarChartParams()
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Bar chart",
            BarChart(CollapsablePanel.CONTENT_ID, "bar", data)
        )
    }

    private fun createCollapsablePanel3(): CollapsablePanel {
        val data =
            TransmissionClient(
                URI.create("http://10.0.0.6:8181/transmission/rpc"),
                "michel",
                Base64.getEncoder().encodeToString("llae2215".toByteArray()),
                true
            ).fetchAll()
        return CollapsablePanel(
            CollapsablePanelGroup.CONTENT_ID,
            "Bar chart",
            DynamicDataTable.get(CollapsablePanel.CONTENT_ID, data.map { toTuple(it) }, 15, 70)
        )
    }

    private fun toTuple(it: TransmissionTorrent): Tuple {
        return Tuple(
            listOf(
                "Name" to it.name,
                "Status" to it.getStatusEnum(),
                "Progress" to it.percentComplete
            ).toMap()
        )
    }

    override fun isAvailable(): Boolean {
        return !modelCache.isEmpty()
    }
}