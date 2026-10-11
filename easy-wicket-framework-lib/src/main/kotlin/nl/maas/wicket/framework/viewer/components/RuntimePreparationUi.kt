package nl.maas.wicket.framework.viewer.components

import nl.maas.wicket.framework.services.RuntimeProgressHandler

interface RuntimePreparationUi {

    val progressHandler: RuntimeProgressHandler

    fun show()

    fun close()

    fun failed(throwable: Throwable)
}