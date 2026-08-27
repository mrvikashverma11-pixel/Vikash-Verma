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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.FilterType
import com.example.model.VideoClip
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ColorFilterPanel(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Filter Presets Carousel
        Column {
            Text("Cinematic Presets & Looks", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterType.values().forEach { filter ->
                    val isSelected = clip.filter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) MotivationGold else ObsidianCard)
                            .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(8.dp))
                            .clickable { onUpdateClip(clip.copy(filter = filter)) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = filter.displayName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Color Adjustments Sliders
        Text("Manual Color Grading", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        // Saturation
        ColorSliderItem(
            label = "Saturation",
            value = clip.saturation,
            valueRange = 0f..2.5f,
            displayValue = String.format("%.2f", clip.saturation),
            onValueChange = { onUpdateClip(clip.copy(saturation = it)) }
        )

        // Contrast
        ColorSliderItem(
            label = "Contrast",
            value = clip.contrast,
            valueRange = 0.5f..2.0f,
            displayValue = String.format("%.2f", clip.contrast),
            onValueChange = { onUpdateClip(clip.copy(contrast = it)) }
        )

        // Brightness
        ColorSliderItem(
            label = "Brightness",
            value = clip.brightness,
            valueRange = -50f..50f,
            displayValue = "${clip.brightness.toInt()}",
            onValueChange = { onUpdateClip(clip.copy(brightness = it)) }
        )

        // Temperature (Warm vs Cold)
        ColorSliderItem(
            label = "Temperature (Warm/Cold)",
            value = clip.temperature,
            valueRange = -50f..50f,
            displayValue = "${clip.temperature.toInt()}",
            onValueChange = { onUpdateClip(clip.copy(temperature = it)) }
        )

        // Sharpness
        ColorSliderItem(
            label = "Sharpness & Clarity",
            value = clip.sharpness,
            valueRange = 0f..100f,
            displayValue = "${clip.sharpness.toInt()}%",
            onValueChange = { onUpdateClip(clip.copy(sharpness = it)) }
        )
    }
}

@Composable
private fun ColorSliderItem(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    displayValue: String,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text(displayValue, color = ElectricCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MotivationGold,
                activeTrackColor = MotivationGold,
                inactiveTrackColor = ObsidianBorder
            )
        )
    }
}
