package com.github.ai.autokpass.presentation.ui.core.strings

import java.util.Locale
import java.util.ResourceBundle

class StringResourcesImpl : StringResources {

    private val resources: ResourceBundle = ResourceBundle.getBundle("strings", Locale.getDefault())

    override val appName: String
        get() = resources.getString("appName")

    override val cancel: String
        get() = resources.getString("cancel")

    override val password: String
        get() = resources.getString("password")

    override val unlock: String
        get() = resources.getString("unlock")

    override val exit: String
        get() = resources.getString("exit")

    override val selectPattern: String
        get() = resources.getString("selectPattern")

    override val selectEntry: String
        get() = resources.getString("selectEntry")

    override val autotyping: String
        get() = resources.getString("autotyping")

    override val autotypeSelectWindowMessage: String
        get() = resources.getString("autotypeSelectWindowMessage")

    override val autotypeCountDownMessage: String
        get() = resources.getString("autotypeCountDownMessage")

    override val invalidCredentialsMessage: String
        get() = resources.getString("invalidCredentialsMessage")

    override val greetingsMessage: String
        get() = resources.getString("greetingsMessage")

    override val errorFailedToDetermineOsType: String
        get() = resources.getString("errorFailedToDetermineOsType")

    override val errorFailedToCompileAutotypeSequence: String
        get() = resources.getString("errorFailedToCompileAutotypeSequence")

    override val errorHasBeenOccurred: String
        get() = resources.getString("errorHasBeenOccurred")

    override val errorFailedToGetWindowName: String
        get() = resources.getString("errorFailedToGetWindowName")

    override val errorFailedToGetWindowFocus: String
        get() = resources.getString("errorFailedToGetWindowFocus")

    override val errorWindowFocusAwaitTimeout: String
        get() = resources.getString("errorWindowFocusAwaitTimeout")

    override val errorFailedToParseConfigFile: String
        get() = resources.getString("errorFailedToParseConfigFile")

    override val errorNoArgumentsWereSpecified: String
        get() = resources.getString("errorNoArgumentsWereSpecified")

    override val errorNoAccessibilityPermission: String
        get() = resources.getString("errorNoAccessibilityPermission")

    override val errorOptionCanNotBeEmpty: String
        get() = resources.getString("errorOptionCanNotBeEmpty")

    override val errorFileDoesNotExist: String
        get() = resources.getString("errorFileDoesNotExist")

    override val errorFileIsNotFile: String
        get() = resources.getString("errorFileIsNotFile")

    override val errorFailedToParseArgument: String
        get() = resources.getString("errorFailedToParseArgument")

    override val errorFailedToGetEnvironmentVariable: String
        get() = resources.getString("errorFailedToGetEnvironmentVariable")
}