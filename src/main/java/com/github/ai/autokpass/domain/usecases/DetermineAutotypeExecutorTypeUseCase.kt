package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result

class DetermineAutotypeExecutorTypeUseCase(
    private val getOSTypeUseCase: GetOSTypeUseCase
) {

    fun getAutotypeExecutorType(
        autotypeFromArgs: AutotypeExecutorType?
    ): Result<AutotypeExecutorType> {
        val getOsTypeResult = getOSTypeUseCase.getOSType()
        if (getOsTypeResult.isFailed()) {
            return getOsTypeResult.asErrorOrThrow()
        }

        val osType = getOsTypeResult.getDataOrThrow()

        return when {
            autotypeFromArgs != null -> Result.Success(autotypeFromArgs)
            else -> Result.Success(getExecutorTypeByOsType(osType))
        }
    }

    private fun getExecutorTypeByOsType(osType: OSType): AutotypeExecutorType {
        return when (osType) {
            OSType.LINUX -> AutotypeExecutorType.XDOTOOL
            OSType.MAC_OS -> AutotypeExecutorType.OSA_SCRIPT
        }
    }
}