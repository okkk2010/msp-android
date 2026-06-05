package com.mspoverlay.android

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectSkeletonTest {
    @Test
    fun packageNameMatchesAndroidClientContract() {
        assertEquals("com.mspoverlay.android", MainActivity::class.java.`package`?.name)
    }
}
