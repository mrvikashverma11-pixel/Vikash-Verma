package com.example.ui.editor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AutoCaptionSegment
import com.example.ai.GeminiAiService
import com.example.model.AudioTrack
import com.example.model.Style3DType
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AiToolsSheet(
    videoDurationMs: Long,
    onAddAutoCaptions: (List<TextOverlay>) -> Unit,
    onAddVoiceOver: (AudioTrack, String) -> Unit,
    onPlayTts: (String, Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var activeAiTab by remember { mutableStateOf("captions") } // "captions", "voice_over", "hooks"

    // Captions State
    var captionLang by remember { mutableStateOf("Hindi & English (Hinglish)") }
    var captionTopic by remember { mutableStateOf("Never give up, hard work & success") }
    var isGeneratingCaptions by remember { mutableStateOf(false) }

    // Voice-over State
    var voicePersona by remember { mutableStateOf("Gym Beast & Aggressive Hustle") }
    var voiceTopic by remember { mutableStateOf("Morning grind and mental toughness") }
    var generatedScript by remember { mutableStateOf("") }
    var isGeneratingVoice by remember { mutableStateOf(false) }

    // Hooks State
    var generatedHooks by remember { mutableStateOf<List<String>>(emptyList()) }
    var isGeneratingHooks by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(ObsidianCard)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MotivationGold, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                Text("AI Creator Suite (Gemini 2.5)", color = MotivationGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "Close",
                color = TextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.clickable(onClick = onDismiss).padding(4.dp)
            )
        }

        // Subtabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AiSubTabChip("Auto Captions", activeAiTab == "captions") { activeAiTab = "captions" }
            AiSubTabChip("AI Voice-over", activeAiTab == "voice_over") { activeAiTab = "voice_over" }
            AiSubTabChip("Viral Quotes / Hooks", activeAiTab == "hooks") { activeAiTab = "hooks" }
        }

        // --- TAB 1: AUTO CAPTIONS ---
        if (activeAiTab == "captions") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Auto Captions Generator", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text("Generates animated 3D word-by-word synced subtitles for your reel.", color = TextSecondary, fontSize = 11.sp)

                OutlinedTextField(
                    value = captionTopic,
                    onValueChange = { captionTopic = it },
                    label = { Text("Video Topic / Core Theme", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MotivationGold, unfocusedBorderColor = ObsidianBorder),
                    shape = RoundedCornerShape(8.dp)
                )

                // Language options
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("English", "Hindi", "Hinglish").forEach { lang ->
                        val isSel = captionLang.contains(lang, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) MotivationGold else ObsidianCard)
                                .clickable { captionLang = lang }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(lang, color = if (isSel) Color.Black else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGeneratingCaptions) TextMuted else MotivationGold)
                        .clickable(enabled = !isGeneratingCaptions) {
                            isGeneratingCaptions = true
                            coroutineScope.launch {
                                val segments = GeminiAiService.generateAutoCaptions(captionTopic, videoDurationMs, captionLang)
                                val textOverlays = segments.map { seg ->
                                    TextOverlay(
                                        id = UUID.randomUUID().toString(),
                                        text = seg.text,
                                        startTimeMs = seg.startTimeMs,
                                        endTimeMs = seg.endTimeMs,
                                        fontSizeSp = 28f,
                                        is3D = true,
                                        style3D = Style3DType.METALLIC_GOLD,
                                        animation = TextAnimationType.WORD_BY_WORD
                                    )
                                }
                                onAddAutoCaptions(textOverlays)
                                isGeneratingCaptions = false
                                onDismiss()
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGeneratingCaptions) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("⚡ Generate & Add Auto Captions", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // --- TAB 2: AI VOICE-OVER ---
        if (activeAiTab == "voice_over") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = ElectricPink, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("AI Motivational Voice-Over", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedTextField(
                    value = voiceTopic,
                    onValueChange = { voiceTopic = it },
                    label = { Text("Script Topic / Hook", color = TextSecondary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricPink, unfocusedBorderColor = ObsidianBorder),
                    shape = RoundedCornerShape(8.dp)
                )

                // Voice Personas
                Text("Speaker Persona:", color = TextSecondary, fontSize = 11.sp)
                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Gym Beast Alpha", "Stoic Leader", "Inspiring Mentor", "Hindi Josh Guru").forEach { persona ->
                        val isSel = voicePersona == persona
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) ElectricPink else ObsidianCard)
                                .clickable { voicePersona = persona }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(persona, color = if (isSel) Color.Black else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGeneratingVoice) TextMuted else ElectricPink)
                        .clickable(enabled = !isGeneratingVoice) {
                            isGeneratingVoice = true
                            coroutineScope.launch {
                                generatedScript = GeminiAiService.generateVoiceOverScript(voiceTopic, voicePersona)
                                isGeneratingVoice = false
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGeneratingVoice) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("✨ Generate AI Script", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                if (generatedScript.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ObsidianCard)
                            .padding(10.dp)
                    ) {
                        Text("Script Preview:", color = MotivationGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(generatedScript, color = TextPrimary, fontSize = 12.sp, lineHeight = 16.sp)

                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ObsidianSurfaceVariant)
                                    .clickable {
                                        val isHindi = voicePersona.contains("Hindi")
                                        onPlayTts(generatedScript, isHindi)
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MotivationGold, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Listen TTS", color = MotivationGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MotivationGold)
                                    .clickable {
                                        val audioTrack = AudioTrack(
                                            id = UUID.randomUUID().toString(),
                                            title = "AI Voice: $voicePersona",
                                            sampleTrackKey = "deep_stoic",
                                            durationMs = videoDurationMs,
                                            isVoiceOver = true
                                        )
                                        onAddVoiceOver(audioTrack, generatedScript)
                                        onDismiss()
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("+ Add to Reel", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // --- TAB 3: VIRAL QUOTES / HOOKS ---
        if (activeAiTab == "hooks") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianSurfaceVariant)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Viral Reel Quotes & Hooks", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("Instant punchy motivational hooks to maximize reel watch-time:", color = TextSecondary, fontSize = 11.sp)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isGeneratingHooks) TextMuted else MotivationGold)
                        .clickable(enabled = !isGeneratingHooks) {
                            isGeneratingHooks = true
                            coroutineScope.launch {
                                generatedHooks = GeminiAiService.generateMotivationalCaptions(captionTopic)
                                isGeneratingHooks = false
                            }
                        }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGeneratingHooks) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Text("Generate 5 Viral Hooks", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                if (generatedHooks.isNotEmpty()) {
                    generatedHooks.forEach { hook ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ObsidianCard)
                                .clickable {
                                    val overlay = TextOverlay(
                                        id = UUID.randomUUID().toString(),
                                        text = hook,
                                        startTimeMs = 0L,
                                        endTimeMs = 5000L,
                                        fontSizeSp = 30f,
                                        is3D = true,
                                        style3D = Style3DType.METALLIC_GOLD,
                                        animation = TextAnimationType.BOUNCE_POP
                                    )
                                    onAddAutoCaptions(listOf(overlay))
                                    onDismiss()
                                }
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(hook, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                            Spacer(Modifier.width(8.dp))
                            Text("+ Use", color = MotivationGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AiSubTabChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MotivationGold else ObsidianSurfaceVariant)
            .border(1.dp, if (selected) Color.White else ObsidianBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (selected) Color.Black else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
