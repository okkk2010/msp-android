package com.mspoverlay.android.overlay.cache

import java.io.File

data class CachedOverlay(
    val overlayId: String,
    val code: String?,
    val overlayJson: String,
)

interface OverlayCache {
    fun save(cachedOverlay: CachedOverlay)
    fun loadLastApplied(): CachedOverlay?
    fun clear()
}

class FileOverlayCache(
    private val directory: File,
) : OverlayCache {
    private val jsonFile = File(directory, "last-overlay.json")
    private val metaFile = File(directory, "last-overlay.properties")

    override fun save(cachedOverlay: CachedOverlay) {
        directory.mkdirs()
        jsonFile.writeText(cachedOverlay.overlayJson)
        metaFile.writeText(
            buildString {
                appendLine("overlayId=${cachedOverlay.overlayId}")
                appendLine("code=${cachedOverlay.code.orEmpty()}")
            },
        )
    }

    override fun loadLastApplied(): CachedOverlay? {
        if (!jsonFile.isFile || !metaFile.isFile) {
            return null
        }

        val meta = metaFile.readLines()
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            .toMap()

        val overlayId = meta["overlayId"]?.takeIf { it.isNotBlank() } ?: return null
        val code = meta["code"]?.takeIf { it.isNotBlank() }
        return CachedOverlay(
            overlayId = overlayId,
            code = code,
            overlayJson = jsonFile.readText(),
        )
    }

    override fun clear() {
        jsonFile.delete()
        metaFile.delete()
    }
}

