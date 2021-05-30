package me.nikhilchaudhari.library.shapes

import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.nikhilchaudhari.library.internal.BlurMaker


/**
 * Punched or Bumped shape
 *    _________
 * __/         \__
 */
open class Punched(private val cornerType: CornerType = CornerType.Rounded()) : NeuShape {

    override fun drawShadows(
        drawScope: ContentDrawScope,
        blurMaker: BlurMaker,
        shapeConfig: ShapeConfig
    ) {
        shapeConfig.cornerType = cornerType
        drawScope.drawOnBackground(shapeConfig, blurMaker)
        drawScope.drawContent()
    }

    class Oval() : Punched(CornerType.Oval)

    class Rounded(radius: Dp = 12.dp) : Punched(CornerType.Rounded(radius))
}