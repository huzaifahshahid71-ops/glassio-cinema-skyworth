package com.glasslauncher.cinema.ui

import android.graphics.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONObject

/** Live 24-hour clock; foreground masks share the wallpaper's exact coordinate space. */
@Composable
fun CinematicDepthClock(image: ImageBitmap?, filename: String, opacity: Float = 1f) {
    val ctx=LocalContext.current
    val profiles=remember(ctx) { JSONObject(ctx.assets.open("cinema/depth-profiles.json").bufferedReader().use{it.readText()}) }
    val profile=profiles.optJSONObject(filename) ?: return
    val layout=profile.getJSONObject("clock")
    val mask by produceState<Pair<String,Bitmap>?>(null,filename) {
        value=withContext(Dispatchers.IO) {
            runCatching { ctx.assets.open("cinema/"+profile.getString("mask")).use { filename to BitmapFactory.decodeStream(it) } }.getOrNull()
        }
    }
    val time by produceState(java.text.SimpleDateFormat("HH:mm",java.util.Locale.US).format(java.util.Date())) {
        while(true) {
            value=java.text.SimpleDateFormat("HH:mm",java.util.Locale.US).format(java.util.Date())
            delay(1000L*(60-java.util.Calendar.getInstance().get(java.util.Calendar.SECOND)))
        }
    }
    val face=remember(ctx) { Typeface.createFromAsset(ctx.assets,"cinema/clock-condensed.ttf") }
    val textPaint=remember(face) { Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface=face;textAlign=Paint.Align.CENTER;fontVariationSettings="'wght' 700";fontFeatureSettings="tnum"
    } }
    val maskPaint=remember { Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
        xfermode=PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    } }
    Canvas(Modifier.fillMaxSize().graphicsLayer { alpha=opacity.coerceIn(0f,1f) }) {
        val bitmap=image?.asAndroidBitmap() ?: return@Canvas
        val foreground=mask?.takeIf{it.first==filename}?.second ?: return@Canvas
        val w=size.width;val h=size.height
        val x=layout.getDouble("x").toFloat()*w
        val dark=layout.optBoolean("dark",false)
        val dateBaseline=(layout.getDouble("date").toFloat()*h).coerceIn(h*.105f,h*.30f)
        textPaint.textSize=layout.getDouble("font").toFloat()*h;textPaint.textScaleX=1f
        textPaint.textScaleX=layout.getDouble("width").toFloat()*w/textPaint.measureText(time).coerceAtLeast(1f)
        val bounds=Rect();textPaint.getTextBounds(time,0,time.length,bounds)
        // Guard actual glyph bounds, not nominal font size; the date never touches screen overscan.
        val baseline=maxOf(layout.getDouble("y").toFloat()*h,dateBaseline+h*.055f-bounds.top)
        drawIntoCanvas { target ->
            val c=target.nativeCanvas
            val layer=c.saveLayer(0f,0f,w,h,null)
            // Filled silhouettes avoid the internal overlapping contours exposed by path strokes.
            textPaint.shader=null;textPaint.color=if(dark)0x55304751 else 0x55718C9D
            c.drawText(time,x+1.1f,baseline+1.1f,textPaint)
            textPaint.color=if(dark)0x887B959E.toInt() else 0xCCFFFFFF.toInt()
            c.drawText(time,x-.8f,baseline-.8f,textPaint)
            val shader=BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
            shader.setLocalMatrix(Matrix().apply{setScale(w/bitmap.width,h/bitmap.height);postTranslate(-5f,3f)})
            textPaint.shader=shader;textPaint.alpha=255;c.drawText(time,x,baseline,textPaint)
            val colors=if(dark)intArrayOf(0xA02B4550.toInt(),0x552B4550,0x801B3544.toInt())
                else intArrayOf(0xA8F4FAFF.toInt(),0x38D9EDFA,0x78C1D8E5)
            textPaint.shader=LinearGradient(0f,baseline-textPaint.textSize,0f,baseline,colors,null,Shader.TileMode.CLAMP)
            c.drawText(time,x,baseline,textPaint)
            // Erase only clock pixels; never repaint or misalign the underlying photograph.
            c.drawBitmap(foreground,null,RectF(0f,0f,w,h),maskPaint)
            c.restoreToCount(layer)
            textPaint.textScaleX=1f;textPaint.textSize=w*.017f;textPaint.shader=null
            textPaint.color=if(dark)0xFF263D47.toInt() else 0xFFF0F5FA.toInt()
            textPaint.setShadowLayer(w*.001f,0f,1f,if(dark)0x55FFFFFF else 0x66000000)
            val date=java.text.SimpleDateFormat("EEEE, d MMMM",java.util.Locale.US).format(java.util.Date())
            c.drawText(date,x,dateBaseline,textPaint)
            textPaint.clearShadowLayer()
        }
    }
}
