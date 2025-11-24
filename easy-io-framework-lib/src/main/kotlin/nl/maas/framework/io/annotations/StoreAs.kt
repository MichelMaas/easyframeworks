package nl.maas.framework.io.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StoreAs(val storeAs: String)
