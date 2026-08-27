package com.example.ui.editor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Style3DType
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Render3DTextOverlay(
    textOverlay: TextOverlay,
    currentPlayheadMs: Long,
    modifier: Modifier = Modifier
) {
    if (currentPlayheadMs < textOverlay.startTimeMs || currentPlayheadMs > textOverlay.endTimeMs) {
        return
    }

    val progress = ((currentPlayheadMs - textOverlay.startTimeMs).toFloat() /
            (textOverlay.endTimeMs - textOverlay.startTimeMs).coerceAtLeast(1L)).coerceIn(0f, 1f)

    // Animation progress calculations
    val animScale = remember { Animatable(1f) }
    val animAlpha = remember { Animatable(1f) }
    val animOffset = remember { Animatable(0f) }

    LaunchedEffect(textOverlay.animation, textOverlay.id) {
        when (textOverlay.animation) {
            TextAnimationType.BOUNCE_POP -> {
                animScale.snapTo(0.2f)
                animScale.animateTo(1.15f, tween(250))
                animScale.animateTo(1.0f, tween(150))
            }
            TextAnimationType.FADE_IN -> {
                animAlpha.snapTo(0f)
                animAlpha.animateTo(1f, tween(400))
            }
            TextAnimationType.SLIDE_UP -> {
                animOffset.snapTo(60f)
                animOffset.animateTo(0f, tween(350))
            }
            TextAnimationType.ZOOM_IN -> {
                animScale.snapTo(0.5f)
                animScale.animateTo(1.0f, tween(500))
            }
            TextAnimationType.DYNAMIC_SHAKE -> {
                animOffset.animateTo(
                    targetValue = 10f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(60, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            TextAnimationType.GLOW_PULSE -> {
                animScale.animateTo(
                    targetValue = 1.06f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(400, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
            else -> {
                animScale.snapTo(1f)
                animAlpha.snapTo(1f)
                animOffset.snapTo(0f)
            }
        }
    }

    // Text to display depending on word-by-word or typewriter
    val displayText = when (textOverlay.animation) {
        TextAnimationType.TYPEWRITER -> {
            val charCount = (textOverlay.text.length * (progress * 2f).coerceAtMost(1f)).toInt()
            textOverlay.text.take(charCount)
        }
        TextAnimationType.WORD_BY_WORD -> {
            val words = textOverlay.text.split(" ")
            val wordCount = (words.size * (progress * 1.8f).coerceAtMost(1f)).toInt().coerceAtLeast(1)
            words.take(wordCount).joinToString(" ")
        }
        else -> textOverlay.text
    }

    // Material colors
    val styleColors = when (textOverlay.style3D) {
        Style3DType.METALLIC_GOLD -> listOf(Color(0xFFFFDF00), Color(0xFFFFB300), Color(0xFFD48800), Color(0xFF7A4B00))
        Style3DType.NEON_ELECTRIC -> listOf(Color(0xFF00FFFF), Color(0xFF00E5FF), Color(0xFF0091EA), Color(0xFF01579B))
        Style3DType.CHROME_MIRROR -> listOf(Color(0xFFFFFFFF), Color(0xFFE0E0E0), Color(0xFF9E9E9E), Color(0xFF424242))
        Style3DType.CRIMSON_STEEL -> listOf(Color(0xFFFF5252), Color(0xFFFF1744), Color(0xFFD50000), Color(0xFF4A0000))
        Style3DType.OBSIDIAN_GLOSS -> listOf(Color(0xFF90A4AE), Color(0xFF455A64), Color(0xFF263238), Color(0xFF101418))
        Style3DType.HOLOGRAM_CYAN -> listOf(Color(0xFF64FFDA), Color(0xFF00E5FF), Color(0xFFD500F9), Color(0xFF4A148C))
        Style3DType.ROSE_GOLD -> listOf(Color(0xFFFFD1DC), Color(0xFFFFB6C1), Color(0xFFDB7093), Color(0xFF882D4B))
        Style3DType.LIQUID_BRONZE -> listOf(Color(0xFFFFD59E), Color(0xFFCD7F32), Color(0xFF8B4513), Color(0xFF3E1F07))
    }

    val glowColor = if (textOverlay.glow3D) {
        styleColors.first().copy(alpha = 0.85f)
    } else {
        Color(textOverlay.glowColorHex)
    }

    val shadowColor = if (textOverlay.shadow3D) {
        Color.Black.copy(alpha = 0.8f)
    } else {
        Color(textOverlay.shadowColorHex)
    }

    val textAlign = when (textOverlay.alignment.lowercase()) {
        "left" -> TextAlign.Left
        "right" -> TextAlign.Right
        else -> TextAlign.Center
    }

    val fontFam = when (textOverlay.fontFamily) {
        "Hindi Devanagari" -> FontFamily.SansSerif
        "Serif Luxury" -> FontFamily.Serif
        "Modern Bold" -> FontFamily.Monospace
        else -> FontFamily.Default
    }

    val fontWt = if (textOverlay.isBold) FontWeight.Black else FontWeight.Normal

    Box(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = (textOverlay.x - 0.5f) * 400f + if (textOverlay.animation == TextAnimationType.DYNAMIC_SHAKE) animOffset.value else 0f
                translationY = (textOverlay.y - 0.5f) * 600f + if (textOverlay.animation == TextAnimationType.SLIDE_UP) animOffset.value else 0f
                scaleX = textOverlay.scale * animScale.value
                scaleY = textOverlay.scale * animScale.value
                rotationZ = textOverlay.rotation + textOverlay.rotationZ
                if (textOverlay.is3D) {
                    rotationX = textOverlay.rotationX
                    rotationY = textOverlay.rotationY
                    cameraDistance = 12f * density
                }
                alpha = textOverlay.opacity * animAlpha.value
            },
        contentAlignment = Alignment.Center
    ) {
        // Background box if enabled
        if (textOverlay.backgroundBoxColorHex != 0L) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(textOverlay.backgroundBoxCornerRadius.dp))
                    .background(Color(textOverlay.backgroundBoxColorHex))
                    .padding(textOverlay.backgroundBoxPadding.dp)
            )
        }

        // Extruded 3D Layers (depth layers rendered underneath for volumetric look)
        if (textOverlay.is3D && textOverlay.extrusion > 0f) {
            val depthLayers = (textOverlay.extrusion / 2.5f).toInt().coerceIn(2, 6)
            val rad = Math.toRadians(textOverlay.lightAngleDeg.toDouble())
            val stepX = (cos(rad) * 1.5).toFloat()
            val stepY = (sin(rad) * 1.5).toFloat()

            for (i in depthLayers downTo 1) {
                val depthColor = styleColors.last().copy(alpha = 0.9f - (i * 0.1f))
                Text(
                    text = displayText,
                    fontSize = textOverlay.fontSizeSp.sp,
                    fontWeight = fontWt,
                    fontFamily = fontFam,
                    textAlign = textAlign,
                    letterSpacing = textOverlay.letterSpacing.sp,
                    lineHeight = (textOverlay.fontSizeSp * textOverlay.lineSpacing).sp,
                    color = depthColor,
                    modifier = Modifier.graphicsLayer {
                        translationX = stepX * i
                        translationY = stepY * i
                    }
                )
            }
        }

        // Front Face with Gradient, Shadow and Glow
        Text(
            text = displayText,
            fontSize = textOverlay.fontSizeSp.sp,
            fontWeight = fontWt,
            fontFamily = fontFam,
            textAlign = textAlign,
            letterSpacing = textOverlay.letterSpacing.sp,
            lineHeight = (textOverlay.fontSizeSp * textOverlay.lineSpacing).sp,
            style = TextStyle(
                brush = Brush.linearGradient(
                    colors = if (textOverlay.gradientStartHex != null && textOverlay.gradientEndHex != null) {
                        listOf(Color(textOverlay.gradientStartHex), Color(textOverlay.gradientEndHex))
                    } else {
                        listOf(styleColors[0], styleColors[1], styleColors[2])
                    }
                ),
                shadow = Shadow(
                    color = if (textOverlay.glow3D) glowColor else shadowColor,
                    offset = Offset(textOverlay.shadowOffsetX, textOverlay.shadowOffsetY),
                    blurRadius = if (textOverlay.glow3D) 20f else textOverlay.shadowRadius
                )
            )
        )
    }
}
