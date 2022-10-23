package com.github.ai.autokpass.di

import com.github.ai.autokpass.data.file.DefaultFileSystemProvider
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactoryProvider
import com.github.ai.autokpass.domain.Interactor
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.presentation.process.JprocProcessExecutor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.presentation.printer.StandardOutputPrinter
import com.github.ai.autokpass.presentation.selector.Fzf4jOptionSelector
import com.github.ai.autokpass.presentation.selector.OptionSelector
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetVisibleEntriesUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ProcessKeyUseCase
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.domain.usecases.ReadPasswordUseCase
import com.github.ai.autokpass.domain.usecases.SelectEntryUseCase
import com.github.ai.autokpass.domain.usecases.SelectPatternUseCase
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.domain.window.XdotoolFocusedWindowProvider
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.input.InputReaderFactory
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Printer> { StandardOutputPrinter() }
        single<FileSystemProvider> { DefaultFileSystemProvider() }
        single { AutotypeSequenceFactory() }
        single { InputReaderFactory() }
        single { AutotypePatternParser() }
        single { AutotypePatternFormatter() }
        single { ArgumentExtractor() }
        single { ArgumentParser(get()) }
        single { ThreadThrottler() }
        single { SystemPropertyProvider() }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single { ErrorInteractor(get()) }
        single<EntryFormatter> { DefaultEntryFormatter() }
        single<OptionSelector> { Fzf4jOptionSelector() }
        single<FocusedWindowProvider> { XdotoolFocusedWindowProvider(get()) }
        single { AutotypeExecutorFactory(get(), get()) }

        // use cases
        single { PrintGreetingsUseCase(get()) }
        single { ReadDatabaseUseCase(get()) }
        single { GetVisibleEntriesUseCase(get()) }
        single { AutotypeUseCase(get(), get(), get(), get()) }
        single { SelectEntryUseCase(get(), get(), get()) }
        single { SelectPatternUseCase(get(), get()) }
        single { AwaitWindowChangeUseCase(get(), get(), get()) }
        single { GetOSTypeUseCase(get()) }
        single { DetermineAutotypeExecutorTypeUseCase() }
        single { ProcessKeyUseCase(get(), get()) }
        single { KeepassDatabaseFactoryProvider(get()) }
        single { ReadPasswordUseCase(get(), get(), get()) }

        factory { (args: ParsedArgs) -> // TODO: refactor to single {}
            Interactor(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
            )
        }
    }
}