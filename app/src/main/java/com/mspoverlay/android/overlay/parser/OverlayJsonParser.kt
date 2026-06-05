package com.mspoverlay.android.overlay.parser

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.mspoverlay.android.overlay.model.CircleElement
import com.mspoverlay.android.overlay.model.LineElement
import com.mspoverlay.android.overlay.model.OverlayCanvas
import com.mspoverlay.android.overlay.model.OverlayDocument
import com.mspoverlay.android.overlay.model.OverlayElement
import com.mspoverlay.android.overlay.model.OverlayGame
import com.mspoverlay.android.overlay.model.OverlayMeta
import com.mspoverlay.android.overlay.model.OverlaySettings
import com.mspoverlay.android.overlay.model.RectElement

class OverlayJsonParser {
    fun parse(json: String): OverlayDocument {
        require(json.isNotBlank()) { "overlayJson is empty." }

        val root = JsonParser.parseString(json).asJsonObjectOrError("root")
        val document = OverlayDocument(
            schemaVersion = root.requiredString("schemaVersion"),
            overlayId = root.requiredString("overlayId"),
            name = root.requiredString("name"),
            platform = root.requiredString("platform"),
            game = root.optionalObject("game")?.let(::parseGame),
            canvas = parseCanvas(root.requiredObject("canvas")),
            overlaySettings = parseOverlaySettings(root.optionalObject("overlaySettings")),
            elements = parseElements(root.requiredArray("elements")),
            meta = root.optionalObject("meta")?.let(::parseMeta),
        )

        validate(document)
        return document
    }

    private fun parseCanvas(json: JsonObject): OverlayCanvas {
        return OverlayCanvas(
            baseWidth = json.requiredDouble("baseWidth"),
            baseHeight = json.requiredDouble("baseHeight"),
        )
    }

    private fun parseOverlaySettings(json: JsonObject?): OverlaySettings {
        return OverlaySettings(
            opacity = json?.optionalDouble("opacity", 1.0) ?: 1.0,
        )
    }

    private fun parseGame(json: JsonObject): OverlayGame {
        return OverlayGame(
            id = json.optionalLong("id"),
            name = json.optionalString("name"),
        )
    }

    private fun parseMeta(json: JsonObject): OverlayMeta {
        return OverlayMeta(
            createdAt = json.optionalString("createdAt"),
            updatedAt = json.optionalString("updatedAt"),
        )
    }

    private fun parseElements(elements: List<JsonElement>): List<OverlayElement> {
        return elements.map { element ->
            val json = element.asJsonObjectOrError("element")
            when (val type = json.requiredString("type")) {
                "rect" -> parseRect(json)
                "circle" -> parseCircle(json)
                "line" -> parseLine(json)
                else -> throw OverlayJsonParseException("Unsupported element type: $type")
            }
        }
    }

    private fun parseRect(json: JsonObject): RectElement {
        return RectElement(
            id = json.optionalString("id"),
            type = json.requiredString("type"),
            x = json.requiredDouble("x"),
            y = json.requiredDouble("y"),
            width = json.requiredDouble("width"),
            height = json.requiredDouble("height"),
            rotation = json.optionalDouble("rotation", 0.0),
            opacity = json.optionalDouble("opacity", 1.0),
            zIndex = json.optionalInt("zIndex", 0),
            visible = json.optionalBoolean("visible", true),
            locked = json.optionalBoolean("locked", false),
            fillColor = json.optionalString("fillColor"),
            strokeColor = json.optionalString("strokeColor"),
            strokeWidth = json.optionalDouble("strokeWidth", 0.0),
            cornerRadius = json.optionalDouble("cornerRadius", 0.0),
        )
    }

    private fun parseCircle(json: JsonObject): CircleElement {
        return CircleElement(
            id = json.optionalString("id"),
            type = json.requiredString("type"),
            x = json.requiredDouble("x"),
            y = json.requiredDouble("y"),
            width = json.requiredDouble("width"),
            height = json.requiredDouble("height"),
            rotation = json.optionalDouble("rotation", 0.0),
            opacity = json.optionalDouble("opacity", 1.0),
            zIndex = json.optionalInt("zIndex", 0),
            visible = json.optionalBoolean("visible", true),
            locked = json.optionalBoolean("locked", false),
            fillColor = json.optionalString("fillColor"),
            strokeColor = json.optionalString("strokeColor"),
            strokeWidth = json.optionalDouble("strokeWidth", 0.0),
        )
    }

    private fun parseLine(json: JsonObject): LineElement {
        return LineElement(
            id = json.optionalString("id"),
            type = json.requiredString("type"),
            x1 = json.requiredDouble("x1"),
            y1 = json.requiredDouble("y1"),
            x2 = json.requiredDouble("x2"),
            y2 = json.requiredDouble("y2"),
            opacity = json.optionalDouble("opacity", 1.0),
            zIndex = json.optionalInt("zIndex", 0),
            visible = json.optionalBoolean("visible", true),
            locked = json.optionalBoolean("locked", false),
            strokeColor = json.optionalString("strokeColor"),
            strokeWidth = json.optionalDouble("strokeWidth", 1.0),
            dashStyle = json.optionalString("dashStyle"),
        )
    }

    private fun validate(document: OverlayDocument) {
        if (document.platform.lowercase() != "android") {
            throw OverlayJsonParseException("Unsupported overlay platform.")
        }
        if (document.canvas.baseWidth <= 0 || document.canvas.baseHeight <= 0) {
            throw OverlayJsonParseException("canvas size is invalid.")
        }
        if (document.overlaySettings.opacity !in 0.0..1.0) {
            throw OverlayJsonParseException("overlaySettings.opacity is invalid.")
        }
    }
}

class OverlayJsonParseException(message: String) : IllegalArgumentException(message)

private fun JsonElement.asJsonObjectOrError(name: String): JsonObject {
    if (!isJsonObject) {
        throw OverlayJsonParseException("Invalid object field: $name")
    }
    return asJsonObject
}

private fun JsonObject.requiredObject(key: String): JsonObject {
    return requiredElement(key).asJsonObjectOrError(key)
}

private fun JsonObject.optionalObject(key: String): JsonObject? {
    val value = get(key) ?: return null
    if (value.isJsonNull) return null
    return value.asJsonObjectOrError(key)
}

private fun JsonObject.requiredArray(key: String): List<JsonElement> {
    val value = requiredElement(key)
    if (!value.isJsonArray) {
        throw OverlayJsonParseException("Invalid array field: $key")
    }
    return value.asJsonArray.toList()
}

private fun JsonObject.requiredString(key: String): String {
    return requiredElement(key).asString
}

private fun JsonObject.optionalString(key: String): String? {
    val value = get(key) ?: return null
    if (value.isJsonNull) return null
    return value.asString
}

private fun JsonObject.requiredDouble(key: String): Double {
    return requiredElement(key).asDouble
}

private fun JsonObject.optionalDouble(key: String, defaultValue: Double): Double {
    val value = get(key) ?: return defaultValue
    if (value.isJsonNull) return defaultValue
    return value.asDouble
}

private fun JsonObject.optionalLong(key: String): Long? {
    val value = get(key) ?: return null
    if (value.isJsonNull) return null
    return value.asLong
}

private fun JsonObject.optionalInt(key: String, defaultValue: Int): Int {
    val value = get(key) ?: return defaultValue
    if (value.isJsonNull) return defaultValue
    return value.asInt
}

private fun JsonObject.optionalBoolean(key: String, defaultValue: Boolean): Boolean {
    val value = get(key) ?: return defaultValue
    if (value.isJsonNull) return defaultValue
    return value.asBoolean
}

private fun JsonObject.requiredElement(key: String): JsonElement {
    val value = get(key)
    if (value == null || value.isJsonNull) {
        throw OverlayJsonParseException("Missing field: $key")
    }
    return value
}
