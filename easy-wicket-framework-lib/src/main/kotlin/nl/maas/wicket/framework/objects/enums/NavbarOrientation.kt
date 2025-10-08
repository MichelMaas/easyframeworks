package nl.maas.wicket.framework.objects.enums

enum class NavbarOrientation(private val makeUp_: String) {
    HORIZONTAL("border-bottom-0 border-top border-end-0 border-start-0 rounded-bottom"),
    VERTICAL("border-bottom-0 border-top-0 border-end border-start-0 rounded-end");

    val makeUp = "border-5 ${makeUp_}"
}