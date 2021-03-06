package com.github.ai.autokpass.presentation.process

import com.github.ai.autokpass.extensions.splitIntoCommandAndArgs
import org.buildobjects.process.ProcBuilder

class JprocProcessExecutor : ProcessExecutor {

    override fun execute(command: String): String {
        val (com, args) = command.splitIntoCommandAndArgs()
        return ProcBuilder(com, *args.toTypedArray())
            .run()
            .outputString
    }

    override fun executeWithBash(command: String): String {
        return ProcBuilder("bash", "-c", command)
            .run()
            .outputString
    }

    override fun execute(input: String, command: String): String {
        val (com, args) = command.splitIntoCommandAndArgs()

        return ProcBuilder(com, *args.toTypedArray())
            .withInput(input)
            .run()
            .outputString
    }

    override fun execute(input: ByteArray, command: String): String {
        val (com, args) = command.splitIntoCommandAndArgs()

        return ProcBuilder(com, *args.toTypedArray())
            .withInput(input)
            .run()
            .outputString
    }
}