package com.github.ai.autokpass.domain.exception

import com.github.ai.autokpass.presentation.ui.core.strings.StringResources

class InvalidPasswordException(
    strings: StringResources
) : AutokpassException(strings.invalidCredentialsMessage)