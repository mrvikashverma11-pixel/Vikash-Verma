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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EffectOverlay
import com.example.model.MotionEffectType
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@Composable
fun EffectsMotionPanel(
    activeEffect: EffectOverlay?,
    onUpdateEffect: (EffectOverlay) -> Unit,
    onAddEffect: (EffectOverlay) -> Unit,
    onDeleteEffect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Effects Grid / Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianCard)
                .padding(12.dp)
        ) {
            Text("Cinematic Motion & Glitch Effects", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MotionEffectType.values().filter { it != MotionEffectType.NONE }.forEach { effectType ->
                    val isSelected = activeEffect?.effectType == effectType
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) ElectricPink else ObsidianSurfaceVariant)
                            .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                if (activeEffect != null) {
                                    onUpdateEffect(activeEffect.copy(effectType = effectType))
                                } else {
                                    val newEff = EffectOverlay(
                                        id = UUID.randomUUID().toString(),
                                        effectType = effectType,
                                        startTimeMs = 0L,
                                        endTimeMs = 5000L,
                                        intensity = 0.8f
                                    )
                                    onAddEffect(newEff)
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = effectType.displayName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Intensity slider if active effect selected
        if (activeEffect != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ObsidianCard)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Effect: ${activeEffect.effectType.displayName}", color = ElectricPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Red.copy(alpha = 0.2f))
                            .clickable(onClick = onDeleteEffect)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Remove Effect", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Intensity", color = TextSecondary, fontSize = 12.sp)
                    Text("${(activeEffect.intensity * 100).toInt()}%", color = ElectricPink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = activeEffect.intensity,
                    onValueChange = { onUpdateEffect(activeEffect.copy(intensity = it)) },
                    valueRange = 0.1f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = ElectricPink, activeTrackColor = ElectricPink)
                )
            }
        }
    }
}
