package com.github.ai.autokpass.presentation.process

import com.github.ai.autokpass.extensions.splitIntoCommandAndArgs
import org.buildobjects.process.ProcBuilder
import com.github.ai.autokpass.model.Result

class JprocProcessExecutor : ProcessExecutor {

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
            Result.Error(exception)
        }
    }
}