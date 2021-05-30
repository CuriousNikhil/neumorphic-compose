package me.nikhilchaudhari.library.shapes


import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import me.nikhilchaudhari.library.internal.BlurMaker

/**
 * Represents neumorphic shape
 */
interface NeuShape {

    fun drawShadows(drawScope: ContentDrawScope, blurMaker: BlurMaker, shapeConfig: ShapeConfig)
}