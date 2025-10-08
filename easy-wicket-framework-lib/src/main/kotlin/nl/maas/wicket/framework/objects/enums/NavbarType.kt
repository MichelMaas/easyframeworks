package nl.maas.wicket.framework.objects.enums

enum class NavbarType {

    LIGHT,
    DARK,
    PRIMARY,
    SECUNDARY;

    val makeup get() = "navbar-${name.lowercase()} bg-light border border-${name.lowercase()}"
}