package com.example.ui.editor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.model.AspectRatioType
import com.example.model.FilterType
import com.example.model.MaskType
import com.example.model.MotionEffectType
import com.example.model.ProjectData
import com.example.model.TextOverlay
import com.example.model.TransitionType
import com.example.model.VideoClip
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBorder
import kotlin.random.Random

@Composable
fun VideoPreviewCanvas(
    project: ProjectData,
    currentPlayheadMs: Long,
    isPlaying: Boolean,
    showSafeGuides: Boolean = false,
    selectedTextId: String? = null,
    onSelectText: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Find active base clip
    val activeClip = project.clips.firstOrNull { clip ->
        currentPlayheadMs >= clip.startTimeMs && currentPlayheadMs <= clip.endTimeMs
    } ?: project.clips.firstOrNull()

    // Find active overlay/PIP clips
    val overlayClips = project.clips.filter { it.isOverlayPip && currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= it.endTimeMs }

    // Find active effects
    val activeEffects = project.effects.filter { currentPlayheadMs >= it.startTimeMs && currentPlayheadMs <= it.endTimeMs }

    val activeMotionEffect = activeEffects.firstOrNull()?.effectType ?: activeClip?.motionEffect ?: MotionEffectType.NONE

    // Shake and pulse animations
    val shakeOffset = remember { Animatable(0f) }
    val flashAlpha = remember { Animatable(0f) }
    val beatScale = remember { Animatable(1f) }

    LaunchedEffect(activeMotionEffect, isPlaying) {
        if (isPlaying) {
            when (activeMotionEffect) {
                MotionEffectType.IMPACT_SHAKE -> {
                    shakeOffset.animateTo(
                        targetValue = 12f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(50, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
                MotionEffectType.STROBE_FLASH -> {
                    flashAlpha.animateTo(
                        targetValue = 0.8f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(100, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
                MotionEffectType.ZOOM_PULSE -> {
                    beatScale.animateTo(
                        targetValue = 1.08f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(300, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )
                }
                else -> {
                    shakeOffset.snapTo(0f)
                    flashAlpha.snapTo(0f)
                    beatScale.snapTo(1f)
                }
            }
        } else {
            shakeOffset.snapTo(0f)
            flashAlpha.snapTo(0f)
            beatScale.snapTo(1f)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF07080C)),
        contentAlignment = Alignment.Center
    ) {
        // Frame strictly to project's AspectRatio
        val ratio = project.aspectRatio.ratio
        Box(
            modifier = Modifier
                .aspectRatio(ratio)
                .fillMaxSize()
                .padding(8.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, ObsidianBorder, RoundedCornerShape(12.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Render Base Video/Photo Clip
            if (activeClip != null) {
                RenderVideoClipItem(
                    clip = activeClip,
                    activeMotionEffect = activeMotionEffect,
                    shakeOffset = shakeOffset.value,
                    beatScale = beatScale.value,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Render Overlays / PIP
            for (overlay in overlayClips) {
                RenderPipOverlay(clip = overlay)
            }

            // Motion Overlays (VHS Scanlines, RGB Glitch, Flash, Golden Sparks)
            if (activeMotionEffect == MotionEffectType.VHS_SCANLINES) {
                RenderVhsScanlines()
            }
            if (activeMotionEffect == MotionEffectType.GLITCH_SPLIT) {
                RenderGlitchArtifacts()
            }
            if (activeMotionEffect == MotionEffectType.SPARKLE_RAIN) {
                RenderSparkleRain()
            }
            if (activeMotionEffect == MotionEffectType.STROBE_FLASH && flashAlpha.value > 0.05f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = flashAlpha.value))
                )
            }

            // Render 3D Text Overlays
            for (text in project.texts) {
                Render3DTextOverlay(
                    textOverlay = text,
                    currentPlayheadMs = currentPlayheadMs,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Safe Zone Guides Overlay (for Reels / TikTok button boundaries)
            if (showSafeGuides) {
                RenderSafeGuidesOverlay()
            }
        }
    }
}

@Composable
private fun RenderVideoClipItem(
    clip: VideoClip,
    activeMotionEffect: MotionEffectType,
    shakeOffset: Float,
    beatScale: Float,
    modifier: Modifier = Modifier
) {
    // Build Color Matrix for filter and adjustments
    val colorMatrix = remember(
        clip.filter,
        clip.brightness,
        clip.contrast,
        clip.saturation,
        clip.temperature,
        clip.exposure
    ) {
        buildCombinedColorMatrix(
            clip.filter,
            clip.brightness,
            clip.contrast,
            clip.saturation,
            clip.temperature,
            clip.exposure
        )
    }

    // Mask shapes
    val shapeModifier = when (clip.maskType) {
        MaskType.CIRCLE -> Modifier.clip(CircleShape)
        MaskType.ROUNDED_RECT -> Modifier.clip(RoundedCornerShape(24.dp))
        MaskType.STAR -> Modifier.clip(StarShape)
        MaskType.HEART -> Modifier.clip(HeartShape)
        MaskType.DIAMOND -> Modifier.clip(DiamondShape)
        else -> Modifier
    }

    val transformMod = Modifier
        .graphicsLayer {
            scaleX = (if (clip.isFlippedH) -1f else 1f) * clip.zoomScale * beatScale
            scaleY = (if (clip.isFlippedV) -1f else 1f) * clip.zoomScale * beatScale
            rotationZ = clip.rotateDeg
            translationX = clip.panX * 200f + (if (activeMotionEffect == MotionEffectType.IMPACT_SHAKE) shakeOffset else 0f)
            translationY = clip.panY * 200f + (if (activeMotionEffect == MotionEffectType.IMPACT_SHAKE) (shakeOffset * 0.7f) else 0f)
            if (clip.blurRadius > 0f) {
                alpha = 0.95f
            }
        }
        .then(shapeModifier)

    Box(
        modifier = modifier.then(transformMod),
        contentAlignment = Alignment.Center
    ) {
        if (!clip.uri.isNullOrBlank()) {
            AsyncImage(
                model = clip.uri,
                contentDescription = clip.name,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Draw atmospheric visual backdrop based on sample type
            val drawableRes = when (clip.sampleVisualType) {
                "gym" -> R.drawable.hero_motivation_gym_1787831517556
                "success" -> R.drawable.hero_motivation_success_1787831536660
                "study" -> R.drawable.hero_motivation_gym_1787831517556
                else -> R.drawable.hero_motivation_success_1787831536660
            }
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = clip.name,
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(colorMatrix),
                modifier = Modifier.fillMaxSize()
            )
        }

        // Green Screen / Chroma Key Overlay simulation if enabled
        if (clip.chromaKeyEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color(0x6600FF00))
                        )
                    )
            )
        }
    }
}

@Composable
private fun RenderPipOverlay(clip: VideoClip) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = clip.pipScale
                scaleY = clip.pipScale
                translationX = clip.pipX * 300f
                translationY = clip.pipY * 400f
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp, 280.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(2.dp, MotivationGold, RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_motivation_success_1787831536660),
                contentDescription = "PIP",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun RenderVhsScanlines() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.35f)) {
        val lineSpacing = 8.dp.toPx()
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = Color.White.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
            y += lineSpacing
        }
    }
}

@Composable
private fun RenderGlitchArtifacts() {
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.45f)) {
        val barHeight = 24.dp.toPx()
        val randomY = (size.height * 0.3f)
        drawRect(
            color = Color.Cyan.copy(alpha = 0.4f),
            topLeft = Offset(-15f, randomY),
            size = Size(size.width, barHeight)
        )
        drawRect(
            color = Color.Magenta.copy(alpha = 0.4f),
            topLeft = Offset(15f, randomY + 10f),
            size = Size(size.width, barHeight)
        )
    }
}

@Composable
private fun RenderSparkleRain() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val points = listOf(
            Offset(size.width * 0.2f, size.height * 0.25f),
            Offset(size.width * 0.75f, size.height * 0.18f),
            Offset(size.width * 0.4f, size.height * 0.65f),
            Offset(size.width * 0.85f, size.height * 0.75f),
            Offset(size.width * 0.15f, size.height * 0.82f)
        )
        for (pt in points) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD700), Color.Transparent),
                    center = pt,
                    radius = 28f
                ),
                radius = 28f,
                center = pt
            )
        }
    }
}

@Composable
private fun RenderSafeGuidesOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, ElectricCyan.copy(alpha = 0.6f))
            .padding(top = 40.dp, bottom = 60.dp, start = 20.dp, end = 50.dp)
            .border(1.dp, Color.Yellow.copy(alpha = 0.4f))
    ) {
        Text(
            text = "SAFE REEL ZONE (9:16)",
            color = ElectricCyan.copy(alpha = 0.8f),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(4.dp)
        )
    }
}

private fun buildCombinedColorMatrix(
    filter: FilterType,
    brightness: Float,
    contrast: Float,
    saturation: Float,
    temperature: Float,
    exposure: Float
): ColorMatrix {
    val cm = ColorMatrix()

    // Apply base Filter Matrix
    when (filter) {
        FilterType.CINEMATIC_GOLD -> {
            cm.setToSaturation(1.15f)
            // Boost warm amber and crush darks
            val arr = floatArrayOf(
                1.3f, 0f, 0f, 0f, 15f,
                0f, 1.15f, 0f, 0f, 5f,
                0f, 0f, 0.85f, 0f, -10f,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(arr))
        }
        FilterType.CYBERPUNK -> {
            val arr = floatArrayOf(
                1.2f, 0f, 0.2f, 0f, 10f,
                0f, 0.8f, 0.2f, 0f, -10f,
                0.2f, 0f, 1.4f, 0f, 25f,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(arr))
        }
        FilterType.MOODY_DARK -> {
            val arr = floatArrayOf(
                0.9f, 0f, 0f, 0f, -20f,
                0f, 0.9f, 0f, 0f, -20f,
                0f, 0f, 0.9f, 0f, -15f,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(arr))
        }
        FilterType.MONOCHROME -> {
            cm.setToSaturation(0f)
        }
        FilterType.HIGH_DRAMA -> {
            val arr = floatArrayOf(
                1.4f, 0f, 0f, 0f, -15f,
                0f, 1.4f, 0f, 0f, -15f,
                0f, 0f, 1.4f, 0f, -15f,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(arr))
        }
        FilterType.WARM_SUNRISE -> {
            val arr = floatArrayOf(
                1.25f, 0f, 0f, 0f, 20f,
                0f, 1.1f, 0f, 0f, 10f,
                0f, 0f, 0.9f, 0f, -15f,
                0f, 0f, 0f, 1f, 0f
            )
            cm.set(ColorMatrix(arr))
        }
        else -> {}
    }

    // Additional user adjustment controls
    if (saturation != 1.0f) {
        val satMatrix = ColorMatrix().apply { setToSaturation(saturation) }
        cm.timesAssign(satMatrix)
    }

    return cm
}

// Custom shapes for masking
val StarShape = GenericShape { size, _ ->
    val cx = size.width / 2f
    val cy = size.height / 2f
    val outerR = minOf(size.width, size.height) / 2f
    val innerR = outerR * 0.45f
    val path = Path()
    for (i in 0 until 10) {
        val r = if (i % 2 == 0) outerR else innerR
        val angle = (i * 36 - 90) * (Math.PI / 180f).toFloat()
        val x = cx + r * kotlin.math.cos(angle)
        val y = cy + r * kotlin.math.sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
}

val HeartShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height
    moveTo(width / 2f, height / 5f)
    cubicTo(5 * width / 14f, 0f, 0f, height / 15f, width / 28f, 2 * height / 5f)
    cubicTo(width / 14f, 2 * height / 3f, 3 * width / 7f, 5 * height / 6f, width / 2f, height)
    cubicTo(4 * width / 7f, 5 * height / 6f, 13 * width / 14f, 2 * height / 3f, 27 * width / 28f, 2 * height / 5f)
    cubicTo(width, height / 15f, 9 * width / 14f, 0f, width / 2f, height / 5f)
    close()
}

val DiamondShape = GenericShape { size, _ ->
    moveTo(size.width / 2f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width / 2f, size.height)
    lineTo(0f, size.height / 2f)
    close()
}
