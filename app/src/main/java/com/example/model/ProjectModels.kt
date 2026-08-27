package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

enum class AspectRatioType(val title: String, val ratio: Float, val iconLabel: String, val desc: String) {
    REEL_9_16("9:16", 9f / 16f, "Reel / Shorts", "TikTok, Reels & Shorts"),
    YOUTUBE_16_9("16:9", 16f / 9f, "YouTube", "Landscape standard"),
    SQUARE_1_1("1:1", 1f, "Instagram 1:1", "Square feed posts"),
    PORTRAIT_4_5("4:5", 4f / 5f, "Instagram 4:5", "Portrait feed posts");

    val displayName: String get() = title
}

enum class ExportResolution(val label: String, val width: Int, val height: Int, val isPremium: Boolean) {
    HD_720P("720p HD", 720, 1280, false),
    FHD_1080P("1080p FHD", 1080, 1920, false),
    QHD_2K("2K QHD", 1440, 2560, true),
    UHD_4K("4K Ultra HD", 2160, 3840, true)
}

enum class ExportFps(val fps: Int, val label: String) {
    FPS_24(24, "24 FPS (Cinematic)"),
    FPS_30(30, "30 FPS (Standard)"),
    FPS_60(60, "60 FPS (Ultra Smooth)")
}

enum class FilterType(val displayName: String, val intensity: Float = 1f) {
    NONE("Original"),
    CINEMATIC_GOLD("Moody Gold"),
    CYBERPUNK("Cyberpunk"),
    MOODY_DARK("Dark Noir"),
    MONOCHROME("B&W Grit"),
    VHS_VIBE("VHS Retro"),
    WARM_SUNRISE("Golden Hour"),
    BLEACH_BYPASS("Bleach Bypass"),
    NEON_NIGHT("Neon Pulse"),
    VINTAGE_FILM("1980s Film"),
    EMERALD_FADE("Emerald Tone"),
    HIGH_DRAMA("High Contrast")
}

enum class MotionEffectType(val displayName: String) {
    NONE("None"),
    IMPACT_SHAKE("Impact Shake"),
    GLITCH_SPLIT("RGB Glitch"),
    STROBE_FLASH("Strobe Flash"),
    VHS_SCANLINES("VHS Scanlines"),
    ZOOM_PULSE("Beat Pulse"),
    RGB_DRIFT("Chromatic Drift"),
    FILM_GRAIN("Film Grain"),
    SPARKLE_RAIN("Golden Sparks")
}

enum class TransitionType(val displayName: String) {
    NONE("Cut"),
    CROSS_FADE("Fade"),
    ZOOM_IN("Zoom In"),
    ZOOM_OUT("Zoom Out"),
    SLIDE_LEFT("Slide Left"),
    SLIDE_UP("Slide Up"),
    GLITCH_CUT("Glitch Warp"),
    WHIP_PAN("Whip Pan"),
    FLASH_TRANSITION("White Flash")
}

enum class MaskType(val displayName: String) {
    NONE("None"),
    CIRCLE("Circle"),
    ROUNDED_RECT("Rounded Box"),
    STAR("Star"),
    DIAMOND("Diamond"),
    HEART("Heart"),
    LINEAR_GRADIENT("Gradient Vignette")
}

enum class TextAnimationType(val displayName: String) {
    NONE("Static"),
    TYPEWRITER("Typewriter"),
    FADE_IN("Smooth Fade"),
    BOUNCE_POP("Bounce Pop"),
    ZOOM_IN("Cinematic Zoom"),
    SLIDE_UP("Slide Up"),
    DYNAMIC_SHAKE("Heavy Shake"),
    GLITCH_FLICKER("Glitch Glow"),
    WORD_BY_WORD("Word By Word"),
    GLOW_PULSE("Neon Pulse")
}

enum class Style3DType(val displayName: String, val primaryHex: Long, val secondaryHex: Long, val isPremium: Boolean) {
    METALLIC_GOLD("Gold Bar", 0xFFFFD700, 0xFFB8860B, false),
    NEON_ELECTRIC("Neon Cyan", 0xFF00E5FF, 0xFF0077B6, false),
    CHROME_MIRROR("Chrome Mirror", 0xFFE0E0E0, 0xFF757575, true),
    OBSIDIAN_GLOSS("Obsidian Stealth", 0xFF2D3748, 0xFF1A202C, true),
    CRIMSON_STEEL("Crimson Rage", 0xFFFF1744, 0xFF880E4F, true),
    HOLOGRAM_CYAN("Holo Matrix", 0xFF00FFCC, 0xFF7928CA, true),
    ROSE_GOLD("Rose Gold Luxury", 0xFFFFB5A7, 0xFFD8817B, true),
    LIQUID_BRONZE("Raw Bronze", 0xFFCD7F32, 0xFF8B4513, false)
}

enum class TemplateCategory(val title: String) {
    ALL("All"),
    GYM("Gym Motivation"),
    SUCCESS("Success & Wealth"),
    STUDY("Study & Focus"),
    BUSINESS("Business & Hustle"),
    LIFE("Life Wisdom"),
    ATTITUDE("Attitude & Alpha"),
    INSPIRATIONAL("Inspirational");

    val displayName: String get() = title
}

data class VideoClip(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Clip",
    val uri: String? = null,
    val sampleVisualType: String = "gym", // "gym", "success", "study", "attitude", "nature", "neon"
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 5000L,
    val sourceDurationMs: Long = 5000L,
    val speed: Float = 1.0f,
    val isReversed: Boolean = false,
    val isFrozen: Boolean = false,
    val rotateDeg: Float = 0f,
    val isFlippedH: Boolean = false,
    val isFlippedV: Boolean = false,
    val zoomScale: Float = 1.0f,
    val panX: Float = 0f,
    val panY: Float = 0f,
    val blurRadius: Float = 0f,
    val chromaKeyEnabled: Boolean = false,
    val chromaKeyColor: Long = 0xFF00FF00,
    val maskType: MaskType = MaskType.NONE,
    val brightness: Float = 0f, // -100 to 100
    val contrast: Float = 1.0f, // 0.5 to 2.0
    val saturation: Float = 1.0f, // 0 to 2.0
    val exposure: Float = 0f, // -100 to 100
    val sharpness: Float = 0f, // 0 to 100
    val temperature: Float = 0f, // -100 (cold) to 100 (warm)
    val filter: FilterType = FilterType.NONE,
    val motionEffect: MotionEffectType = MotionEffectType.NONE,
    val transitionIn: TransitionType = TransitionType.NONE,
    val isOverlayPip: Boolean = false,
    val pipScale: Float = 0.35f,
    val pipX: Float = 0.3f,
    val pipY: Float = -0.3f
) {
    val durationMs: Long get() = ((endTimeMs - startTimeMs) / speed).toLong().coerceAtLeast(100L)
}

data class TextOverlay(
    val id: String = UUID.randomUUID().toString(),
    val text: String = "NEVER GIVE UP",
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 4000L,
    val x: Float = 0.5f, // Normalized 0..1
    val y: Float = 0.5f, // Normalized 0..1
    val scale: Float = 1.0f,
    val rotation: Float = 0f,
    val opacity: Float = 1.0f,
    val fontFamily: String = "Cinematic Impact", // "Cinematic Impact", "Hindi Devanagari", "Modern Bold", "Serif Luxury", "Cyberpunk Heavy"
    val fontSizeSp: Float = 28f,
    val textColorHex: Long = 0xFFFFFFFF,
    val gradientStartHex: Long? = 0xFFFFD700,
    val gradientEndHex: Long? = 0xFFFF8C00,
    val isBold: Boolean = true,
    val isItalic: Boolean = false,
    val outlineColorHex: Long = 0xFF000000,
    val outlineWidth: Float = 4f,
    val shadowColorHex: Long = 0xAA000000,
    val shadowRadius: Float = 12f,
    val shadowOffsetX: Float = 4f,
    val shadowOffsetY: Float = 4f,
    val glowColorHex: Long = 0xFFFFD700,
    val glowRadius: Float = 0f,
    val backgroundBoxColorHex: Long = 0x00000000,
    val backgroundBoxPadding: Float = 8f,
    val backgroundBoxCornerRadius: Float = 8f,
    val letterSpacing: Float = 2f,
    val lineSpacing: Float = 1.2f,
    val alignment: String = "Center", // "Left", "Center", "Right"
    val animation: TextAnimationType = TextAnimationType.BOUNCE_POP,
    // 3D Text Specifics
    val is3D: Boolean = true,
    val depth3D: Float = 16f,
    val extrusion: Float = 12f,
    val rotationX: Float = 12f,
    val rotationY: Float = -15f,
    val rotationZ: Float = 0f,
    val style3D: Style3DType = Style3DType.METALLIC_GOLD,
    val shadow3D: Boolean = true,
    val glow3D: Boolean = true,
    val lightAngleDeg: Float = 45f
)

data class AudioTrack(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Epic Motivational Theme",
    val artist: String = "VV Beats Studio",
    val uri: String? = null,
    val sampleTrackKey: String = "epic_orchestra", // "epic_orchestra", "phonk_drive", "gym_trap", "deep_stoic", "cinematic_piano"
    val startTimeMs: Long = 0L,
    val durationMs: Long = 15000L,
    val volume: Float = 0.85f,
    val fadeInMs: Long = 500L,
    val fadeOutMs: Long = 800L,
    val isExtracted: Boolean = false,
    val isVoiceOver: Boolean = false,
    val waveformPoints: List<Float> = listOf(0.2f, 0.4f, 0.7f, 0.9f, 0.6f, 0.85f, 1f, 0.75f, 0.5f, 0.9f, 0.6f, 0.3f, 0.8f, 0.95f, 0.7f, 0.4f, 0.6f, 0.9f, 0.5f, 0.3f)
)

data class EffectOverlay(
    val id: String = UUID.randomUUID().toString(),
    val effectType: MotionEffectType = MotionEffectType.IMPACT_SHAKE,
    val startTimeMs: Long = 0L,
    val endTimeMs: Long = 2500L,
    val intensity: Float = 0.8f
)

data class ProjectData(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Motivation Reel #1",
    val aspectRatio: AspectRatioType = AspectRatioType.REEL_9_16,
    val resolution: ExportResolution = ExportResolution.FHD_1080P,
    val fps: ExportFps = ExportFps.FPS_30,
    val clips: List<VideoClip> = emptyList(),
    val texts: List<TextOverlay> = emptyList(),
    val audios: List<AudioTrack> = emptyList(),
    val effects: List<EffectOverlay> = emptyList(),
    val isAutosaved: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
) {
    val totalDurationMs: Long
        get() {
            val clipEnd = clips.maxOfOrNull { it.endTimeMs } ?: 5000L
            val textEnd = texts.maxOfOrNull { it.endTimeMs } ?: 0L
            val audioEnd = audios.maxOfOrNull { it.startTimeMs + it.durationMs } ?: 0L
            val effectEnd = effects.maxOfOrNull { it.endTimeMs } ?: 0L
            return maxOf(clipEnd, textEnd, audioEnd, effectEnd, 3000L)
        }
}

data class MotivationalTemplate(
    val id: String,
    val title: String,
    val category: TemplateCategory,
    val description: String,
    val durationSec: Int,
    val aspectRatio: AspectRatioType,
    val sampleVisualType: String,
    val defaultQuoteHindi: String,
    val defaultQuoteEnglish: String,
    val style3D: Style3DType,
    val filter: FilterType,
    val motionEffect: MotionEffectType,
    val audioTitle: String,
    val isPremium: Boolean
)

data class Preset3DTitle(
    val id: String,
    val englishText: String,
    val hindiText: String,
    val style3D: Style3DType,
    val animation: TextAnimationType,
    val tag: String
)
