package nl.maas.wicket.framework.test.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension
import nl.maas.wicket.framework.config.AbstractBootstrapConfig
import nl.maas.wicket.framework.config.AbstractBootstrapProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ApplicationInitExtension
@EnableConfigurationProperties(
    BootstrapProperties::class
)
open class BootstrapConfig(@Autowired prop: AbstractBootstrapProperties) : AbstractBootstrapConfig(prop) {

}