package com.xu.kiko.ui.screen.profile

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileHeatmapIntensityTest {

    @Test
    fun pomodoroCountMapsToCappedHeatmapIntensity() {
        assertEquals(0, heatmapIntensityFor(0))
        assertEquals(1, heatmapIntensityFor(1))
        assertEquals(2, heatmapIntensityFor(2))
        assertEquals(3, heatmapIntensityFor(3))
        assertEquals(4, heatmapIntensityFor(4))
        assertEquals(4, heatmapIntensityFor(5))
    }

    @Test
    fun negativePomodoroCountMapsToZeroIntensity() {
        assertEquals(0, heatmapIntensityFor(-1))
    }
}
