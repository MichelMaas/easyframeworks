package nl.maas.framework.torrent.rest.client

import java.net.Authenticator
import java.net.PasswordAuthentication

class BaseAuthenticator(private val name: String, private val pass: String) : Authenticator() {

    override fun getPasswordAuthentication(): PasswordAuthentication {
        return PasswordAuthentication(name, pass.toCharArray())
    }
}