package com.github.ai.autokpass.di

import com.github.ai.autokpass.data.config.ConfigRepository
import com.github.ai.autokpass.data.file.DefaultFileSystemProvider
import com.github.ai.autokpass.data.file.FileSystemProvider
import com.github.ai.autokpass.data.keepass.KeepassDatabaseFactoryProvider
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.ErrorInteractorImpl
import com.github.ai.autokpass.domain.StartInteractor
import com.github.ai.autokpass.domain.SystemPropertyProvider
import com.github.ai.autokpass.domain.arguments.ConfigParser
import com.github.ai.autokpass.domain.autotype.AutotypeExecutorFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFactory
import com.github.ai.autokpass.domain.autotype.AutotypePatternFormatter
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.ThreadThrottler
import com.github.ai.autokpass.domain.coroutine.DefaultDispatchers
import com.github.ai.autokpass.domain.coroutine.Dispatchers
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.fuzzySearch.FuzzyMatcher
import com.github.ai.autokpass.domain.fuzzySearch.Fzf4jFuzzyMatcher
import com.github.ai.autokpass.domain.usecases.DetermineAutotypeExecutorTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetOSTypeUseCase
import com.github.ai.autokpass.domain.usecases.GetVisibleEntriesUseCase
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import com.github.ai.autokpass.domain.usecases.ReadDatabaseUseCase
import com.github.ai.autokpass.domain.window.FocusedWindowProvider
import com.github.ai.autokpass.domain.window.XdotoolFocusedWindowProvider
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.presentation.printer.Printer
import com.github.ai.autokpass.presentation.printer.StandardOutputPrinter
import com.github.ai.autokpass.presentation.process.JprocProcessExecutor
import com.github.ai.autokpass.presentation.process.ProcessExecutor
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.core.strings.StringResources
import com.github.ai.autokpass.presentation.ui.core.strings.StringResourcesImpl
import com.github.ai.autokpass.presentation.ui.root.RootViewModel
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeArgs
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeInteractor
import com.github.ai.autokpass.presentation.ui.screens.autotype.AutotypeViewModel
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryInteractor
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryInteractorImpl
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryViewModel
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternArgs
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternInteractor
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternInteractorImpl
import com.github.ai.autokpass.presentation.ui.screens.selectPattern.SelectPatternViewModel
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockInteractor
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockInteractorImpl
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel
import org.koin.dsl.module
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object KoinModule {

    val appModule = module {
        single<StringResources> { StringResourcesImpl() }
        single<Printer> { StandardOutputPrinter() }
        single<FileSystemProvider> { DefaultFileSystemProvider() }
        single { AutotypeSequenceFactory() }
        single { AutotypePatternParser() }
        single { AutotypePatternFormatter() }
        single { ConfigParser(get(), get()) }
        single { ThreadThrottler() }
        single { SystemPropertyProvider() }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single<ErrorInteractor> { ErrorInteractorImpl(logger<ErrorInteractorImpl>(), get()) }
        single<EntryFormatter> { DefaultEntryFormatter() }
        single<FocusedWindowProvider> { XdotoolFocusedWindowProvider(get()) }
        single { AutotypeExecutorFactory(get(), get()) }
        single<Dispatchers> { DefaultDispatchers() }
        single<FuzzyMatcher> { Fzf4jFuzzyMatcher() }
        single { AutotypePatternFactory() }

        // services
        single { ConfigRepository(get(), get(), get(), get()) }

        // use cases
        single { PrintGreetingsUseCase(get(), get()) }
        single { ReadDatabaseUseCase(get()) }
        single { GetVisibleEntriesUseCase(get()) }
        single { GetOSTypeUseCase(get(), get()) }
        single { DetermineAutotypeExecutorTypeUseCase(get()) }
        single { KeepassDatabaseFactoryProvider(get(), get(), get()) }

        // interactors
        single { StartInteractor(get(), get()) }
        single<UnlockInteractor> { UnlockInteractorImpl(get(), get(), get()) }
        single<SelectEntryInteractor> { SelectEntryInteractorImpl(get(), get(), get(), get()) }
        single<SelectPatternInteractor> { SelectPatternInteractorImpl(get(), get(), get(), get()) }
        single { AutotypeInteractor(get(), get(), get(), get(), get(), get(), get()) }

        // View Models
        factory { (router: Router) ->
            UnlockViewModel(
                get(),
                get(),
                get(),
                router
            )
        }
        factory { (router: Router, args: SelectEntryArgs) ->
            SelectEntryViewModel(
                get(),
                get(),
                get(),
                get(),
                router,
                args
            )
        }
        factory { (router: Router, args: SelectPatternArgs) ->
            SelectPatternViewModel(
                get(),
                get(),
                get(),
                router,
                args
            )
        }
        factory { (vm: RootViewModel, r: Router, a: AutotypeArgs, appArgs: ParsedConfig) ->
            AutotypeViewModel(
                get(),
                get(),
                get(),
                get(),
                vm,
                r,
                a,
                appArgs
            )
        }
    }

    private inline fun <reified T> logger(): Logger {
        return LoggerFactory.getLogger(T::class.java)
    }
}