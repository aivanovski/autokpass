package com.github.ai.autokpass.domain.usecases

import com.github.ai.autokpass.Config
import com.github.ai.autokpass.domain.arguments.Argument.AUTOTYPE_SEQUENCE
import com.github.ai.autokpass.domain.arguments.Argument.DELAY
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFormatter
import com.github.ai.autokpass.domain.exception.AutokpassException
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.printer.Printer
import java.util.Base64

class RunItselfUseCase(
    private val sequenceFormatter: AutotypeSequenceFormatter,
    private val printer: Printer
) {

    fun runItself(
        sequence: AutotypeSequence,
        delayInSeconds: Long?
    ): Result<Unit> {
        if (Config.DEBUG) {
            val path = Config::class.java.protectionDomain.codeSource.location.toExternalForm()
            printer.println("class_path=$path")
        }

        val command = when (getLaunchType()) {
            LaunchType.CLASSPATH -> buildLaunchCommandForClasspath(sequence, delayInSeconds)
            LaunchType.JAR -> {
                if (getJarFilePath() == null) {
                    return Result.Error(AutokpassException("Unable to determine jar file path"))
                }

                buildLaunchCommandForJar(sequence, delayInSeconds)
            }
            else -> return Result.Error(AutokpassException("Unable to determine launch environment"))
        }

        if (Config.DEBUG) {
            printer.println("command=$command")
        }

        Runtime.getRuntime().exec(command)

        return Result.Success(Unit)
    }

    private fun buildLaunchCommandForClasspath(
        sequence: AutotypeSequence,
        delayInSeconds: Long?
    ): String {
        val classPath = System.getProperty("java.class.path")

        val encodedSequence = sequenceFormatter.format(sequence)?.let {
            Base64.getEncoder().encodeToString(it.toByteArray())
        }

        return buildString {
            append("java -cp ").append(classPath).append(" com.github.ai.autokpass.MainKt")

            if (delayInSeconds != null) {
                append(" ")
                append(DELAY.cliName).append(" ").append(delayInSeconds)
            }

            append(" ")
            append(AUTOTYPE_SEQUENCE.cliName).append(" ").append(encodedSequence)
        }
    }

    private fun buildLaunchCommandForJar(
        sequence: AutotypeSequence,
        delayInSeconds: Long?
    ): String {
        val jarPath = getJarFilePath()

        val encodedSequence = sequenceFormatter.format(sequence)?.let {
            Base64.getEncoder().encodeToString(it.toByteArray())
        }

        return buildString {
            append("java -jar ").append(jarPath)

            if (delayInSeconds != null) {
                append(" ")
                append(DELAY.cliName).append(" ").append(delayInSeconds)
            }

            append(" ")
            append(AUTOTYPE_SEQUENCE.cliName).append(" ").append(encodedSequence)
        }
    }

    private fun getJarFilePath(): String? {
        val path = Config::class.java.protectionDomain.codeSource.location.toExternalForm()

        val startIdx = PREFIX_FOR_JAR.length
        val endIdx = path.indexOf(".jar", startIdx)

        return if (startIdx < endIdx + 4) {
            path.substring(startIdx, endIdx + 4)
        } else {
            null
        }
    }

    private fun getLaunchType(): LaunchType? {
        val path = Config::class.java.protectionDomain.codeSource.location.toExternalForm()
        return when {
            path.startsWith(PREFIX_FOR_CLASSPATH) -> LaunchType.CLASSPATH
            path.startsWith(PREFIX_FOR_JAR) -> LaunchType.JAR
            else -> null
        }
    }

    private enum class LaunchType {
        CLASSPATH,
        JAR
    }

    companion object {
        private const val PREFIX_FOR_CLASSPATH = "file:"
        private const val PREFIX_FOR_JAR = "jar:file:"
    }
}