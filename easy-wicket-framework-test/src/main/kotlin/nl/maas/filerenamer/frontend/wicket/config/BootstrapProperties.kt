package nl.maas.filerenamer.frontend.wicket.config

import nl.maas.wicket.framework.config.AbstractBootstrapProperties
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = AbstractBootstrapProperties.PROPERTY_PREFIX)
class BootstrapProperties : AbstractBootstrapProperties() {

}