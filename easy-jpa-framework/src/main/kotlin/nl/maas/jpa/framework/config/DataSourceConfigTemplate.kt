package nl.maas.jpa.framework.config

import org.hibernate.jpa.HibernatePersistenceProvider
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableTransactionManagement
abstract class DataSourceConfigTemplate(
    val dbURL: String,
    val dbUser: String,
    val dbPass: String,
    val dbType: DB_TYPES = DB_TYPES.MYSQL8,
    val showSQL: Boolean = true,
    vararg val packagesToScan: String
) {

    constructor(dbProperties: DBProperties) : this(
        dbProperties.dbURL,
        dbProperties.dbUser,
        dbProperties.dbPass,
        dbProperties.dbType,
        dbProperties.showSQL,
        *dbProperties.packagesToScan
    )

    companion object {
        enum class DB_TYPES(val driver: String, val dialect: String) {
            MYSQL8("com.mysql.cj.jdbc.Driver", "org.hibernate.dialect.MySQL8Dialect")
        }
    }


    @Bean
    fun dataSource(): DataSource {
        return DataSourceBuilder.create().driverClassName(dbType.driver)
            .url(dbURL)
            .username(dbUser)
            .password(dbPass)
            .build()
    }

    @Bean
    fun jpaTransactionManager(): JpaTransactionManager {
        val jpaTransactionManager = JpaTransactionManager()
        jpaTransactionManager.entityManagerFactory = entityManagerFactory().`object`
        return jpaTransactionManager
    }

    @Bean
    fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
        entityManagerFactoryBean.jpaVendorAdapter = vendorAdaptor()
        entityManagerFactoryBean.dataSource = dataSource()
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider::class.java)
        entityManagerFactoryBean.setPackagesToScan(*packagesToScan)
        entityManagerFactoryBean.setJpaProperties(addProperties())
        return entityManagerFactoryBean
    }

    private fun vendorAdaptor(): HibernateJpaVendorAdapter {
        return HibernateJpaVendorAdapter()
    }

    private fun addProperties(): Properties {
        val properties = Properties()
        properties.setProperty("hibernate.hbm2ddl.auto", "update")
        properties.setProperty("hibernate.dialect", dbType.dialect)
        properties.setProperty("hibernate.show_sql", showSQL.toString())
        properties.setProperty("hibernate.format_sql", showSQL.toString())
        return properties
    }
}