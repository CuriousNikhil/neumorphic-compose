package me.nikhilchaudhari.library.internal

import android.content.Context
import android.graphics.*
import android.renderscript.*
import java.lang.ref.WeakReference


/**
 * Holds required data for of blur operation
 */
data class BlurConfig(
    val width: Int,
    val height: Int,
    val radius: Int = MAX_RADIUS,
    val sampling: Int = DEFAULT_SAMPLING,
    val color: Int = Color.TRANSPARENT
) {
    companion object {
        const val MAX_RADIUS = 25
        const val DEFAULT_SAMPLING = 1
    }
}

//TODO: Make sure if this is being invoked properly
class BlurMaker(context: Context, private val defaultBlurRadius: Int) {

    private val contextRef = WeakReference(context)

    fun blur(
        source: Bitmap,
        radius: Int = defaultBlurRadius,
        sampling: Int = BlurConfig.DEFAULT_SAMPLING
    ): Bitmap? {
        val blurConfig = BlurConfig(
            width = source.width,
            height = source.height,
            radius = radius,
            sampling = sampling
        )
        return blur(source, blurConfig)
    }

    private fun blur(source: Bitmap, blurConfig: BlurConfig): Bitmap? {
        val width = blurConfig.width / blurConfig.sampling
        val height = blurConfig.height / blurConfig.sampling
        if (width == 0 || height == 0) {
            return null
        }
        //create a bitmap
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        Canvas(bitmap).run {
            scale(1 / blurConfig.sampling.toFloat(), 1 / blurConfig.sampling.toFloat())
            val paint = Paint().apply {
                flags = Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
                colorFilter = PorterDuffColorFilter(blurConfig.color, PorterDuff.Mode.SRC_ATOP)
            }
            drawBitmap(source, 0f, 0f, paint)
        }

        //try blur with renderscript
        val blurBitmap: Bitmap? = try {
            blurWithRenderScript(bitmap, blurConfig.radius)
        } catch (e: RSRuntimeException) {
            // if renderscript fails, use stack blur
            bitmap.stackBlur(blurConfig.radius)
        }

        // return blurred bitmap
        return blurBitmap?.let {
            if (blurConfig.sampling == BlurConfig.DEFAULT_SAMPLING) {
                it
            } else {
                // If sample size altered, create a scaled bitmap
                val scaled = Bitmap.createScaledBitmap(it, blurConfig.width, blurConfig.height, true)
                it.recycle()
                scaled
            }
        }
    }

    @Throws(RSRuntimeException::class)
    private fun blurWithRenderScript(bitmap: Bitmap, radius: Int): Bitmap? {
        val context = contextRef.get() ?: return null
        var blur: ScriptIntrinsicBlur? = null
        var input: Allocation? = null
        var output: Allocation? = null
        var rs: RenderScript? = null
        try {
            rs = RenderScript.create(context)
            rs.messageHandler = RenderScript.RSMessageHandler()
            input = Allocation.createFromBitmap(
                rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT
            )
            output = Allocation.createTyped(rs, input.type)
            blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            blur.setInput(input)
            blur.setRadius(radius.toFloat())
            blur.forEach(output)
            output.copyTo(bitmap)
        } finally {
            rs?.destroy()
            input?.destroy()
            output?.destroy()
            blur?.destroy()
        }
        return bitmap
    }
}