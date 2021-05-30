package me.nikhilchaudhari.library.shapes

import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.nikhilchaudhari.library.internal.BlurMaker


/**
 * Pot / Basin shape
 *    /\___________/\
 * __/               \__
 * TODO: Fix the clipping of the shadows on foreground
 */
open class Pot(private val cornerType: CornerType = CornerType.Rounded()) : NeuShape {

    override fun drawShadows(
        drawScope: ContentDrawScope,
        blurMaker: BlurMaker,
        shapeConfig: ShapeConfig
    ) {
        shapeConfig.cornerType = cornerType
        drawScope.drawOnBackground(shapeConfig, blurMaker)
        drawScope.drawContent()
        drawScope.drawOnForeground(shapeConfig, blurMaker)
    }

    class Oval(): Pot(CornerType.Oval)

    class Rounded(radius: Dp = 12.dp): Pot(CornerType.Rounded(radius))
}