package com.github.ai.autokpass.presentation.ui.core.theme

import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AutokpassColors(
    val background: Color,
    val selectedItemBackground: Color,
    val primaryTextColor: Color,
    val secondaryTextColor: Color,
    val error: Color,
    val highlightedTextColor: Color,
    val materialColors: Colors
)

val AppColors = AutokpassColors(
    background = Color(0xff_060606),
    selectedItemBackground = Color(0xff_3a3a3a),
    primaryTextColor = Color(0xff_e1e3df),
    secondaryTextColor = Color(0xff_c0c9c1),
    error = Color(0xff_cf6679),
    highlightedTextColor = Color(0xff_388E3C),
    materialColors = darkColors(
        primary = Color(0xff_364b3f),
        background = Color(0xff_121212),
        error = Color(0xff_cf6679)
    )
)
