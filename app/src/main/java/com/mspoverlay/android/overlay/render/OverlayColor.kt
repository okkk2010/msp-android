package com.mspoverlay.android.overlay.render

import kotlin.math.roundToInt

object OverlayColor {
    fun parse(value: String?, opacity: Double = 1.0): Int? {
        if (value.isNullOrBlank()) {
            return null
        }

        val color = value.trim()
        val argb = when {
            color.startsWith("#") -> parseHex(color)
            color.startsWith("rgba", ignoreCase = true) -> parseRgba(color)
            color.startsWith("rgb", ignoreCase = true) -> parseRgb(color)
            else -> null
        } ?: return null

        val alpha = ((argb ushr 24) and 0xff)
        val scaledAlpha = (alpha * opacity.coerceIn(0.0, 1.0)).roundToInt().coerceIn(0, 255)
        return (scaledAlpha shl 24) or (argb and 0x00ffffff)
    }

    private fun parseHex(value: String): Int? {
        val hex = value.removePrefix("#")
        return when (hex.length) {
            6 -> {
                val rgb = hex.toLongOrNull(16)?.toInt() ?: return null
                0xff000000.toInt() or rgb
            }
            8 -> hex.toLongOrNull(16)?.toInt()
            else -> null
        }
    }

    private fun parseRgb(value: String): Int? {
        val components = value.substringAfter("(").substringBeforeLast(")")
            .split(",")
            .map { it.trim() }
        if (components.size != 3) {
            return null
        }

        val r = components[0].toIntOrNull()?.coerceIn(0, 255) ?: return null
        val g = components[1].toIntOrNull()?.coerceIn(0, 255) ?: return null
        val b = components[2].toIntOrNull()?.coerceIn(0, 255) ?: return null
        return argb(255, r, g, b)
    }

    private fun parseRgba(value: String): Int? {
        val components = value.substringAfter("(").substringBeforeLast(")")
            .split(",")
            .map { it.trim() }
        if (components.size != 4) {
            return null
        }

        val r = components[0].toIntOrNull()?.coerceIn(0, 255) ?: return null
        val g = components[1].toIntOrNull()?.coerceIn(0, 255) ?: return null
        val b = components[2].toIntOrNull()?.coerceIn(0, 255) ?: return null
        val a = ((components[3].toDoubleOrNull() ?: return null).coerceIn(0.0, 1.0) * 255).roundToInt()
        return argb(a, r, g, b)
    }

    private fun argb(a: Int, r: Int, g: Int, b: Int): Int {
        return (a.coerceIn(0, 255) shl 24) or
            (r.coerceIn(0, 255) shl 16) or
            (g.coerceIn(0, 255) shl 8) or
            b.coerceIn(0, 255)
    }
}

