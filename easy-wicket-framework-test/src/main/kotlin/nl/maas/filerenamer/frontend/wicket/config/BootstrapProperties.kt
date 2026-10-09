package nl.maas.filerenamer.frontend.wicket.config

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = AbstractBootstrapProperties.PROPERTY_PREFIX)
class BootstrapProperties : AbstractBootstrapProperties() {

}