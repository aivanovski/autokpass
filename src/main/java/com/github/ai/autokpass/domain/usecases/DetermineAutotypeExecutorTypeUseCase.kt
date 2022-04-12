package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.domain.Errors.FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.OSType
import com.github.ai.autokpass.model.Result

class DetermineAutotypeExecutorTypeUseCase {

    fun getAutotypeExecutorType(
        osType: OSType?,
        autotypeFromArgs: AutotypeExecutorType?
    ): Result<AutotypeExecutorType> {
        return when {
            autotypeFromArgs != null -> Result.Success(autotypeFromArgs)
            osType != null -> Result.Success(getExecutorTypeByOsType(osType))
            else -> Result.Error(AutokpassException(FAILED_TO_DETERMINE_AUTOTYPE_EXECUTOR_TYPE))
        }
    }

    private fun getExecutorTypeByOsType(osType: OSType): AutotypeExecutorType {
        return when (osType) {
            OSType.LINUX -> AutotypeExecutorType.XDOTOOL
            OSType.MAC_OS -> AutotypeExecutorType.CLICLICK
        }
    }
}