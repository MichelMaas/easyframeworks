package nl.maas.wicket.framework.services

import com.vaadin.open.Docker
import com.vaadin.open.FileUtil
import com.vaadin.open.OSUtils
import com.vaadin.open.Options
import nl.maas.wicket.framework.objects.enums.App
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Function
import java.util.regex.Pattern
import java.util.stream.Collectors


abstract class BrowserManager {

    private val wslMountPoint: String? = null

    init {
        onInit()
    }

    abstract fun onInit()

    /**
     * Get the mount point for fixed drives in WSL.
     *
     * @inner
     * @returns {string} The mount point.
     */
    private fun getWslDrivesMountPoint(): String? {
        // Default value for "root" param
        // according to https://docs.microsoft.com/en-us/windows/wsl/wsl-config
        if (wslMountPoint != null) {
            return wslMountPoint
        }
        val defaultMountPoint = "/mnt/"
        val configFilePath = "/etc/wsl.conf"
        val configFile = File(configFilePath)
        val isConfigFileExists = configFile.exists()
        return if (!isConfigFileExists) {
            defaultMountPoint
        } else try {
            val configContent = FileUtil.readFile(configFile)
            val p = Pattern.compile("(?<!#.*)root\\s*=\\s*(?<mountPoint>.*)")
            val matcher = p.matcher(configContent)
            if (!matcher.matches()) {
                return defaultMountPoint
            }
            val mountPoint = matcher.group("mountPoint").trim { it <= ' ' }
            if (mountPoint.endsWith("/")) mountPoint else "$mountPoint/"
        } catch (e: IOException) {
            e.printStackTrace()
            defaultMountPoint
        }
    }

    /**
     * Opens the given URL.
     *
     * @param target the URL to open
     * @return true if the URL was launched
     */
    fun open(target: String?): Process? {
        return open(target, Options())
    }

    /**
     * Opens the given URL using the given options.
     *
     * @param target  the URL to open
     * @param options the options to use
     * @return true if the URL was launched
     */
    fun open(target: String?, options: Options = Options()): Process? {
        return doOpen(target, null, ArrayList<String>(), options)
    }

    /**
     * Opens the given URL in the given application.
     *
     * @param target the URL to open
     * @param app    the application to use
     * @return true if the URL was launched
     */
    fun open(target: String?, app: App = App.CHROME): Process? {
        return open(target, app, Options()).firstOrNull()
    }

    /**
     * Opens the given URL in the given application using the given options.
     *
     * @param target  the URL to open
     * @param app     the application to use
     * @param options the options to use
     * @return true if the URL was launched
     */
    fun open(target: String?, app: App, options: Options = Options()): List<Process> {
        return app.app.stream().map<Process>(Function<String, Process> { name: String? ->
            doOpen(
                target,
                name,
                mutableListOf(),
                options
            )!!
        }).collect(Collectors.toList<Process>())
    }

    /**
     * Opens the given application using the given arguments and options.
     *
     * @param name         the name of the application to open
     * @param appArguments the arguments to pass to the application
     * @param options      the options to use
     * @return true if the URL was launched
     */
    fun openApp(
        name: String = "chrome",
        appArguments: List<String> = listOf(),
        options: Options = Options()
    ): Process? {
        return doOpen(null, name, appArguments.toMutableList(), options)
    }

    /**
     * Opens the given application using the given arguments and options.
     *
     * @param app          the application to open
     * @param appArguments the arguments to pass to the application
     * @param options      the options to use
     * @return true if the URL was launched
     */
    fun openApp(
        app: App = App.CHROME,
        appArguments: List<String>,
        options: Options = Options()
    ): kotlin.collections.List<Process> {
        return app.app.map { name -> doOpen(null, name, appArguments.toMutableList(), options) }.filterNotNull()
    }

    private fun doOpen(
        target: String?,
        app: String?,
        appArguments: MutableList<String>,
        options: Options = Options()
    ): Process? {
        var target: String? = target
        var command: String
        val cliArguments: MutableList<String> = java.util.ArrayList()
        var consumeOutput = false
        if (OSUtils.isMac()) {
            command = "open"
            if (options.isWait) {
                cliArguments.add("--wait-apps")
            }
            if (options.isBackground) {
                cliArguments.add("--background")
            }
            if (options.isNewInstance) {
                cliArguments.add("--new")
            }
            if (app != null) {
                cliArguments.add("-a")
                cliArguments.add(app)
            }
        } else if (OSUtils.isWindows() || OSUtils.isWsl() && !Docker.isDocker()) {
            val isWsl = OSUtils.isWsl()
            // Windows or WSL on windows
            val mountPoint = getWslDrivesMountPoint()
            command = if (isWsl) {
                mountPoint + "c/Windows/System32/WindowsPowerShell/v1.0/powershell.exe"
            } else {
                System.getenv("SYSTEMROOT") + "\\System32\\WindowsPowerShell\\v1.0\\powershell"
            }
            cliArguments.addAll(
                Arrays.asList("-NoProfile", "-NonInteractive", "–ExecutionPolicy", "Bypass", "-EncodedCommand")
            )
            val encodedArguments: MutableList<String> = java.util.ArrayList()
            encodedArguments.add("Start")
            if (options.isWait) {
                encodedArguments.add("-Wait")
            }
            if (app != null) {
                // Double quote with double quotes to ensure the inner quotes are passed
                // through.
                // Inner quotes are delimited for PowerShell interpretation with backticks.
                encodedArguments.add("\"`\"$app`\"\"")
                encodedArguments.add("-ArgumentList")
                if (target != null) {
                    appArguments.add(0, target)
                }
            } else if (target != null) {
                encodedArguments.add("\"" + target + "\"")
            }
            if (!appArguments.isEmpty()) {
                val joinedArgs = appArguments.stream().map { arg: String -> "\"`\"$arg`\"\"" }
                    .collect(Collectors.joining(","))
                encodedArguments.add(joinedArgs)
            }
            // Using Base64-encoded command, accepted by PowerShell, to allow special
            // characters.
            target = Base64.getEncoder().encodeToString(
                encodedArguments.stream().collect(Collectors.joining(" ")).toByteArray(StandardCharsets.UTF_16LE)
            )
        } else {
            if (app != null) {
                command = app
            } else {
                // Use system xdg-open if we cannot use the included one
                command = "xdg-open"

                // Use bundled xdg-open
                val bundledXdgOpen = BrowserManager::class.java.getResourceAsStream("xdg-open")
                try {
                    val open = File.createTempFile("xdg", "open")
                    open.setExecutable(true)
                    open.deleteOnExit()
                    FileOutputStream(open).use { out ->
                        FileUtil.copy(bundledXdgOpen, out)
                        command = open.absolutePath
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            if (!appArguments.isEmpty()) {
                cliArguments.addAll(appArguments)
            }
            if (!options.isWait) {
                // `xdg-open` will block the process unless stdio is ignored
                // and it"s detached from the parent even if it"s unref"d.
                consumeOutput = true
            }
        }
        if (target != null) {
            cliArguments.add(target)
        }
        if (OSUtils.isMac() && appArguments.size > 0) {
            cliArguments.add("--args")
            cliArguments.addAll(appArguments)
        }
        cliArguments.add(0, command)
        var subprocess: Process? = null
        try {
            subprocess = ProcessBuilder().command(cliArguments).start()
            if (options.isWait) {
                try {
                    val exitCode = subprocess.waitFor()
                    if (!options.isAllowNonzeroExitCode && exitCode > 0) {
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } finally {
                    return subprocess
                }
            } else {
                if (consumeOutput) {
                    FileUtil.read(subprocess.inputStream)
                }

                // Give it a little time to realize the command might not be found
                for (i in 0..4) {
                    if (!subprocess.isAlive) {
                        if (subprocess.exitValue() != 0) {
                            return subprocess
                        }
                    }
                    try {
                        Thread.sleep(50)
                    } catch (e: InterruptedException) {
                    }
                }
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
            return null
        }
        return subprocess
    }

}