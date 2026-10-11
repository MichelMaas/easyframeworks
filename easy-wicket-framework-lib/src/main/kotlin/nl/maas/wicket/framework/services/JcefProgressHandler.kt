package nl.maas.wicket.framework.services

import me.friwi.jcefmaven.EnumProgress
import me.friwi.jcefmaven.IProgressHandler
import nl.maas.wicket.framework.objects.enums.RuntimeRequirement

class JcefProgressHandler(
    private val delegate: RuntimeProgressHandler
) : IProgressHandler {

    override fun handleProgress(
        state: EnumProgress,
        percent: Float
    ) {
        val message = when (state) {
            EnumProgress.LOCATING ->
                "Installatie controleren..."

            EnumProgress.DOWNLOADING ->
                "Browser downloaden..."

            EnumProgress.EXTRACTING ->
                "Browser uitpakken..."

            EnumProgress.INSTALL ->
                "Browser installeren..."

            EnumProgress.INITIALIZING ->
                "Browser initialiseren..."

            EnumProgress.INITIALIZED ->
                "Browser gereed"
        }

        delegate.progress(
            RuntimeRequirement.JCEF,
            percent
                .takeIf { it >= 0 }
                ?.toInt()
                ?.coerceIn(0, 100) ?: 0,
            message
        )
    }
}