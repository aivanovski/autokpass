package com.github.ai.autokpass.presentation.ui.core.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class AutokpassTextStyles(
    val editor: TextStyle,
    val primary: TextStyle,
    val secondary: TextStyle,
    val button: TextStyle,
    val error: TextStyle,
    val hint: TextStyle
)

val AppTextStyles = AutokpassTextStyles(
    editor = TextStyle(
        fontSize = 22.sp,
        color = AppColors.primaryTextColor
    ),
    primary = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.primaryTextColor
    ),
    secondary = TextStyle(
        fontSize = 16.sp,
        color = AppColors.secondaryTextColor
    ),
    button = TextStyle(
        fontSize = 18.sp,
        color = AppColors.primaryTextColor
    ),
    error = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.error
    ),
    hint = TextStyle(
        fontSize = 14.sp,
        color = AppColors.hintTextColor
    )
)