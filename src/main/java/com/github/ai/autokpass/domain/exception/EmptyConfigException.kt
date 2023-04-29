package com.github.ai.autokpass.domain.exception

import com.github.ai.autokpass.presentation.ui.core.strings.StringResources

class EmptyConfigException(
    strings: StringResources
) : ParsingException(strings.errorNoArgumentsWereSpecified)