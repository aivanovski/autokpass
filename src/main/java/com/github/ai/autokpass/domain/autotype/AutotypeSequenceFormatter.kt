package com.github.ai.autokpass.domain.autotype

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.github.ai.autokpass.model.AutotypeSequence
import com.github.ai.autokpass.model.AutotypeSequenceItem
import com.github.ai.autokpass.model.AutotypeSequenceItem.Delay
import com.github.ai.autokpass.model.AutotypeSequenceItem.Enter
import com.github.ai.autokpass.model.AutotypeSequenceItem.Tab
import com.github.ai.autokpass.model.AutotypeSequenceItem.Text
import com.github.ai.autokpass.util.StringUtils.EMPTY
import java.util.Base64

class AutotypeSequenceFormatter {

    fun format(sequence: AutotypeSequence): String? {
        if (sequence.items.isEmpty()) {
            return null
        }

        val sequenceNode = serializeAutotypeSequence(sequence, JsonNodeFactory(false))

        return ObjectMapper().writeValueAsString(sequenceNode)
    }

    private fun serializeAutotypeSequence(
        sequence: AutotypeSequence,
        factory: JsonNodeFactory
    ): JsonNode {
        val itemsNode = factory.arrayNode()

        for (item in sequence.items) {
            val itemNode = serializeItem(item, factory)
            itemsNode.add(itemNode)
        }

        val sequenceNode = factory.objectNode()
        sequenceNode.set<JsonNode>(JsonKeys.ITEMS, itemsNode)

        return sequenceNode
    }

    private fun serializeItem(
        item: AutotypeSequenceItem,
        factory: JsonNodeFactory
    ): JsonNode {
        val type = item.javaClass.simpleName ?: EMPTY

        return when (item) {
            is Tab, is Enter -> {
                factory.objectNode().apply {
                    put(JsonKeys.TYPE, type)
                }
            }
            is Text -> {
                val encodedValue = Base64.getEncoder().encodeToString(item.text.toByteArray())
                factory.objectNode().apply {
                    put(JsonKeys.TYPE, type)
                    put(JsonKeys.VALUE, encodedValue)
                }
            }
            is Delay -> {
                factory.objectNode().apply {
                    put(JsonKeys.TYPE, type)
                    put(JsonKeys.VALUE, item.millis)
                }
            }
        }
    }
}