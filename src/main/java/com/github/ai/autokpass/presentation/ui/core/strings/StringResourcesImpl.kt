package com.github.ai.autokpass.presentation.ui.core.strings

import java.util.Locale
import java.util.ResourceBundle

class StringResourcesImpl : StringResources {

    private val resourceMap = ResourceBundleMap(
        resources = ResourceBundle.getBundle("strings", Locale.getDefault())
    )

    override val appName: String by resourceMap
    override val cancel: String by resourceMap
    override val password: String by resourceMap
    override val unlock: String by resourceMap
    override val exit: String by resourceMap
    override val selectPattern: String by resourceMap
    override val selectEntry: String by resourceMap
    override val autotyping: String by resourceMap
    override val autotypeSelectWindowMessage: String by resourceMap
    override val autotypeCountDownMessage: String by resourceMap
    override val invalidCredentialsMessage: String by resourceMap
    override val greetingsMessage: String by resourceMap
    override val noEntriesInDatabase: String by resourceMap
    override val errorFailedToDetermineOsType: String by resourceMap
    override val errorFailedToCompileAutotypeSequence: String by resourceMap
    override val errorHasBeenOccurred: String by resourceMap
    override val errorFailedToGetWindowName: String by resourceMap
    override val errorFailedToGetWindowFocus: String by resourceMap
    override val errorWindowFocusAwaitTimeout: String by resourceMap
    override val errorFailedToParseConfigFile: String by resourceMap
    override val errorNoArgumentsWereSpecified: String by resourceMap
    override val errorOptionCanNotBeEmpty: String by resourceMap
    override val errorFileDoesNotExist: String by resourceMap
    override val errorFileIsNotFile: String by resourceMap
    override val errorFailedToParseArgument: String by resourceMap
    override val errorFailedToGetEnvironmentVariable: String by resourceMap
}