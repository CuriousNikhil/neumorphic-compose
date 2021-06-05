package me.nikhilchaudhari.library

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import me.nikhilchaudhari.library.internal.BlurConfig
import me.nikhilchaudhari.library.internal.BlurMaker
import me.nikhilchaudhari.library.shapes.NeuShape
import me.nikhilchaudhari.library.shapes.Punched
import me.nikhilchaudhari.library.shapes.ShapeConfig
import kotlin.math.min
import kotlin.math.roundToInt

data class NeuInsets(
    val horizontal: Dp = 6.dp,
    val vertical: Dp = 6.dp
)

fun Modifier.neumorphic(
    neuInsets: NeuInsets = NeuInsets(),
    neuShape: NeuShape = Punched.Rounded(),
    lightShadowColor: Color = Color.White,
    darkShadowColor: Color = Color.LightGray,
    strokeWidth: Dp = 6.dp,
    elevation: Dp = 6.dp
) = composed {
    val context = LocalContext.current
    this.then(
        NeumorphicModifier(
            context,
            neuInsets,
            neuShape,
            lightShadowColor,
            darkShadowColor,
            strokeWidth,
            elevation,
            inspectorInfo = debugInspectorInfo {
                name = "neumorphic"
                properties["context"] = context
                properties["neuInsets"] = neuInsets
                properties["neuShape"] = neuShape
                properties["elevation"] = elevation
                properties["strokeWidth"] = strokeWidth
                properties["lightShadowColor"] = lightShadowColor
                properties["darkShadowColor"] = darkShadowColor
            }
        )
    )
}

internal class NeumorphicModifier(
    context: Context,
    private val insets: NeuInsets,
    private val neuShape: NeuShape,
    private val lightShadowColor: Color,
    private val darkShadowColor: Color,
    private val strokeWidth: Dp,
    private val elevation: Dp,
    inspectorInfo: InspectorInfo.() -> Unit
) : DrawModifier, InspectorValueInfo(inspectorInfo) {

    private val blurMaker =
        BlurMaker(context, calculateDefaultBlurRadius(context.resources.displayMetrics))

    override fun ContentDrawScope.draw() {
        val shapeConfig = ShapeConfig(
            insets,
            elevation,
            lightShadowColor,
            darkShadowColor,
            strokeWidth
        )
        neuShape.drawShadows(this, blurMaker, shapeConfig)
    }

    // TODO: Try tweaking values
    private fun calculateDefaultBlurRadius(displayMetrics: DisplayMetrics): Int {
        val densityStable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DisplayMetrics.DENSITY_DEVICE_STABLE / DisplayMetrics.DENSITY_DEFAULT.toFloat()
        } else {
            displayMetrics.density
        }
        return min(BlurConfig.MAX_RADIUS, (densityStable * 10).roundToInt())
    }
}