package me.nikhilchaudhari.library.shapes

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.withTranslation
import me.nikhilchaudhari.library.internal.BlurMaker
import kotlin.math.roundToInt


// TODO: Try tweaking canvas values
/*pressed shape - draw after the content */
internal fun ContentDrawScope.drawOnForeground(
    shapeConfig: ShapeConfig,
    blurMaker: BlurMaker
) {

    val drawScope = this
    val lightShadowDrawable = GradientDrawable()
    val darkShadowDrawable = GradientDrawable()

    val elevation = this.density.run { shapeConfig.elevation.toPx() }
    val cornerType = shapeConfig.cornerType
    val radius =
        if (cornerType is CornerType.Rounded) this.density.run { cornerType.radius.toPx() } else 0f

    val size = drawScope.size
    val width = size.width.toInt() + elevation.toInt()
    val height = size.height.toInt() + elevation.toInt()
    val strokeWidth = this.density.run { shapeConfig.strokeWidth.toPx() }.toInt()

    lightShadowDrawable.apply {
        setSize(width, height)
        setStroke(strokeWidth, shapeConfig.lightShadowColor.toArgb())
        setBounds(0, 0, width, height)
        setColor(Color.Transparent.toArgb())
        setNeuShape(cornerType, ShadowForm.LightShadow, radius)
    }

    darkShadowDrawable.apply {
        setSize(width, height)
        setStroke(strokeWidth, shapeConfig.darkShadowColor.toArgb())
        setColor(Color.Transparent.toArgb())
        setBounds(0, 0, width, height)
        setNeuShape(cornerType, ShadowForm.DarkShadow, radius)
    }


    generateShadowBitmap(
        size.width.toInt(),
        size.height.toInt(),
        lightShadowDrawable,
        darkShadowDrawable,
        elevation,
        blurMaker
    )?.asImageBitmap()?.let {
        drawScope.drawImage(it)
    }
}


private fun generateShadowBitmap(
    w: Int,
    h: Int,
    lightShadowDrawable: GradientDrawable,
    darkShadowDrawable: GradientDrawable,
    elevation: Float,
    blurMaker: BlurMaker
) = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).blurred(blurMaker) {
    withTranslation(
        x = -elevation,
        y = -elevation
    ) {
        lightShadowDrawable.draw(this)
    }
    withTranslation(
//        x = elevation,
//        y = elevation
    ) {
        darkShadowDrawable.draw(this)
    }
}


/* Flat shape - before the content draw scope */
internal fun ContentDrawScope.drawOnBackground(
    shapeConfig: ShapeConfig,
    blurMaker: BlurMaker
) {
    val drawScope = this
    val lightShadowDrawable = GradientDrawable()
    val darkShadowDrawable = GradientDrawable()

    val elevation = this.density.run { shapeConfig.elevation.toPx() }
    val horizontalInset = this.density.run { shapeConfig.neuInsets.horizontal.toPx() }
    val verticalInset = this.density.run { shapeConfig.neuInsets.vertical.toPx() }
    val cornerType = shapeConfig.cornerType
    val radius =
        if (cornerType is CornerType.Rounded) this.density.run { cornerType.radius.toPx() } else 0f

    val size = drawScope.size
    val width = size.width.toInt()
    val height = size.height.toInt()

    lightShadowDrawable.apply {
        setColor(shapeConfig.lightShadowColor.toArgb())
        setSize(width, height)
        setBounds(0, 0, width, height)
        setNeuShape(cornerType, ShadowForm.Default, radius)
    }

    darkShadowDrawable.apply {
        setColor(shapeConfig.darkShadowColor.toArgb())
        setSize(width, height)
        setBounds(0, 0, width, height)
        setNeuShape(cornerType, ShadowForm.Default, radius)
    }

    val lightShadowBitmap =
        lightShadowDrawable
            .toBlurredBitmap(width, height, elevation, blurMaker)
            ?.asImageBitmap()
    val darkShadowBitmap =
        darkShadowDrawable
            .toBlurredBitmap(width, height, elevation, blurMaker)
            ?.asImageBitmap()

    lightShadowBitmap?.let { bitmap ->
        drawScope.inset(
            -(horizontalInset + elevation),
            -(verticalInset + elevation)
        ) {
            drawImage(bitmap)
        }
    }

    darkShadowBitmap?.let { bitmap ->
        drawScope.inset(
            horizontalInset - elevation,
            verticalInset - elevation
        ) {
            drawImage(bitmap)
        }
    }
}


private fun Drawable.toBlurredBitmap(
    w: Int,
    h: Int,
    elevation: Float,
    blurMaker: BlurMaker
): Bitmap? {
    val width = (w + elevation * 2).roundToInt()
    val height = (h + elevation * 2).roundToInt()

    return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        .blurred(blurMaker) {
            withTranslation(elevation, elevation) {
                draw(this)
            }
        }
}


internal fun Bitmap.blurred(
    blurMaker: BlurMaker,
    block: Canvas.() -> Unit
): Bitmap? {
    return blurMaker.blur(this.also {
        Canvas(this).run {
            block()
        }
    })
}

internal sealed class ShadowForm {
    object Default : ShadowForm()
    object LightShadow : ShadowForm()
    object DarkShadow : ShadowForm()
}


private fun GradientDrawable.setNeuShape(
    cornerType: CornerType,
    shadowForm: ShadowForm,
    radius: Float
) {
    when (cornerType) {
        is CornerType.Oval -> {
            shape = GradientDrawable.OVAL
        }
        is CornerType.Rounded -> {
            shape = GradientDrawable.RECTANGLE
            when (shadowForm) {
                is ShadowForm.Default -> {
                    cornerRadius = radius
                }
                is ShadowForm.LightShadow -> {
                    cornerRadii =
                        floatArrayOf(
                            0f,
                            0f,
                            radius,
                            radius,
                            radius,
                            radius,
                            radius,
                            radius
                        )
                }
                is ShadowForm.DarkShadow -> {
                    cornerRadii =
                        floatArrayOf(
                            radius,
                            radius,
                            radius,
                            radius,
                            0f,
                            0f,
                            radius,
                            radius
                        )
                }
            }
        }
    }
}

