package com.glasslauncher.cinema

import android.content.Context
import com.glasslauncher.cinema.data.Settings
import java.io.File

object CinemaSetup {
    fun initialize(ctx: Context, settings: Settings) {
        val flags=ctx.getSharedPreferences("cinema-setup",Context.MODE_PRIVATE)
        val needsPack=flags.getInt("wallpaperPackVersion",0)<2
        if(needsPack) {
            val packFolder=File(ctx.getExternalFilesDir(null),"backgrounds").apply{mkdirs()}
            ctx.assets.list("cinema").orEmpty().filter{(it.endsWith(".jpg")||it.endsWith(".png"))&&it!="ary-plus-tile.png"}.forEach{name->
                ctx.assets.open("cinema/$name").use{input->File(packFolder,name).outputStream().use{input.copyTo(it)}}
            }
            settings.clock24h=true
            settings.showClock=true
            settings.wallpaperRotationHours=4
            settings.wallpaperShuffleQueue=""
            flags.edit().putInt("wallpaperPackVersion",2).apply()
        }
        if(flags.getBoolean("seeded",false)) {
            if(!flags.getBoolean("clockProfileV2",false)) {
                settings.showClock=true
                flags.edit().putBoolean("clockProfileV2",true).apply()
            }
            return
        }
        val folder=File(ctx.getExternalFilesDir(null),"backgrounds").apply{mkdirs()}
        // Wallpaper assets were copied above, including upgrades from an existing custom build.
        val config=File(ctx.filesDir,"cinema-seed.json")
        ctx.assets.open("cinema/settings.json").use{input->config.outputStream().use{input.copyTo(it)}}
        settings.importLocalConfig(config)
        settings.bgImage=File(folder,"depth-mountain.png").absolutePath
        settings.wallpaperChangedAt=System.currentTimeMillis().toString()
        flags.edit().putBoolean("seeded",true).putBoolean("clockProfileV2",true).apply()
    }
}
