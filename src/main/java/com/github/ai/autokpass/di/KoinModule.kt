package com.github.ai.autokpass.di

import com.github.ai.autokpass.domain.Interactor
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.process.ProcessExecutor
import com.github.ai.autokpass.domain.process.JprocProcessExecutor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.XdotoolAutotypeExecutor
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.input.InputReader
import com.github.ai.autokpass.domain.input.SecretInputReader
import com.github.ai.autokpass.domain.input.StandardInputReader
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.domain.printer.StandardOutputPrinter
import com.github.ai.autokpass.domain.selector.Fzf4jOptionSelector
import com.github.ai.autokpass.domain.selector.OptionSelector
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.GetAllEntriesUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.model.InputReaderType
import com.github.ai.autokpass.model.ParsedArgs
import org.koin.core.qualifier.named
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Printer> { StandardOutputPrinter() }
        single { AutotypeSequenceFactory() }
        single { AutotypePatternParser() }
        single { AutotypePatternFormatter() }
        single { ArgumentExtractor() }
        single { ArgumentParser() }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single { ErrorInteractor(get()) }
        single<EntryFormatter> { DefaultEntryFormatter() }
        single<AutotypeExecutor> { XdotoolAutotypeExecutor(get()) }
        single<OptionSelector> { Fzf4jOptionSelector() }

        single<InputReader>(named(InputReaderType.STANDARD.name)) { StandardInputReader() }
        single<InputReader>(named(InputReaderType.SECRET.name)) { SecretInputReader() }

        single(named(InputReaderType.STANDARD.name)) {
            ReadPasswordUseCase(
                get(), get(qualifier = named(InputReaderType.STANDARD.name))
            )
        }
        single(named(InputReaderType.SECRET.name)) {
            ReadPasswordUseCase(
                get(), get(qualifier = named(InputReaderType.SECRET.name))
            )
        }

        // use cases
        single { GetAllEntriesUseCase() }
        single { AutotypeUseCase(get(), get(), get()) }
        single { SelectEntryUseCase(get(), get(), get()) }
        single { SelectPatternUseCase(get(), get(), get()) }

        factory { (args: ParsedArgs) ->
            Interactor(
                get(qualifier = named(args.inputReaderType.name)),
                get(),
                get(),
                get(),
                get()
            )
        }
    }
}