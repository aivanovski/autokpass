package com.github.ai.autokpass.presentation.ui.core.strings

interface StringResources {
    val appName: String
    val cancel: String
    val password: String
    val unlock: String
    val exit: String
    val selectPattern: String
    val selectEntry: String

    // Errors
    val errorFailedToDetermineOsType: String
    val errorFailedToCompileAutotypeSequence: String
    val errorHasBeenOccurred: String

    // Errors with formatted values
    val errorOptionCanNotBeEmpty: String
    val errorFileDoesNotExist: String
    val errorFileIsNotFile: String
    val errorFailedToParseArgument: String
    val errorFailedToGetValueFromEnvironment: String
}