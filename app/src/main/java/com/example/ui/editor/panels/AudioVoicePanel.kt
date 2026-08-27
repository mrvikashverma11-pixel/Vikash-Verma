package com.example.ui.editor.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrack
import com.example.ui.theme.AudioTrackColor
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun AudioVoicePanel(
    audioTrack: AudioTrack?,
    onUpdateAudio: (AudioTrack) -> Unit,
    onAddAudio: (AudioTrack) -> Unit,
    onStartVoiceRecord: () -> Unit,
    onStopVoiceRecord: () -> Unit,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    var noiseReduction by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Voice Recording Hero Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianCard)
                .border(1.dp, if (isRecording) Color.Red else ObsidianBorder, RoundedCornerShape(12.dp))
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isRecording) "🔴 Recording Voice-over..." else "Live Voice-over Recording",
                color = if (isRecording) Color.Red else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (isRecording) "Speak clearly into the microphone" else "Record your own motivational voice or speech",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isRecording) Color.Red else MotivationGold)
                    .clickable {
                        if (isRecording) onStopVoiceRecord() else onStartVoiceRecord()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Mic",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        // Background Music Presets
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianCard)
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MusicNote, contentDescription = null, tint = AudioTrackColor, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Motivational Music Library", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))

            val musicList = listOf(
                Pair("Heavy Gym Trap Drive", "gym_trap"),
                Pair("Phonk Hustle Energy", "phonk_drive"),
                Pair("Deep Stoic Atmosphere", "deep_stoic"),
                Pair("Epic Orchestra Crescendo", "epic_orchestra"),
                Pair("Cinematic Piano Motivation", "cinematic_piano")
            )

            musicList.forEach { (title, key) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ObsidianSurfaceVariant)
                        .clickable {
                            val newTrack = AudioTrack(
                                id = UUID.randomUUID().toString(),
                                title = title,
                                sampleTrackKey = key,
                                durationMs = 15000L
                            )
                            onAddAudio(newTrack)
                        }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AudioTrackColor.copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("+ Add", color = AudioTrackColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active Track Volume & Fade In/Out Controls
        if (audioTrack != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianCard)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Selected Track: ${audioTrack.title}", color = MotivationGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                // Volume Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Volume", color = TextSecondary, fontSize = 12.sp)
                    Text("${(audioTrack.volume * 100).toInt()}%", color = MotivationGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = audioTrack.volume,
                    onValueChange = { onUpdateAudio(audioTrack.copy(volume = it)) },
                    valueRange = 0f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = MotivationGold, activeTrackColor = MotivationGold)
                )

                // Fade In / Out
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Fade In (${audioTrack.fadeInMs}ms)", color = TextSecondary, fontSize = 11.sp)
                    Text("Fade Out (${audioTrack.fadeOutMs}ms)", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // Noise Cleaner Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("AI Background Noise Cleaner", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Switch(
                checked = noiseReduction,
                onCheckedChange = { noiseReduction = it },
                colors = SwitchDefaults.colors(checkedThumbColor = ElectricCyan, checkedTrackColor = ObsidianBorder)
            )
        }
    }
}
