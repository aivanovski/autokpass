package com.github.ai.autokpass.domain.arguments

import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.domain.Errors.GENERIC_EMPTY_ARGUMENT
import com.github.ai.autokpass.domain.Errors.GENERIC_FAILED_TO_PARSE_ARGUMENT
import com.github.ai.autokpass.domain.Errors.GENERIC_FILE_DOES_NOT_EXIST
import com.github.ai.autokpass.domain.Errors.GENERIC_FILE_IS_NOT_A_FILE
import com.github.ai.autokpass.domain.exception.ParsingException
import com.github.ai.autokpass.model.AutotypeExecutorType
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.RawArgs
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.lang.String.format

class ArgumentParserTest {

    @Test
    fun `validateAndParse should return result if --file exists`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
    }

    @Test
    fun `validateAndParse should return error if --file is null`() {
        // arrange
        val args = argsWith(filePath = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_EMPTY_ARGUMENT,
                Argument.FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --file is empty`() {
        // arrange
        val args = argsWith(filePath = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_EMPTY_ARGUMENT,
                Argument.FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --file doesn't exist`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(format(GENERIC_FILE_DOES_NOT_EXIST, FILE_PATH))
    }

    @Test
    fun `validateAndParse should return error if --file is a directory`() {
        // arrange
        val args = argsWith(filePath = FILE_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(format(GENERIC_FILE_IS_NOT_A_FILE, FILE_PATH))
    }

    @Test
    fun `validateAndParse should return result if --key-file exists`() {
        // arrange
        val args = argsWith(keyPath = KEY_PATH)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return result if --key-file is null`() {
        // arrange
        val args = argsWith(keyPath = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --key-file is empty`() {
        // arrange
        val args = argsWith(keyPath = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_EMPTY_ARGUMENT,
                Argument.KEY_FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return error if --key-file doesn't exist`() {
        // arrange
        val args = argsWith(keyPath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(format(GENERIC_FILE_DOES_NOT_EXIST, KEY_PATH))
    }

    @Test
    fun `validateAndParse should return error if --key-file is a directory`() {
        // arrange
        val args = argsWith(filePath = KEY_PATH)
        val fsProvider = mockk<FileSystemProvider>()
        every { fsProvider.exists(FILE_PATH) }.returns(true)
        every { fsProvider.isFile(FILE_PATH) }.returns(true)
        every { fsProvider.exists(KEY_PATH) }.returns(true)
        every { fsProvider.isFile(KEY_PATH) }.returns(false)

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(format(GENERIC_FILE_IS_NOT_A_FILE, KEY_PATH))
    }

    @Test
    fun `validateAndParse should return autotype delay if --autotype-delay specified`() {
        // arrange
        val args = argsWith(autotypeDelayInMillis = DELAY)

        // act
        val result = ArgumentParser(providerForAnyFile()).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is null`() {
        // arrange
        val args = argsWith(autotypeDelayInMillis = null)

        // act
        val result = ArgumentParser(providerForAnyFile()).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return null if --autotype-delay is empty`() {
        // arrange
        val args = argsWith(autotypeDelayInMillis = EMPTY)

        // act
        val result = ArgumentParser(providerForAnyFile()).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.copy(autotypeDelayInMillis = null).toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --autotype-delay is invalid`() {
        // arrange
        val args = argsWith(autotypeDelayInMillis = INVALID_VALUE)

        // act
        val result = ArgumentParser(providerForAnyFile()).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_FAILED_TO_PARSE_ARGUMENT,
                Argument.AUTOTYPE_DELAY.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return delay if --delay specified`() {
        // arrange
        val args = argsWith(delayInSeconds = DELAY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return null if --delay is null`() {
        // arrange
        val args = argsWith(delayInSeconds = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return null if --delay is empty`() {
        // arrange
        val args = argsWith(delayInSeconds = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.copy(delayInSeconds = null).toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --delay is invalid`() {
        // arrange
        val args = argsWith(delayInSeconds = INVALID_VALUE)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_FAILED_TO_PARSE_ARGUMENT,
                Argument.DELAY.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return default value if --input is not specified`() {
        // arrange
        val args = argsWith(inputType = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert

        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(
            args.copy(inputType = InputReaderType.SECRET.cliName).toParsedArgs()
        )
    }

    @Test
    fun `validateAndParse should return result if --input is specified`() {
        // arrange
        val args = argsWith(inputType = InputReaderType.STANDARD.cliName)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --input is invalid`() {
        // arrange
        val args = argsWith(inputType = INVALID_VALUE)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_FAILED_TO_PARSE_ARGUMENT,
                Argument.INPUT.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return null if --autotype is not specified`() {
        // arrange
        val args = argsWith(autotypeExecutorType = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return value if --autotype is specified`() {
        // arrange
        val args = argsWith(autotypeExecutorType = AutotypeExecutorType.XDOTOOL.cliName)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --autotype is invalid`() {
        // arrange
        val args = argsWith(autotypeExecutorType = INVALID_VALUE)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_FAILED_TO_PARSE_ARGUMENT,
                Argument.AUTOTYPE.cliName,
                INVALID_VALUE
            )
        )
    }

    @Test
    fun `validateAndParse should return error if all empty`() {
        // arrange
        val args = argsWith(EMPTY, EMPTY, EMPTY, EMPTY, EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(
                GENERIC_EMPTY_ARGUMENT,
                Argument.FILE.cliName
            )
        )
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is specified`() {
        // arrange
        val args = argsWith(keyProcessingCommand = COMMAND)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return value if --process-key-command is not specified`() {
        // arrange
        val args = argsWith(keyProcessingCommand = null)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isSucceeded()).isTrue()
        assertThat(result.getDataOrThrow()).isEqualTo(args.toParsedArgs())
    }

    @Test
    fun `validateAndParse should return error if --process-key-command is empty`() {
        // arrange
        val args = argsWith(keyProcessingCommand = EMPTY)
        val fsProvider = providerForAnyFile()

        // act
        val result = ArgumentParser(fsProvider).validateAndParse(args)

        // assert
        assertThat(result.isFailed()).isTrue()
        assertThat(result.getExceptionOrThrow()).isInstanceOf(ParsingException::class.java)
        assertThat(result.getExceptionOrThrow().message).isEqualTo(
            format(GENERIC_EMPTY_ARGUMENT, Argument.PROCESS_KEY_COMMAND.cliName)
        )
    }

    private fun argsWith(
        filePath: String? = FILE_PATH,
        keyPath: String? = null,
        delayInSeconds: String? = null,
        autotypeDelayInMillis: String? = null,
        inputType: String? = InputReaderType.SECRET.cliName,
        autotypeExecutorType: String? = AutotypeExecutorType.XDOTOOL.cliName,
        keyProcessingCommand: String? = null
    ): RawArgs {
        return RawArgs(
            filePath = filePath,
            keyPath = keyPath,
            delayInSeconds = delayInSeconds,
            autotypeDelayInMillis = autotypeDelayInMillis,
            inputType = inputType,
            autotypeType = autotypeExecutorType,
            keyProcessingCommand = keyProcessingCommand
        )
    }

    private fun RawArgs.toParsedArgs(): ParsedArgs =
        ParsedArgs(
            filePath = filePath ?: EMPTY,
            keyPath = keyPath,
            delayInSeconds = delayInSeconds?.toLong(),
            autotypeDelayInMillis = autotypeDelayInMillis?.toLong(),
            inputReaderType = InputReaderType.values().first { it.cliName == inputType },
            autotypeType = AutotypeExecutorType.values().firstOrNull { it.cliName == autotypeType },
            keyProcessingCommand = keyProcessingCommand
        )

    private fun providerForAnyFile(): FileSystemProvider {
        val provider = mockk<FileSystemProvider>()

        every { provider.exists(any()) }.returns(true)
        every { provider.isFile(any()) }.returns(true)

        return provider
    }

    companion object {
        private const val FILE_PATH = "/tmp/filePath"
        private const val KEY_PATH = "/tmp/keyPath"
        private const val DELAY = "123"
        private const val INVALID_VALUE = "abc123/_=[]"
        private const val COMMAND = "gpg --decrypt"
    }
}