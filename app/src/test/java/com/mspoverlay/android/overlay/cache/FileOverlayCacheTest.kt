package com.mspoverlay.android.overlay.cache

import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FileOverlayCacheTest {
    @Test
    fun savesAndLoadsLastAppliedOverlay() {
        val directory = Files.createTempDirectory("msp-overlay-cache").toFile()
        val cache = FileOverlayCache(directory)

        cache.save(
            CachedOverlay(
                overlayId = "ovl_android_001",
                code = "ABC123",
                overlayJson = """{"overlayId":"ovl_android_001"}""",
            ),
        )

        val loaded = cache.loadLastApplied()

        assertEquals("ovl_android_001", loaded?.overlayId)
        assertEquals("ABC123", loaded?.code)
        assertEquals("""{"overlayId":"ovl_android_001"}""", loaded?.overlayJson)
    }

    @Test
    fun clearRemovesCachedOverlay() {
        val directory = Files.createTempDirectory("msp-overlay-cache").toFile()
        val cache = FileOverlayCache(directory)
        cache.save(
            CachedOverlay(
                overlayId = "ovl_android_001",
                code = null,
                overlayJson = "{}",
            ),
        )

        cache.clear()

        assertNull(cache.loadLastApplied())
    }
}
