package com.example.data

import com.example.model.AspectRatioType
import com.example.model.AudioTrack
import com.example.model.EffectOverlay
import com.example.model.FilterType
import com.example.model.MotionEffectType
import com.example.model.MotivationalTemplate
import com.example.model.Preset3DTitle
import com.example.model.ProjectData
import com.example.model.Style3DType
import com.example.model.TemplateCategory
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.model.TransitionType
import com.example.model.VideoClip
import java.util.UUID

object TemplateRepository {

    val templates: List<MotivationalTemplate> = listOf(
        MotivationalTemplate(
            id = "tmpl_gym_1",
            title = "Unstoppable Beast",
            category = TemplateCategory.GYM,
            description = "High energy gym motivation reel with 3D metallic gold title & impact beat drops.",
            durationSec = 15,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "gym",
            defaultQuoteHindi = "मेहनत कभी बेकार नहीं जाती",
            defaultQuoteEnglish = "CONQUER YOUR LIMITS",
            style3D = Style3DType.METALLIC_GOLD,
            filter = FilterType.CINEMATIC_GOLD,
            motionEffect = MotionEffectType.IMPACT_SHAKE,
            audioTitle = "Heavy Gym Trap Drive",
            isPremium = false
        ),
        MotivationalTemplate(
            id = "tmpl_success_1",
            title = "Empire In Silence",
            category = TemplateCategory.SUCCESS,
            description = "Sleek dark cyberpunk billionaire mindset reel with reflective 3D chrome typography.",
            durationSec = 18,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "success",
            defaultQuoteHindi = "सपने पूरे करने हैं",
            defaultQuoteEnglish = "BUILD YOUR EMPIRE",
            style3D = Style3DType.CHROME_MIRROR,
            filter = FilterType.CYBERPUNK,
            motionEffect = MotionEffectType.GLITCH_SPLIT,
            audioTitle = "Phonk Hustle Drive",
            isPremium = true
        ),
        MotivationalTemplate(
            id = "tmpl_study_1",
            title = "Midnight Focus",
            category = TemplateCategory.STUDY,
            description = "Deep stoic late-night study reel with electric neon text and subtle film grain.",
            durationSec = 12,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "study",
            defaultQuoteHindi = "मेहनत करते रहो",
            defaultQuoteEnglish = "DISCIPLINE IS FREEDOM",
            style3D = Style3DType.NEON_ELECTRIC,
            filter = FilterType.MOODY_DARK,
            motionEffect = MotionEffectType.FILM_GRAIN,
            audioTitle = "Deep Ambient Focus",
            isPremium = false
        ),
        MotivationalTemplate(
            id = "tmpl_attitude_1",
            title = "Warrior Mindset",
            category = TemplateCategory.ATTITUDE,
            description = "Aggressive alpha motivation reel with crimson 3D steel and strobe impact.",
            durationSec = 15,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "attitude",
            defaultQuoteHindi = "हार मत मानना",
            defaultQuoteEnglish = "NEVER SURRENDER",
            style3D = Style3DType.CRIMSON_STEEL,
            filter = FilterType.HIGH_DRAMA,
            motionEffect = MotionEffectType.STROBE_FLASH,
            audioTitle = "War Horns & Epic Brass",
            isPremium = false
        ),
        MotivationalTemplate(
            id = "tmpl_biz_1",
            title = "Billionaire Routine",
            category = TemplateCategory.BUSINESS,
            description = "High-ticket business hustle reel with 3D obsidian matte letters and VHS aesthetic.",
            durationSec = 20,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "success",
            defaultQuoteHindi = "समय आपका है",
            defaultQuoteEnglish = "EXECUTE IN SILENCE",
            style3D = Style3DType.OBSIDIAN_GLOSS,
            filter = FilterType.BLEACH_BYPASS,
            motionEffect = MotionEffectType.VHS_SCANLINES,
            audioTitle = "Dark Capital Beats",
            isPremium = true
        ),
        MotivationalTemplate(
            id = "tmpl_inspire_1",
            title = "The Great Comeback",
            category = TemplateCategory.INSPIRATIONAL,
            description = "Uplifting transformation reel with glowing 3D hologram and golden sparks.",
            durationSec = 16,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "gym",
            defaultQuoteHindi = "विश्वास खुद पर रखो",
            defaultQuoteEnglish = "THE COMEBACK IS STRONGER",
            style3D = Style3DType.HOLOGRAM_CYAN,
            filter = FilterType.WARM_SUNRISE,
            motionEffect = MotionEffectType.SPARKLE_RAIN,
            audioTitle = "Inspiring Piano Crescendo",
            isPremium = false
        ),
        MotivationalTemplate(
            id = "tmpl_life_1",
            title = "Stoic Wisdom",
            category = TemplateCategory.LIFE,
            description = "Timeless philosophy & life guidance with bronze 3D typography and vintage tones.",
            durationSec = 14,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "study",
            defaultQuoteHindi = "मंजिल पाना तय है",
            defaultQuoteEnglish = "PAIN IS TEMPORARY",
            style3D = Style3DType.LIQUID_BRONZE,
            filter = FilterType.VINTAGE_FILM,
            motionEffect = MotionEffectType.ZOOM_PULSE,
            audioTitle = "Cinematic Strings Symphony",
            isPremium = false
        ),
        MotivationalTemplate(
            id = "tmpl_gym_2",
            title = "No Mercy Grind",
            category = TemplateCategory.GYM,
            description = "Fast-paced workout montage with rose gold 3D depth and chromatic drift.",
            durationSec = 15,
            aspectRatio = AspectRatioType.REEL_9_16,
            sampleVisualType = "attitude",
            defaultQuoteHindi = "रुकना मना है",
            defaultQuoteEnglish = "SWEAT NOW SHINE LATER",
            style3D = Style3DType.ROSE_GOLD,
            filter = FilterType.NEON_NIGHT,
            motionEffect = MotionEffectType.RGB_DRIFT,
            audioTitle = "Adrenaline Rush Drop",
            isPremium = true
        )
    )

    val preset3DTitles: List<Preset3DTitle> = listOf(
        Preset3DTitle("p1", "NEVER GIVE UP", "हार मत मानना", Style3DType.METALLIC_GOLD, TextAnimationType.BOUNCE_POP, "Legendary"),
        Preset3DTitle("p2", "KEEP MOVING", "चलते रहो", Style3DType.NEON_ELECTRIC, TextAnimationType.ZOOM_IN, "Momentum"),
        Preset3DTitle("p3", "BELIEVE IN YOURSELF", "खुद पर विश्वास रखो", Style3DType.CHROME_MIRROR, TextAnimationType.GLOW_PULSE, "Faith"),
        Preset3DTitle("p4", "GRIND EVERY DAY", "मेहनत करते रहो", Style3DType.METALLIC_GOLD, TextAnimationType.TYPEWRITER, "Hardwork"),
        Preset3DTitle("p5", "DREAMS TO REALITY", "सपने पूरे करने हैं", Style3DType.HOLOGRAM_CYAN, TextAnimationType.WORD_BY_WORD, "Aspiration"),
        Preset3DTitle("p6", "DISCIPLINE > MOTIVATION", "अनुशासन ही जीत है", Style3DType.OBSIDIAN_GLOSS, TextAnimationType.SLIDE_UP, "Stoic"),
        Preset3DTitle("p7", "RISE & CONQUER", "उठो और जीतो", Style3DType.CRIMSON_STEEL, TextAnimationType.DYNAMIC_SHAKE, "Warrior"),
        Preset3DTitle("p8", "NO EXCUSES", "कोई बहाना नहीं", Style3DType.ROSE_GOLD, TextAnimationType.GLITCH_FLICKER, "Alpha"),
        Preset3DTitle("p9", "SILENCE THE DOUBTERS", "खामोशी से वार करो", Style3DType.LIQUID_BRONZE, TextAnimationType.FADE_IN, "Focus"),
        Preset3DTitle("p10", "PROVE THEM WRONG", "खुद को साबित करो", Style3DType.NEON_ELECTRIC, TextAnimationType.BOUNCE_POP, "Pride")
    )

    fun createProjectFromTemplate(template: MotivationalTemplate): ProjectData {
        val clipId1 = UUID.randomUUID().toString()
        val clipId2 = UUID.randomUUID().toString()
        val textId1 = UUID.randomUUID().toString()
        val textId2 = UUID.randomUUID().toString()
        val audioId = UUID.randomUUID().toString()
        val effectId = UUID.randomUUID().toString()

        val clip1 = VideoClip(
            id = clipId1,
            name = "Scene 1",
            sampleVisualType = template.sampleVisualType,
            startTimeMs = 0L,
            endTimeMs = 7000L,
            sourceDurationMs = 7000L,
            filter = template.filter,
            motionEffect = template.motionEffect,
            transitionIn = TransitionType.CROSS_FADE
        )
        val clip2 = VideoClip(
            id = clipId2,
            name = "Scene 2",
            sampleVisualType = if (template.sampleVisualType == "gym") "attitude" else "success",
            startTimeMs = 7000L,
            endTimeMs = template.durationSec * 1000L,
            sourceDurationMs = (template.durationSec * 1000L) - 7000L,
            filter = template.filter,
            motionEffect = template.motionEffect,
            transitionIn = TransitionType.ZOOM_IN
        )

        val text1 = TextOverlay(
            id = textId1,
            text = template.defaultQuoteEnglish,
            startTimeMs = 500L,
            endTimeMs = 7000L,
            x = 0.5f,
            y = 0.45f,
            fontSizeSp = 32f,
            is3D = true,
            depth3D = 18f,
            extrusion = 14f,
            rotationX = 14f,
            rotationY = -12f,
            style3D = template.style3D,
            animation = TextAnimationType.BOUNCE_POP
        )

        val text2 = TextOverlay(
            id = textId2,
            text = template.defaultQuoteHindi,
            startTimeMs = 7200L,
            endTimeMs = (template.durationSec * 1000L) - 500L,
            x = 0.5f,
            y = 0.52f,
            fontFamily = "Hindi Devanagari",
            fontSizeSp = 30f,
            is3D = true,
            depth3D = 16f,
            extrusion = 12f,
            rotationX = 10f,
            rotationY = 10f,
            style3D = template.style3D,
            animation = TextAnimationType.WORD_BY_WORD
        )

        val audio = AudioTrack(
            id = audioId,
            title = template.audioTitle,
            artist = "VV Motivation Audio",
            sampleTrackKey = when (template.category) {
                TemplateCategory.GYM -> "gym_trap"
                TemplateCategory.SUCCESS -> "phonk_drive"
                TemplateCategory.STUDY -> "deep_stoic"
                TemplateCategory.ATTITUDE -> "epic_orchestra"
                else -> "cinematic_piano"
            },
            startTimeMs = 0L,
            durationMs = template.durationSec * 1000L,
            volume = 0.9f
        )

        val effect = EffectOverlay(
            id = effectId,
            effectType = template.motionEffect,
            startTimeMs = 0L,
            endTimeMs = template.durationSec * 1000L,
            intensity = 0.75f
        )

        return ProjectData(
            id = UUID.randomUUID().toString(),
            title = template.title,
            aspectRatio = template.aspectRatio,
            clips = listOf(clip1, clip2),
            texts = listOf(text1, text2),
            audios = listOf(audio),
            effects = listOf(effect),
            isAutosaved = true,
            updatedAt = System.currentTimeMillis()
        )
    }

    fun createBlankProject(title: String, ratio: AspectRatioType): ProjectData {
        val clipId = UUID.randomUUID().toString()
        val textId = UUID.randomUUID().toString()
        val audioId = UUID.randomUUID().toString()

        val clip = VideoClip(
            id = clipId,
            name = "Initial Clip",
            sampleVisualType = "gym",
            startTimeMs = 0L,
            endTimeMs = 6000L,
            sourceDurationMs = 6000L,
            filter = FilterType.CINEMATIC_GOLD
        )

        val text = TextOverlay(
            id = textId,
            text = "NEVER GIVE UP",
            startTimeMs = 0L,
            endTimeMs = 5000L,
            fontSizeSp = 30f,
            is3D = true,
            style3D = Style3DType.METALLIC_GOLD,
            animation = TextAnimationType.BOUNCE_POP
        )

        val audio = AudioTrack(
            id = audioId,
            title = "Epic Motivation Beats",
            artist = "VV Beats Studio",
            sampleTrackKey = "epic_orchestra",
            startTimeMs = 0L,
            durationMs = 6000L
        )

        return ProjectData(
            id = UUID.randomUUID().toString(),
            title = title,
            aspectRatio = ratio,
            clips = listOf(clip),
            texts = listOf(text),
            audios = listOf(audio),
            effects = emptyList(),
            isAutosaved = true,
            updatedAt = System.currentTimeMillis()
        )
    }
}
