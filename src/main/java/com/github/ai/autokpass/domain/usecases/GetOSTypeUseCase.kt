package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources

class GetOSTypeUseCase(
    private val propertyProvider: SystemPropertyProvider,
    private val strings: StringResources
) {

    fun getOSType(): Result<OSType> {
        val osName = propertyProvider.getSystemProperty(PROPERTY_OS_NAME)
        val osType = OS_TYPES[osName] ?: return Result.Error(
            AutokpassException(strings.errorFailedToDetermineOsType)
        )

        return Result.Success(osType)
    }

    companion object {

        const val PROPERTY_OS_NAME = "os.name"

        private val OS_TYPES = mapOf(
            "Linux" to OSType.LINUX,
            "Mac OS X" to OSType.MAC_OS
        )
    }
}