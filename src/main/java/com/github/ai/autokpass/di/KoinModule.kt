package com.github.ai.autokpass.di

import com.github.ai.autokpass.domain.Interactor
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.process.ProcessExecutor
import com.github.ai.autokpass.domain.process.JprocProcessExecutor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.autotype.AutotypeExecutor
import com.github.ai.autokpass.domain.autotype.AutotypeSequenceFactory
import com.github.ai.autokpass.domain.autotype.XdotoolAutotypeExecutor
import com.github.ai.autokpass.domain.printer.Printer
import com.github.ai.autokpass.domain.printer.StandardOutputPrinter
import com.github.ai.autokpass.domain.selector.OptionSelector
import com.github.ai.autokpass.domain.selector.StandardOutputOptionSelector
import com.github.ai.autokpass.domain.usecases.GetAllEntriesUseCase
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.SelectorType.STANDARD_OUTPUT
import org.koin.core.qualifier.named
import org.koin.dsl.module

object KoinModule {

    val appModule = module {
        single<Printer> { StandardOutputPrinter() }
        single { ArgumentExtractor(get()) }
        single { ArgumentParser() }
        single<ProcessExecutor> { JprocProcessExecutor() }
        single { ErrorInteractor(get()) }
        single { AutotypeSequenceFactory() }

        single<OptionSelector>(named(STANDARD_OUTPUT.name)) { StandardOutputOptionSelector(get()) }

        single<AutotypeExecutor> { XdotoolAutotypeExecutor(get(), get()) }

        single { GetAllEntriesUseCase() }

        factory { (args: ParsedArgs) ->
            Interactor(
                args,
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(qualifier = named(args.selector.name))
            )
        }
    }
}