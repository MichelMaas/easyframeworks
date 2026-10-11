package nl.maas.wicket.framework.services

import nl.maas.wicket.framework.objects.enums.RuntimeRequirement

interface RuntimeProgressHandler {
    fun started(
        requirement: RuntimeRequirement,
        message: String
    )

    fun progress(
        requirement: RuntimeRequirement,
        percent: Int,
        message: String? = null
    )

    fun completed(
        requirement: RuntimeRequirement
    )

    fun failed(
        requirement: RuntimeRequirement,
        throwable: Throwable
    )
}