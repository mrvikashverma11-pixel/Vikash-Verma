package com.example.data.local

import com.example.model.AudioTrack
import com.example.model.EffectOverlay
import com.example.model.FilterType
import com.example.model.MaskType
import com.example.model.MotionEffectType
import com.example.model.Style3DType
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.model.TransitionType
import com.example.model.VideoClip
import org.json.JSONArray
import org.json.JSONObject

object ProjectJsonUtils {

    fun clipsToJson(clips: List<VideoClip>): String {
        val array = JSONArray()
        for (c in clips) {
            val obj = JSONObject().apply {
                put("id", c.id)
                put("name", c.name)
                put("uri", c.uri ?: "")
                put("sampleVisualType", c.sampleVisualType)
                put("startTimeMs", c.startTimeMs)
                put("endTimeMs", c.endTimeMs)
                put("sourceDurationMs", c.sourceDurationMs)
                put("speed", c.speed.toDouble())
                put("isReversed", c.isReversed)
                put("isFrozen", c.isFrozen)
                put("rotateDeg", c.rotateDeg.toDouble())
                put("isFlippedH", c.isFlippedH)
                put("isFlippedV", c.isFlippedV)
                put("zoomScale", c.zoomScale.toDouble())
                put("panX", c.panX.toDouble())
                put("panY", c.panY.toDouble())
                put("blurRadius", c.blurRadius.toDouble())
                put("chromaKeyEnabled", c.chromaKeyEnabled)
                put("chromaKeyColor", c.chromaKeyColor)
                put("maskType", c.maskType.name)
                put("brightness", c.brightness.toDouble())
                put("contrast", c.contrast.toDouble())
                put("saturation", c.saturation.toDouble())
                put("exposure", c.exposure.toDouble())
                put("sharpness", c.sharpness.toDouble())
                put("temperature", c.temperature.toDouble())
                put("filter", c.filter.name)
                put("motionEffect", c.motionEffect.name)
                put("transitionIn", c.transitionIn.name)
                put("isOverlayPip", c.isOverlayPip)
                put("pipScale", c.pipScale.toDouble())
                put("pipX", c.pipX.toDouble())
                put("pipY", c.pipY.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun jsonToClips(jsonStr: String): List<VideoClip> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<VideoClip>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    VideoClip(
                        id = obj.optString("id"),
                        name = obj.optString("name", "Clip"),
                        uri = obj.optString("uri").takeIf { it.isNotBlank() },
                        sampleVisualType = obj.optString("sampleVisualType", "gym"),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        endTimeMs = obj.optLong("endTimeMs", 5000L),
                        sourceDurationMs = obj.optLong("sourceDurationMs", 5000L),
                        speed = obj.optDouble("speed", 1.0).toFloat(),
                        isReversed = obj.optBoolean("isReversed", false),
                        isFrozen = obj.optBoolean("isFrozen", false),
                        rotateDeg = obj.optDouble("rotateDeg", 0.0).toFloat(),
                        isFlippedH = obj.optBoolean("isFlippedH", false),
                        isFlippedV = obj.optBoolean("isFlippedV", false),
                        zoomScale = obj.optDouble("zoomScale", 1.0).toFloat(),
                        panX = obj.optDouble("panX", 0.0).toFloat(),
                        panY = obj.optDouble("panY", 0.0).toFloat(),
                        blurRadius = obj.optDouble("blurRadius", 0.0).toFloat(),
                        chromaKeyEnabled = obj.optBoolean("chromaKeyEnabled", false),
                        chromaKeyColor = obj.optLong("chromaKeyColor", 0xFF00FF00),
                        maskType = runCatching { MaskType.valueOf(obj.optString("maskType")) }.getOrDefault(MaskType.NONE),
                        brightness = obj.optDouble("brightness", 0.0).toFloat(),
                        contrast = obj.optDouble("contrast", 1.0).toFloat(),
                        saturation = obj.optDouble("saturation", 1.0).toFloat(),
                        exposure = obj.optDouble("exposure", 0.0).toFloat(),
                        sharpness = obj.optDouble("sharpness", 0.0).toFloat(),
                        temperature = obj.optDouble("temperature", 0.0).toFloat(),
                        filter = runCatching { FilterType.valueOf(obj.optString("filter")) }.getOrDefault(FilterType.NONE),
                        motionEffect = runCatching { MotionEffectType.valueOf(obj.optString("motionEffect")) }.getOrDefault(MotionEffectType.NONE),
                        transitionIn = runCatching { TransitionType.valueOf(obj.optString("transitionIn")) }.getOrDefault(TransitionType.NONE),
                        isOverlayPip = obj.optBoolean("isOverlayPip", false),
                        pipScale = obj.optDouble("pipScale", 0.35).toFloat(),
                        pipX = obj.optDouble("pipX", 0.3).toFloat(),
                        pipY = obj.optDouble("pipY", -0.3).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun textsToJson(texts: List<TextOverlay>): String {
        val array = JSONArray()
        for (t in texts) {
            val obj = JSONObject().apply {
                put("id", t.id)
                put("text", t.text)
                put("startTimeMs", t.startTimeMs)
                put("endTimeMs", t.endTimeMs)
                put("x", t.x.toDouble())
                put("y", t.y.toDouble())
                put("scale", t.scale.toDouble())
                put("rotation", t.rotation.toDouble())
                put("opacity", t.opacity.toDouble())
                put("fontFamily", t.fontFamily)
                put("fontSizeSp", t.fontSizeSp.toDouble())
                put("textColorHex", t.textColorHex)
                put("gradientStartHex", t.gradientStartHex ?: 0L)
                put("gradientEndHex", t.gradientEndHex ?: 0L)
                put("isBold", t.isBold)
                put("isItalic", t.isItalic)
                put("outlineColorHex", t.outlineColorHex)
                put("outlineWidth", t.outlineWidth.toDouble())
                put("shadowColorHex", t.shadowColorHex)
                put("shadowRadius", t.shadowRadius.toDouble())
                put("shadowOffsetX", t.shadowOffsetX.toDouble())
                put("shadowOffsetY", t.shadowOffsetY.toDouble())
                put("glowColorHex", t.glowColorHex)
                put("glowRadius", t.glowRadius.toDouble())
                put("backgroundBoxColorHex", t.backgroundBoxColorHex)
                put("backgroundBoxPadding", t.backgroundBoxPadding.toDouble())
                put("backgroundBoxCornerRadius", t.backgroundBoxCornerRadius.toDouble())
                put("letterSpacing", t.letterSpacing.toDouble())
                put("lineSpacing", t.lineSpacing.toDouble())
                put("alignment", t.alignment)
                put("animation", t.animation.name)
                put("is3D", t.is3D)
                put("depth3D", t.depth3D.toDouble())
                put("extrusion", t.extrusion.toDouble())
                put("rotationX", t.rotationX.toDouble())
                put("rotationY", t.rotationY.toDouble())
                put("rotationZ", t.rotationZ.toDouble())
                put("style3D", t.style3D.name)
                put("shadow3D", t.shadow3D)
                put("glow3D", t.glow3D)
                put("lightAngleDeg", t.lightAngleDeg.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun jsonToTexts(jsonStr: String): List<TextOverlay> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<TextOverlay>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    TextOverlay(
                        id = obj.optString("id"),
                        text = obj.optString("text", "MOTIVATION"),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        endTimeMs = obj.optLong("endTimeMs", 4000L),
                        x = obj.optDouble("x", 0.5).toFloat(),
                        y = obj.optDouble("y", 0.5).toFloat(),
                        scale = obj.optDouble("scale", 1.0).toFloat(),
                        rotation = obj.optDouble("rotation", 0.0).toFloat(),
                        opacity = obj.optDouble("opacity", 1.0).toFloat(),
                        fontFamily = obj.optString("fontFamily", "Cinematic Impact"),
                        fontSizeSp = obj.optDouble("fontSizeSp", 28.0).toFloat(),
                        textColorHex = obj.optLong("textColorHex", 0xFFFFFFFF),
                        gradientStartHex = obj.optLong("gradientStartHex", 0xFFFFD700).takeIf { it != 0L },
                        gradientEndHex = obj.optLong("gradientEndHex", 0xFFFF8C00).takeIf { it != 0L },
                        isBold = obj.optBoolean("isBold", true),
                        isItalic = obj.optBoolean("isItalic", false),
                        outlineColorHex = obj.optLong("outlineColorHex", 0xFF000000),
                        outlineWidth = obj.optDouble("outlineWidth", 4.0).toFloat(),
                        shadowColorHex = obj.optLong("shadowColorHex", 0xAA000000),
                        shadowRadius = obj.optDouble("shadowRadius", 12.0).toFloat(),
                        shadowOffsetX = obj.optDouble("shadowOffsetX", 4.0).toFloat(),
                        shadowOffsetY = obj.optDouble("shadowOffsetY", 4.0).toFloat(),
                        glowColorHex = obj.optLong("glowColorHex", 0xFFFFD700),
                        glowRadius = obj.optDouble("glowRadius", 0.0).toFloat(),
                        backgroundBoxColorHex = obj.optLong("backgroundBoxColorHex", 0x00000000),
                        backgroundBoxPadding = obj.optDouble("backgroundBoxPadding", 8.0).toFloat(),
                        backgroundBoxCornerRadius = obj.optDouble("backgroundBoxCornerRadius", 8.0).toFloat(),
                        letterSpacing = obj.optDouble("letterSpacing", 2.0).toFloat(),
                        lineSpacing = obj.optDouble("lineSpacing", 1.2).toFloat(),
                        alignment = obj.optString("alignment", "Center"),
                        animation = runCatching { TextAnimationType.valueOf(obj.optString("animation")) }.getOrDefault(TextAnimationType.BOUNCE_POP),
                        is3D = obj.optBoolean("is3D", true),
                        depth3D = obj.optDouble("depth3D", 16.0).toFloat(),
                        extrusion = obj.optDouble("extrusion", 12.0).toFloat(),
                        rotationX = obj.optDouble("rotationX", 12.0).toFloat(),
                        rotationY = obj.optDouble("rotationY", -15.0).toFloat(),
                        rotationZ = obj.optDouble("rotationZ", 0.0).toFloat(),
                        style3D = runCatching { Style3DType.valueOf(obj.optString("style3D")) }.getOrDefault(Style3DType.METALLIC_GOLD),
                        shadow3D = obj.optBoolean("shadow3D", true),
                        glow3D = obj.optBoolean("glow3D", true),
                        lightAngleDeg = obj.optDouble("lightAngleDeg", 45.0).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun audiosToJson(audios: List<AudioTrack>): String {
        val array = JSONArray()
        for (a in audios) {
            val obj = JSONObject().apply {
                put("id", a.id)
                put("title", a.title)
                put("artist", a.artist)
                put("uri", a.uri ?: "")
                put("sampleTrackKey", a.sampleTrackKey)
                put("startTimeMs", a.startTimeMs)
                put("durationMs", a.durationMs)
                put("volume", a.volume.toDouble())
                put("fadeInMs", a.fadeInMs)
                put("fadeOutMs", a.fadeOutMs)
                put("isExtracted", a.isExtracted)
                put("isVoiceOver", a.isVoiceOver)
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun jsonToAudios(jsonStr: String): List<AudioTrack> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<AudioTrack>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AudioTrack(
                        id = obj.optString("id"),
                        title = obj.optString("title", "Audio Track"),
                        artist = obj.optString("artist", "VV Studio"),
                        uri = obj.optString("uri").takeIf { it.isNotBlank() },
                        sampleTrackKey = obj.optString("sampleTrackKey", "epic_orchestra"),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        durationMs = obj.optLong("durationMs", 15000L),
                        volume = obj.optDouble("volume", 0.85).toFloat(),
                        fadeInMs = obj.optLong("fadeInMs", 500L),
                        fadeOutMs = obj.optLong("fadeOutMs", 800L),
                        isExtracted = obj.optBoolean("isExtracted", false),
                        isVoiceOver = obj.optBoolean("isVoiceOver", false)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun effectsToJson(effects: List<EffectOverlay>): String {
        val array = JSONArray()
        for (e in effects) {
            val obj = JSONObject().apply {
                put("id", e.id)
                put("effectType", e.effectType.name)
                put("startTimeMs", e.startTimeMs)
                put("endTimeMs", e.endTimeMs)
                put("intensity", e.intensity.toDouble())
            }
            array.put(obj)
        }
        return array.toString()
    }

    fun jsonToEffects(jsonStr: String): List<EffectOverlay> {
        if (jsonStr.isBlank()) return emptyList()
        val list = mutableListOf<EffectOverlay>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    EffectOverlay(
                        id = obj.optString("id"),
                        effectType = runCatching { MotionEffectType.valueOf(obj.optString("effectType")) }.getOrDefault(MotionEffectType.IMPACT_SHAKE),
                        startTimeMs = obj.optLong("startTimeMs", 0L),
                        endTimeMs = obj.optLong("endTimeMs", 2500L),
                        intensity = obj.optDouble("intensity", 0.8).toFloat()
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
