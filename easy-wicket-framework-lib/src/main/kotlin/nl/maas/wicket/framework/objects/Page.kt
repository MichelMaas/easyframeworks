package nl.maas.wicket.framework.objects

data class Page(
    val labels: Map<String, String>,
    val name: String
) : java.io.Serializable