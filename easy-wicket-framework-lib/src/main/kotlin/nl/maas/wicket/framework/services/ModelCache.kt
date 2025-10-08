package nl.maas.wicket.framework.services

interface ModelCache:java.io.Serializable {

    fun refresh()

    fun isEmpty(): Boolean

}