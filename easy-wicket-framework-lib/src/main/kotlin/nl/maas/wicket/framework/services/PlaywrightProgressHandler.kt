package nl.maas.wicket.framework.services

import nl.maas.wicket.framework.objects.enums.RuntimeRequirement

class PlaywrightProgressHandler(
    private val delegate: RuntimeProgressHandler
) {

    private val percentRegex =
        Regex("""\|\s*(\d{1,3})%\s+of""")

    private var currentComponent =
        "Playwright Chromium"

    fun handle(line: String) {
        val text = line.trim()

        when {
            text.startsWith("Downloading ") -> {
                currentComponent =
                    determineComponent(text)

                delegate.progress(
                    RuntimeRequirement.PLAYWRIGHT,
                    0,
                    "$currentComponent downloaden..."
                )
            }

            percentRegex.containsMatchIn(text) -> {
                val percent =
                    percentRegex
                        .find(text)
                        ?.groupValues
                        ?.get(1)
                        ?.toIntOrNull()

                delegate.progress(
                    RuntimeRequirement.PLAYWRIGHT,
                    percent,
                    "$currentComponent downloaden..."
                )
            }

            text.contains(" downloaded to ") -> {
                delegate.progress(
                    RuntimeRequirement.PLAYWRIGHT,
                    100,
                    "$currentComponent gedownload"
                )
            }
        }
    }

    private fun determineComponent(
        line: String
    ): String =
        when {
            line.startsWith(
                "Downloading Chrome Headless Shell"
            ) ->
                "Chromium headless browser"

            line.startsWith(
                "Downloading Chrome for Testing"
            ) ->
                "Chromium browser"

            line.startsWith(
                "Downloading FFmpeg"
            ) ->
                "FFmpeg"

            line.startsWith(
                "Downloading Firefox"
            ) ->
                "Firefox"

            line.startsWith(
                "Downloading WebKit"
            ) ->
                "WebKit"

            else ->
                "Playwright onderdeel"
        }
}