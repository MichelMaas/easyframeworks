package nl.maas.filerenamer.frontend.wicket.config

import com.giffing.wicket.spring.boot.context.extensions.ApplicationInitExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties

@ApplicationInitExtension
@ConditionalOnProperty(prefix = AbstractBootstrapProperties.PROPERTY_PREFIX, value = ["enabled"], matchIfMissing = true)
@ConditionalOnClass(
    BootstrapConfig::class
)
@EnableConfigurationProperties(
    BootstrapProperties::class
)
class BootstrapConfig(@Autowired prop: AbstractBootstrapProperties) : AbstractBootstrapConfig(prop) {

}