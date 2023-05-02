package com.github.ai.autokpass.presentation.process

import com.github.ai.autokpass.domain.exception.NoMacOsAccessibilityPermissionException
import com.github.ai.autokpass.extensions.splitIntoCommandAndArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import org.buildobjects.process.ExternalProcessFailureException
import org.buildobjects.process.ProcBuilder

class JprocProcessExecutor(
    private val strings: StringResources
) : ProcessExecutor {

    override fun execute(command: String): Result<String> {
        val (com, arguments) = command.splitIntoCommandAndArgs()

        return execute(
            input = null,
            command = com,
            arguments = arguments
        )
    }

    override fun executeWithBash(command: String): Result<String> {
        return execute(
            input = null,
            command = "bash",
            arguments = listOf("-c", command)
        )
    }

    override fun execute(input: ByteArray, command: String): Result<String> {
        val (com, arguments) = command.splitIntoCommandAndArgs()

        return execute(
            input = input,
            command = com,
            arguments = arguments
        )
    }

    private fun execute(
        input: ByteArray?,
        command: String,
        arguments: List<String>
    ): Result<String> {
        return try {
            val builder = ProcBuilder(command, *arguments.toTypedArray())

            if (input != null) {
                builder.withInput(input)
            }

            Result.Success(builder.run().outputString)
        } catch (exception: Exception) {
            if (exception is ExternalProcessFailureException &&
                exception.message?.contains(NO_PERMISSION_MESSAGE, ignoreCase = true) == true
            ) {
                Result.Error(NoMacOsAccessibilityPermissionException(strings))
            } else {
                Result.Error(exception)
            }
        }
    }

    companion object {
        private const val NO_PERMISSION_MESSAGE = "not allowed to send keystrokes"
    }
}