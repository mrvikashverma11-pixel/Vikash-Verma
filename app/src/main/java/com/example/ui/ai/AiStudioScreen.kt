package com.example.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.GeminiAiService
import com.example.audio.AudioEngine
import com.example.data.TemplateRepository
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianBorderLight
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun AiStudioScreen(
    onOpenEditorWithTemplate: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var promptTopic by remember { mutableStateOf("Morning 5 AM discipline & relentless mindset") }
    var generatedHooks by remember { mutableStateOf<List<String>>(emptyList()) }
    var generatedVoiceScript by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Gemini AI Reels Studio", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text("Transform any topic into viral 3D motivational reels in seconds.", color = TextSecondary, fontSize = 12.sp)
        }

        // Hero Generator Box
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(ObsidianCard)
                    .border(1.dp, ObsidianBorderLight, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Enter Topic or Motivation Goal:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                OutlinedTextField(
                    value = promptTopic,
                    onValueChange = { promptTopic = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = ObsidianBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                // Quick Topic Chips
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Gym Beast", "Late Night Study", "Billionaire Wealth", "Never Give Up").forEach { chip ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ObsidianSurfaceVariant)
                                .border(1.dp, ObsidianBorder, RoundedCornerShape(6.dp))
                                .clickable { promptTopic = chip }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(chip, color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                // Generate Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(CyanPrimary)
                        .clickable(enabled = !isGenerating) {
                            isGenerating = true
                            coroutineScope.launch {
                                generatedHooks = GeminiAiService.generateMotivationalCaptions(promptTopic)
                                generatedVoiceScript = GeminiAiService.generateVoiceOverScript(promptTopic, "Alpha Leader")
                                isGenerating = false
                            }
                        }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("GENERATE AI REEL PACKAGE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 12.sp, letterSpacing = 0.5.sp)
                        }
                    }
                }
            }
        }

        // Generated Results
        if (generatedHooks.isNotEmpty()) {
            item {
                Text("Viral 3D Captions & Hooks", color = ElectricCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            items(generatedHooks) { hook ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(hook, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MotivationGold)
                            .clickable {
                                onOpenEditorWithTemplate(TemplateRepository.templates.first().id)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text("Create Reel", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (generatedVoiceScript.isNotBlank()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ElectricPink, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = ElectricPink, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("AI Voice-Over Script", color = ElectricPink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(generatedVoiceScript, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricPink)
                            .clickable {
                                onOpenEditorWithTemplate(TemplateRepository.templates.first().id)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Open Reel with this Voice-over", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
        }
    }
}
