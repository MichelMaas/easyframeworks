package nl.maas.wicket.framework.objects.enums

import com.vaadin.open.OSUtils
import java.util.function.Supplier

enum class App(private val appSupplier: Supplier<MutableList<String>>) {
    CHROME(Supplier {
        val options = mutableListOf<String>()
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files/Google/Chrome/Application/chrome.exe")
            options.add("/mnt/c/Program Files (x86)/Google/Chrome/Application/chrome.exe")
        } else if (OSUtils.isWindows()) {
            options.add("chrome")
        } else if (OSUtils.isMac()) {
            options.add("google chrome")
        } else if (OSUtils.isLinux()) {
            options.add("google-chrome")
            options.add("google-chrome-stable")
            options.add("chromium")
        }
        options
    }),
    FIREFOX(Supplier {
        val options = mutableListOf<String>()
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files/Mozilla Firefox/firefox.exe")
        } else if (OSUtils.isWindows()) {
            options.add("C:\\Program Files\\Mozilla Firefox\\u000cirefox.exe")
        } else if (OSUtils.isMac()) {
            options.add("firefox")
        } else if (OSUtils.isLinux()) {
            options.add("firefox")
        }
        options
    }),
    EDGE(Supplier {
        val options = mutableListOf<String>()
        if (OSUtils.isWsl()) {
            options.add("/mnt/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe")
        } else if (OSUtils.isWindows()) {
            options.add("msedge")
        } else if (OSUtils.isMac()) {
            options.add("microsoft edge")
        } else if (OSUtils.isLinux()) {
            options.add("microsoft-edge")
            options.add("microsoft-edge-dev")
        }
        options
    });

    val app
        get() = appSupplier.get()
}