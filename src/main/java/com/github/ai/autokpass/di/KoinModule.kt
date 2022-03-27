package com.github.ai.autokpass.di

import com.github.ai.autokpass.domain.Interactor
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.process.ProcessExecutor
import com.github.ai.autokpass.domain.process.JprocProcessExecutor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypePatternParser
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.XdotoolAutotypeExecutor
import com.github.ai.autokpass.domain.formatter.DefaultEntryFormatter
import com.github.ai.autokpass.domain.formatter.EntryFormatter
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.domain.printer.StandardOutputPrinter
import com.github.ai.autokpass.domain.usecases.AutotypeUseCase
import com.github.ai.autokpass.domain.usecases.GetAllEntriesUseCase
import com.github.ai.autokpass.domain.usecases.PrintAllEntriesUseCase
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Printer> { StandardOutputPrinter() }
        single { AutotypeSequenceFactory() }
        single { AutotypePatternParser() }
        single { ArgumentExtractor(get()) }
        single { ArgumentParser(get()) }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single { ErrorInteractor(get()) }
        single<EntryFormatter> { DefaultEntryFormatter() }

        single<AutotypeExecutor> { XdotoolAutotypeExecutor(get()) }

        // use cases
        single { GetAllEntriesUseCase() }
        single { PrintAllEntriesUseCase(get(), get(), get()) }
        single { AutotypeUseCase(get(), get(), get()) }

        single { Interactor(get(), get(), get()) }
    }
}