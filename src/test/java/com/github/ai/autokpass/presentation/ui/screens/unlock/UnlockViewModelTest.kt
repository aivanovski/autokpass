package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PASSWORD
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.TestData.ERROR_MESSAGE
import com.github.ai.autokpass.TestData.EXCEPTION
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.domain.MockErrorInteractorImpl
import com.github.ai.autokpass.model.KeepassKey
import com.github.ai.autokpass.model.ParsedConfig
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.navigation.MockRouterImpl
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.UnlockIntent.OnErrorIconClicked
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.UnlockIntent.OnPasswordInputChanged
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.UnlockIntent.OnPasswordVisibilityChanged
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.UnlockIntent.OnUnlockButtonClicked
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.UnlockState
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import java.io.File
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class UnlockViewModelTest {

    private val dispatchers = TestDispatchers()
    private val router = MockRouterImpl()

    @Nested
    @DisplayName("when init")
    inner class WhenInit {

        @Test
        fun `should set Loading state`() {
            newViewModel().state.value shouldBe UnlockState.Loading
        }
    }

    @Nested
    @DisplayName("when start() called")
    inner class WhenStartCalled {

        @Test
        fun `should show data`() {
            // arrange
            val viewModel = newViewModel()

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe newDataState()
        }

        @Test
        fun `should show error`() {
            // arrange
            val message = EXCEPTION.message ?: EMPTY
            val viewModel = newViewModel(
                interactor = newInteractor(
                    config = Result.Error(EXCEPTION)
                )
            )

            // act
            viewModel.start()

            // assert
            viewModel.state.value shouldBe UnlockState.Error(message)
        }

        @Test
        fun `should show next screen`() {
            // arrange
            val key = KeepassKey.FileKey(
                file = File(KEY_PATH),
                processingCommand = COMMAND
            )
            val config = newConfigWithKey()
            val viewModel = newViewModel(
                interactor = newInteractor(
                    config = Result.Success(config)
                )
            )

            // act
            viewModel.start()

            // assert
            router.lastScreen shouldBe Screen.SelectEntry(args = SelectEntryArgs(key, DB_PATH))
            viewModel.state.value shouldBe UnlockState.Loading
        }
    }

    @Nested
    @DisplayName("when password input changed")
    inner class WhenPasswordInputChanged {

        @Test
        fun `should update screen state`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState())

            // act
            viewModel.sendIntent(OnPasswordInputChanged(CHANGED_TEXT))

            // assert
            viewModel.state.value shouldBe newDataState(password = CHANGED_TEXT)
        }

        @Test
        fun `should remove input error`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState(error = ERROR_MESSAGE))

            // act
            viewModel.sendIntent(OnPasswordInputChanged(CHANGED_TEXT))

            // assert
            viewModel.state.value shouldBe newDataState(password = CHANGED_TEXT)
        }
    }

    @Nested
    @DisplayName("when visibility icon clicked")
    inner class WhenVisibilityIconClicked {

        @Test
        fun `should make password visible`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState(isPasswordVisible = false))

            // act
            viewModel.sendIntent(OnPasswordVisibilityChanged)

            // assert
            viewModel.state.value shouldBe newDataState(isPasswordVisible = true)
        }

        @Test
        fun `should make password not visible`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState(isPasswordVisible = true))

            // act
            viewModel.sendIntent(OnPasswordVisibilityChanged)

            // assert
            viewModel.state.value shouldBe newDataState(isPasswordVisible = false)
        }
    }

    @Nested
    @DisplayName("when unlock button clicked")
    inner class WhenUnlockButtonClicked {

        @Test
        fun `should navigate to next screen`() {
            // arrange
            val viewModel = newViewModel()
            val key = KeepassKey.PasswordKey(DB_PASSWORD)
            viewModel.setupState(newDataState(password = DB_PASSWORD))

            // act
            viewModel.sendIntent(OnUnlockButtonClicked)

            // assert
            router.lastScreen shouldBe Screen.SelectEntry(SelectEntryArgs(key, DB_PATH))
            viewModel.state.value shouldBe UnlockState.Loading
        }

        @Test
        fun `should show error in data state`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState(password = EMPTY))

            // act
            viewModel.sendIntent(OnUnlockButtonClicked)

            // assert
            viewModel.state.value shouldBe newDataState(error = EXCEPTION.message)
        }
    }

    @Nested
    @DisplayName("when error icon clicked")
    inner class WhenErrorIconClicked {
        @Test
        fun `should remove error from data state`() {
            // arrange
            val viewModel = newViewModel()
            viewModel.setupState(newDataState(error = ERROR_MESSAGE))

            // act
            viewModel.sendIntent(OnErrorIconClicked)

            // assert
            viewModel.state.value shouldBe newDataState()
        }
    }

    private fun UnlockViewModel.setupState(state: UnlockState) {
        this.start()
        (this.state as MutableStateFlow).value = state
    }

    private fun newDataState(
        password: String = EMPTY,
        error: String? = null,
        isPasswordVisible: Boolean = false
    ): UnlockState.Data =
        UnlockState.Data(
            password = password,
            error = error,
            isPasswordVisible = isPasswordVisible
        )

    private fun newInteractor(
        config: Result<ParsedConfig> = Result.Success(newConfig())
    ): UnlockInteractor =
        MockUnlockInteractor(
            config = config,
            passwordKey = KeepassKey.PasswordKey(DB_PASSWORD),
            fileKey = KeepassKey.FileKey(
                file = File(KEY_PATH),
                processingCommand = COMMAND
            )
        )

    private fun newViewModel(
        interactor: UnlockInteractor = newInteractor(),
        errorInteractor: ErrorInteractor = MockErrorInteractorImpl(),
        router: Router = this.router
    ): UnlockViewModel =
        UnlockViewModel(
            interactor = interactor,
            errorInteractor = errorInteractor,
            dispatchers = dispatchers,
            router = router
        )

    private fun newConfig(): ParsedConfig =
        ParsedConfig(
            filePath = DB_PATH,
            keyPath = null,
            startDelayInMillis = DEFAULT_DELAY,
            delayBetweenActionsInMillis = DEFAULT_DELAY_BETWEEN_ACTIONS,
            autotypeType = null,
            keyProcessingCommand = null
        )

    private fun newConfigWithKey(): ParsedConfig =
        ParsedConfig(
            filePath = DB_PATH,
            keyPath = KEY_PATH,
            startDelayInMillis = DEFAULT_DELAY,
            delayBetweenActionsInMillis = DEFAULT_DELAY_BETWEEN_ACTIONS,
            autotypeType = null,
            keyProcessingCommand = COMMAND
        )

    companion object {
        private const val CHANGED_TEXT = "changed_text"
    }
}