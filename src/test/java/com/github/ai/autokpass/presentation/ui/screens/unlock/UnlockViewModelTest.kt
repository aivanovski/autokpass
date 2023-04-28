package com.github.ai.autokpass.presentation.ui.screens.unlock

import com.github.ai.autokpass.TestData.COMMAND
import com.github.ai.autokpass.TestData.DB_PATH
import com.github.ai.autokpass.TestData.DEFAULT_DELAY
import com.github.ai.autokpass.TestData.DEFAULT_DELAY_BETWEEN_ACTIONS
import com.github.ai.autokpass.TestData.ERROR_MESSAGE
import com.github.ai.autokpass.TestData.KEY_PATH
import com.github.ai.autokpass.domain.ErrorInteractor
import com.github.ai.autokpass.model.KeepassKey.PasswordKey
import com.github.ai.autokpass.model.ParsedArgs
import com.github.ai.autokpass.model.Result
import com.github.ai.autokpass.presentation.ui.Screen
import com.github.ai.autokpass.presentation.ui.core.navigation.Router
import com.github.ai.autokpass.presentation.ui.screens.selectEntry.SelectEntryArgs
import com.github.ai.autokpass.presentation.ui.screens.unlock.UnlockViewModel.ScreenState
import com.github.ai.autokpass.util.StringUtils.EMPTY
import com.github.ai.autokpass.utils.TestDispatchers
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.lang.Exception
import org.junit.jupiter.api.Test

internal class UnlockViewModelTest {

    private val interactor = mockk<UnlockInteractor>()
    private val errorInteractor = mockk<ErrorInteractor>()
    private val dispatchers = TestDispatchers()
    private val router = mockk<Router>()

    @Test
    fun `init should set Data state`() {
        // arrange
        val viewModel = viewModel()

        // act

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = null,
            isPasswordVisible = false
        )
    }

    @Test
    fun `onPasswordInputChanged should update screen state`() {
        // arrange
        val viewModel = viewModel()

        // act
        viewModel.onPasswordInputChanged(CHANGED_TEXT)

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = CHANGED_TEXT,
            error = null,
            isPasswordVisible = false
        )
    }

    @Test
    fun `onPasswordInputChanged should remove error`() {
        // arrange
        val viewModel = viewModel()
        viewModel.setupErrorState()
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = ERROR_MESSAGE,
            isPasswordVisible = false
        )

        // act
        viewModel.onPasswordInputChanged(CHANGED_TEXT)

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = CHANGED_TEXT,
            error = null,
            isPasswordVisible = false
        )
    }

    @Test
    fun `togglePasswordVisibility should make password visible`() {
        // arrange
        val viewModel = viewModel()

        // act
        viewModel.togglePasswordVisibility()

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = null,
            isPasswordVisible = true
        )
    }

    @Test
    fun `togglePasswordVisibility should make password invisible`() {
        // arrange
        val viewModel = viewModel()
        viewModel.togglePasswordVisibility()
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = null,
            isPasswordVisible = true
        )

        // act
        viewModel.togglePasswordVisibility()

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = null,
            isPasswordVisible = false
        )
    }

    @Test
    fun `unlockDatabase should return error`() {
        // arrange
        val viewModel = viewModel()
        val error = Result.Error(Exception(ERROR_MESSAGE))
        coEvery { interactor.unlockDatabase(EMPTY, DB_PATH) }.returns(error)
        every { errorInteractor.processAndGetMessage(error) }.returns(ERROR_MESSAGE)

        // act
        viewModel.unlockDatabase()

        // assert
        coVerify { interactor.unlockDatabase(EMPTY, DB_PATH) }
        verify { errorInteractor.processAndGetMessage(error) }
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = ERROR_MESSAGE,
            isPasswordVisible = false
        )
    }

    @Test
    fun `unlockDatabase should navigate to next screen`() {
        // arrange
        val viewModel = viewModel()
        val nextScreen = Screen.SelectEntry(args = SelectEntryArgs(PasswordKey(EMPTY)))
        coEvery { interactor.unlockDatabase(EMPTY, DB_PATH) }.returns(Result.Success(Unit))
        every { router.navigateTo(nextScreen) }.returns(Unit)

        // act
        viewModel.unlockDatabase()

        // assert
        coVerify { interactor.unlockDatabase(EMPTY, DB_PATH) }
        verify { router.navigateTo(nextScreen) }
    }

    @Test
    fun `clearError should remove error`() {
        // arrange
        val viewModel = viewModel()
        viewModel.setupErrorState()

        // act
        viewModel.clearError()

        // assert
        viewModel.state.value shouldBe ScreenState.Data(
            password = EMPTY,
            error = null,
            isPasswordVisible = false
        )
    }

    private fun UnlockViewModel.setupErrorState() {
        val error = Result.Error(Exception(ERROR_MESSAGE))
        coEvery { interactor.unlockDatabase(EMPTY, DB_PATH) }.returns(error)
        every { errorInteractor.processAndGetMessage(error) }.returns(ERROR_MESSAGE)

        unlockDatabase()
    }

    private fun viewModel(args: ParsedArgs = newArgs()): UnlockViewModel =
        UnlockViewModel(
            interactor = interactor,
            errorInteractor = errorInteractor,
            dispatchers = dispatchers,
            router = router,
            args = args
        )

    private fun newArgs(): ParsedArgs =
        ParsedArgs(
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