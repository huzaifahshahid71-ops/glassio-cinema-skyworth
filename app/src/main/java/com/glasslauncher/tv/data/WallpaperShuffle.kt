package com.glasslauncher.cinema.data

import kotlin.random.Random

object WallpaperShuffle {
    data class Selection(val next:String,val remaining:List<String>)
    fun choose(files:List<String>,current:String,remaining:List<String>,random:Random=Random.Default):Selection? {
        val candidates=files.distinct()
        if(candidates.isEmpty())return null
        val saved=remaining.filter{it in candidates && it!=current}.distinct()
        val bag=if(saved.isNotEmpty())saved else candidates.shuffled(random).toMutableList().also {
            if(it.size>1&&it.first()==current){val second=it[1];it[1]=it[0];it[0]=second}
        }
        return Selection(bag.first(),bag.drop(1))
    }
    fun due(now:Long,last:Long,hours:Int):Boolean = hours>0 && last>0 && now>=last && now-last>=hours*3600_000L
}
