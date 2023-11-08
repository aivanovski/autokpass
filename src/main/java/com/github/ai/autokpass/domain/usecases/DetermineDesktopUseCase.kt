package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.model.DesktopType
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.util.StringUtils.NEW_LINE

class DetermineDesktopUseCase(
    private val processExecutor: ProcessExecutor
) {

    fun getDesktopType(): Result<DesktopType> {
        val environmentOutputResult = processExecutor.execute(COMMAND)
        if (environmentOutputResult.isFailed()) {
            return environmentOutputResult.asErrorOrThrow()
        }

        val environment = environmentOutputResult.getDataOrThrow()
            .parseEnvironmentOutput()

        return Result.Success(classifyDesktopEnvironment(environment))
    }

    private fun String.parseEnvironmentOutput(): Map<String, String> {
        return this.split(NEW_LINE)
            .map { line -> line.trim() }
            .filter { line -> line.isNotEmpty() && line.contains("=") }
            .mapNotNull { line ->
                val values = line.split("=")
                if (values.size == 2) {
                    values[0] to values[1]
                } else {
                    null
                }
            }
            .toMap()
    }

    private fun classifyDesktopEnvironment(
        environment: Map<String, String>
    ): DesktopType {
        val typeByXdgSession = classifyByXdgSessionTypeVariable(environment)
        if (typeByXdgSession != null) {
            return typeByXdgSession
        }

        val typeByDesktopSession = classifyByDesktopSessionVariable(environment)
        if (typeByDesktopSession != null) {
            return typeByDesktopSession
        }

        return classifyByOthers(environment)
    }

    private fun classifyByXdgSessionTypeVariable(
        environment: Map<String, String>
    ): DesktopType? {
        val xdgSessionType = environment[XDG_SESSION_TYPE] ?: return null

        for (type in TYPES) {
            if (xdgSessionType.contains(type.key, ignoreCase = true)) {
                return type.value
            }
        }

        return null
    }

    private fun classifyByDesktopSessionVariable(
        environment: Map<String, String>
    ): DesktopType? {
        val desktopSession = environment[DESKTOP_SESSION] ?: return null

        for (type in TYPES) {
            if (desktopSession.contains(type.key, ignoreCase = true)) {
                return type.value
            }
        }

        return null
    }

    private fun classifyByOthers(
        environment: Map<String, String>
    ): DesktopType {
        val isWayland = environment.any { (_, value) ->
            value.contains(WAYLAND, ignoreCase = true)
        }

        return if (isWayland) {
            DesktopType.WAYLAND
        } else {
            DesktopType.XORG
        }
    }

    companion object {
        const val COMMAND = "env"
        private const val WAYLAND = "wayland"

        private val TYPES = mapOf(
            "wayland" to DesktopType.WAYLAND,
            "x11" to DesktopType.XORG,
            "xorg" to DesktopType.XORG
        )

        const val XDG_SESSION_TYPE = "XDG_SESSION_TYPE"
        const val DESKTOP_SESSION = "DESKTOP_SESSION"
    }
}