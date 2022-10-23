package com.github.ai.autokpass

import com.github.ai.autokpass.di.GlobalInjector.get
import com.github.ai.autokpass.di.KoinModule
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.Interactor
import com.github.ai.autokpass.domain.arguments.ArgumentExtractor
import com.github.ai.autokpass.domain.arguments.ArgumentParser
import com.github.ai.autokpass.domain.usecases.PrintGreetingsUseCase
import org.koin.core.context.startKoin

fun main(args: Array<String>) {
    startKoin {
        modules(KoinModule.appModule)
    }

    val parser: ArgumentParser = get()
    val extractor: ArgumentExtractor = get()
    val errorInteractor: ErrorInteractor = get()
    val printGreetingsUseCase: PrintGreetingsUseCase = get()
    val interactor: Interactor = get()

    printGreetingsUseCase.printGreetings()

    val rawArgs = extractor.extractArguments(args)

    val parserResult = parser.validateAndParse(rawArgs)
    if (errorInteractor.processFailed(parserResult)) {
        return
    }

    val parsedArgs = parserResult.getDataOrThrow()

    interactor.run(parsedArgs)
}