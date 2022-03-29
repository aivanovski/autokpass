package com.github.ai.autokpass.domain.autotype

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.AutotypeSequenceItem.Delay
import com.github.ai.autokpass.model.AutotypeSequenceItem.Enter
import com.github.ai.autokpass.model.AutotypeSequenceItem.Tab
import com.github.ai.autokpass.model.AutotypeSequenceItem.Text
import com.github.ai.autokpass.util.StringUtils.EMPTY
import java.util.Base64

class AutotypeSequenceParser {

    fun parse(data: String): AutotypeSequence? {
        if (data.isBlank()) {
            return null
        }

        val sequenceNode = ObjectMapper().readTree(data)

        return parseAutotypeSequence(sequenceNode)
    }

    private fun parseAutotypeSequence(node: JsonNode): AutotypeSequence? {
        val itemsNode = node.get(JsonKeys.ITEMS)
        if (!itemsNode.isArray) {
            return null
        }

        val items = mutableListOf<AutotypeSequenceItem>()
        for (itemNode in itemsNode) {
            val item = parseItem(itemNode) ?: continue

            items.add(item)
        }

        return if (items.isNotEmpty()) {
            AutotypeSequence(items)
        } else {
            null
        }
    }

    private fun parseItem(itemNode: JsonNode): AutotypeSequenceItem? {
        val type = itemNode.get(JsonKeys.TYPE).asText(EMPTY)
        if (type.isBlank()) {
            return null
        }

        return when (type) {
            Text::class.java.simpleName -> {
                val encodedValue = itemNode.get(JsonKeys.VALUE).asText(EMPTY)
                if (encodedValue.isBlank()) {
                    return null
                }

                val value = String(Base64.getDecoder().decode(encodedValue))
                Text(value)
            }
            Delay::class.java.simpleName -> {
                val millis = itemNode.get(JsonKeys.VALUE).asLong(-1L)
                if (millis == -1L) {
                    return null
                }

                Delay(millis)
            }
            Tab::class.simpleName -> Tab
            Enter::class.simpleName -> Enter
            else -> null
        }
    }
}