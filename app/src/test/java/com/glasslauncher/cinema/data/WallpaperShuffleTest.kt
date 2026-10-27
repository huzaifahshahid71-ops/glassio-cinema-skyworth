package com.glasslauncher.cinema.data

import org.junit.Assert.*
import org.junit.Test
import kotlin.random.Random

class WallpaperShuffleTest {
    @Test fun fourHourBoundaryAndClockRollback() {
        val last=1_000_000L
        assertFalse(WallpaperShuffle.due(last+4*3600_000L-1,last,4))
        assertTrue(WallpaperShuffle.due(last+4*3600_000L,last,4))
        assertFalse(WallpaperShuffle.due(last-1,last,4))
        assertFalse(WallpaperShuffle.due(last+24*3600_000L,last,0))
        assertFalse(WallpaperShuffle.due(last+24*3600_000L,0,4))
    }
    @Test fun eachBagVisitsEveryWallpaperAndSurvivesSerializedQueue() {
        val files=(1..15).map{"depth-$it.jpg"}
        for(seed in 0..49) {
            val random=Random(seed);var current=files.first();var remaining=emptyList<String>()
            repeat(6) {
                val cycle=mutableListOf<String>()
                repeat(files.size) {
                    val pick=WallpaperShuffle.choose(files,current,remaining,random)!!
                    assertNotEquals(current,pick.next)
                    current=pick.next;cycle+=current
                    remaining=pick.remaining.joinToString("\n").split('\n').filter{it.isNotEmpty()}
                }
                assertEquals(files.toSet(),cycle.toSet())
                assertEquals(files.size,cycle.size)
            }
        }
    }
    @Test fun removedFilesAndDuplicateEntriesCannotBeSelected() {
        val allowed=listOf("a","b","b","c")
        val pick=WallpaperShuffle.choose(allowed,"a",listOf("deleted","a","b","b","c"),Random(1))!!
        assertEquals("b",pick.next);assertEquals(listOf("c"),pick.remaining)
    }
    @Test fun emptyAndSingleFileAreSafe() {
        assertNull(WallpaperShuffle.choose(emptyList(),"a",emptyList()))
        assertEquals("a",WallpaperShuffle.choose(listOf("a"),"a",emptyList())!!.next)
    }
}
