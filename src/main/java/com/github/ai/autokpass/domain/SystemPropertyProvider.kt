package com.github.ai.autokpass.domain

import com.github.ai.autokpass.util.StringUtils.EMPTY

class SystemPropertyProvider {

    fun getSystemProperty(name: String): String {
        return System.getProperty(name) ?: EMPTY
    }
}