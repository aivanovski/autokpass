package com.github.ai.autokpass.presentation.ui.core

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.ai.autokpass.presentation.ui.core.theme.AppColors
import com.github.ai.autokpass.presentation.ui.core.theme.AppTextStyles
import kotlinx.coroutines.launch

@Composable
fun SelectorView(
    title: String,
    query: String,
    entries: List<String>,
    highlights: List<List<Int>>,
    selectedIndex: Int,
    onInputTextChanged: (text: String) -> Unit,
    onItemClicked: (index: Int) -> Unit,
    onDownKeyPressed: () -> Unit,
    onUpKeyPressed: () -> Unit,
    onEnterKeyPressed: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    val counterText = if (entries.isNotEmpty()) {
        "${selectedIndex + 1}/${entries.size}"
    } else {
        "0/0"
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedTextField(
            value = query,
            singleLine = true,
            textStyle = AppTextStyles.editor,
            onValueChange = { text -> onInputTextChanged.invoke(text) },
            label = {
                Text(
                    text = title
                )
            },
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { event ->
                    handleKeyEvent(
                        event,
                        onDownKeyPressed,
                        onUpKeyPressed,
                        onEnterKeyPressed
                    )
                }
        )

        Text(
            text = counterText,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            style = AppTextStyles.secondary
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
                state = state
            ) {
                items(count = entries.size) { idx ->
                    ListItem(
                        text = entries[idx],
                        highlights = highlights[idx],
                        isSelected = (idx == selectedIndex),
                        onClicked = { onItemClicked.invoke(idx) }
                    )
                }
            }

            LaunchedEffect(selectedIndex) {
                scope.launch {
                    if (!state.isScrollInProgress) {
                        val visibleItems = state.layoutInfo.visibleItemsInfo
                        val firstVisibleIndex = visibleItems.firstOrNull()?.index
                        val lastVisibleIndex = visibleItems.lastOrNull()?.index
                        if (firstVisibleIndex != null &&
                            lastVisibleIndex != null &&
                            selectedIndex !in (firstVisibleIndex until lastVisibleIndex)
                        ) {
                            state.scrollToItem(index = selectedIndex)
                        }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
private fun handleKeyEvent(
    event: KeyEvent,
    onDownKeyPressed: () -> Unit,
    onUpKeyPressed: () -> Unit,
    onEnterKeyPressed: () -> Unit
): Boolean {
    return when {
        event.type == KeyEventType.KeyDown && event.key == Key.DirectionDown -> {
            onDownKeyPressed.invoke()
            true
        }

        event.type == KeyEventType.KeyDown && event.key == Key.DirectionUp -> {
            onUpKeyPressed.invoke()
            true
        }

        event.type == KeyEventType.KeyUp && event.key == Key.Enter -> {
            onEnterKeyPressed.invoke()
            true
        }

        else -> {
            false
        }
    }
}

@Composable
private fun ListItem(
    text: String,
    highlights: List<Int>,
    isSelected: Boolean,
    onClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    AppColors.selectedItemBackground
                } else {
                    Color.Transparent
                }
            )
            .focusable(enabled = true)
            .clickable { onClicked.invoke() }
    ) {
        Text(
            text = buildHighlightedText(text, highlights),
            style = AppTextStyles.primary,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
}

private fun buildHighlightedText(
    text: String,
    highlightedIndices: List<Int>
): AnnotatedString {
    val builder = AnnotatedString.Builder()

    if (highlightedIndices.isNotEmpty()) {
        var nextHighlightedIdx = highlightedIndices.first()
        var highlightIdx = 0
        for (textIdx in text.indices) {
            if (textIdx == nextHighlightedIdx) {
                builder.withStyle(style = SpanStyle(AppColors.highlightedTextColor)) {
                    append(text[textIdx])
                }

                if (highlightIdx != highlightedIndices.lastIndex) {
                    highlightIdx++
                    nextHighlightedIdx = highlightedIndices[highlightIdx]
                }
            } else {
                builder.append(text[textIdx])
            }
        }
    } else {
        builder.append(text)
    }

    return builder.toAnnotatedString()
}