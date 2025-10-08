package nl.maas.jpa.framework.config

class DBProperties(
    val dbURL: String,
    val dbUser: String,
    val dbPass: String,
    val dbType: DataSourceConfigTemplate.Companion.DB_TYPES = DataSourceConfigTemplate.Companion.DB_TYPES.MYSQL8,
    val showSQL: Boolean = true,
    vararg val packagesToScan: String
) {
}