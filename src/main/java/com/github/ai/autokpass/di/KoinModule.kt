package com.github.ai.autokpass.di

import com.github.ai.autokpass.data.file.DefaultFileSystemProvider
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactoryProvider
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.presentation.process.JprocProcessExecutor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.ErrorInteractorImpl
import com.github.ai.autokpass.domain.MainInteractor
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.coroutine.DefaultDispatchers
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.fuzzy_search.FuzzyMatcher
import com.github.ai.autokpass.domain.fuzzy_search.Fzf4jFuzzyMatcher
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.presentation.printer.StandardOutputPrinter
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.AwaitWindowChangeUseCase
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetVisibleEntriesUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.domain.window.XdotoolFocusedWindowProvider
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.root.RootViewModel
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeArgs
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeInteractor
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeViewModel
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryInteractor
import com.github.ai.autokpass.presentation.ui.screens.select_entry.SelectEntryViewModel
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.SelectPatternArgs
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.SelectPatternInteractor
import com.github.ai.autokpass.presentation.ui.screens.select_pattern.SelectPatternViewModel
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockInteractor
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Printer> { StandardOutputPrinter() }
        single<FileSystemProvider> { DefaultFileSystemProvider() }
        single { AutotypeSequenceFactory() }
        single { AutotypePatternParser() }
        single { AutotypePatternFormatter() }
        single { ArgumentExtractor() }
        single { ArgumentParser(get()) }
        single { ThreadThrottler() }
        single { SystemPropertyProvider() }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single<ErrorInteractor> { ErrorInteractorImpl(get()) }
        single<EntryFormatter> { DefaultEntryFormatter() }
        single<FocusedWindowProvider> { XdotoolFocusedWindowProvider(get()) }
        single { AutotypeExecutorFactory(get(), get()) }
        single<Dispatchers> { DefaultDispatchers() }
        single<FuzzyMatcher> { Fzf4jFuzzyMatcher() }

        // use cases
        single { PrintGreetingsUseCase(get()) }
        single { ReadDatabaseUseCase(get()) }
        single { GetVisibleEntriesUseCase(get()) }
        single { AutotypeUseCase(get(), get(), get(), get()) }
        single { AwaitWindowChangeUseCase(get(), get(), get()) }
        single { GetOSTypeUseCase(get()) }
        single { DetermineAutotypeExecutorTypeUseCase(get()) }
        single { KeepassDatabaseFactoryProvider(get(), get()) }

        // interactor
        single { MainInteractor(get(), get(), get()) }
        single { UnlockInteractor(get(), get()) }
        single { SelectEntryInteractor(get(), get(), get(), get()) }
        single { SelectPatternInteractor(get(), get(), get()) }
        single { AutotypeInteractor(get(), get(), get(), get(), get(), get()) }

        // View Models
        factory { (router: Router, appArgs: ParsedArgs) -> UnlockViewModel(get(), get(), get(), router, appArgs) }
        factory { (router: Router, args: SelectEntryArgs, appArgs: ParsedArgs) ->
            SelectEntryViewModel(get(), get(), get(), router, args, appArgs)
        }
        factory { (router: Router, args: SelectPatternArgs, appArgs: ParsedArgs) ->
            SelectPatternViewModel(get(), get(), router, args, appArgs)
        }
        factory { (rootViewModel: RootViewModel, router: Router, args: AutotypeArgs, appArgs: ParsedArgs) ->
            AutotypeViewModel(get(), get(), get(), rootViewModel, router, args, appArgs)
        }
    }
}