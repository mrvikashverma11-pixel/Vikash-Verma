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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.data.TemplateRepository
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

@Composable
fun Text3DToolsPanel(
    textOverlay: TextOverlay,
    onUpdateText: (TextOverlay) -> Unit,
    onDeleteText: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("3d") } // "3d", "presets", "font_style", "animation"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Text Input & Delete Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textOverlay.text,
                onValueChange = { onUpdateText(textOverlay.copy(text = it)) },
                label = { Text("Motivational Title / Text", color = TextSecondary) },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MotivationGold,
                    unfocusedBorderColor = ObsidianBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                maxLines = 2
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Red.copy(alpha = 0.2f))
                    .border(1.dp, Color.Red, RoundedCornerShape(8.dp))
                    .clickable(onClick = onDeleteText),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }

        // Sub-tabs: 3D Depth, 3D Presets, Fonts & Colors, Animations
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToolTabChip("3D Extrusion & Style", selectedTab == "3d") { selectedTab = "3d" }
            ToolTabChip("Motivational Presets", selectedTab == "presets") { selectedTab = "presets" }
            ToolTabChip("Fonts & Styling", selectedTab == "font_style") { selectedTab = "font_style" }
            ToolTabChip("Animation", selectedTab == "animation") { selectedTab = "animation" }
        }

        Spacer(Modifier.height(4.dp))

        when (selectedTab) {
            "3d" -> {
                Panel3DControls(textOverlay = textOverlay, onUpdateText = onUpdateText)
            }
            "presets" -> {
                Panel3DPresets(textOverlay = textOverlay, onUpdateText = onUpdateText)
            }
            "font_style" -> {
                PanelFontsAndStyle(textOverlay = textOverlay, onUpdateText = onUpdateText)
            }
            "animation" -> {
                PanelTextAnimation(textOverlay = textOverlay, onUpdateText = onUpdateText)
            }
        }
    }
}

@Composable
private fun Panel3DControls(
    textOverlay: TextOverlay,
    onUpdateText: (TextOverlay) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Enable 3D Toggle
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
                Icon(Icons.Default.ViewInAr, contentDescription = null, tint = MotivationGold, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Enable 3D Depth & Lighting", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Switch(
                checked = textOverlay.is3D,
                onCheckedChange = { onUpdateText(textOverlay.copy(is3D = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = MotivationGold, checkedTrackColor = ObsidianBorder)
            )
        }

        if (textOverlay.is3D) {
            // 3D Material / Shader selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianCard)
                    .padding(10.dp)
            ) {
                Text("3D Material / Style", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Style3DType.values().forEach { style ->
                        val isSelected = textOverlay.style3D == style
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) MotivationGold else ObsidianSurfaceVariant)
                                .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(8.dp))
                                .clickable { onUpdateText(textOverlay.copy(style3D = style)) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = style.displayName,
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Extrusion Depth Slider
            SliderControlBox(
                label = "3D Extrusion Depth",
                value = textOverlay.extrusion,
                valueRange = 0f..25f,
                displayValue = "${textOverlay.extrusion.toInt()}px",
                onValueChange = { onUpdateText(textOverlay.copy(extrusion = it)) }
            )

            // Rotation X (Pitch)
            SliderControlBox(
                label = "3D Rotation X (Pitch)",
                value = textOverlay.rotationX,
                valueRange = -35f..35f,
                displayValue = "${textOverlay.rotationX.toInt()}°",
                onValueChange = { onUpdateText(textOverlay.copy(rotationX = it)) }
            )

            // Rotation Y (Yaw)
            SliderControlBox(
                label = "3D Rotation Y (Yaw)",
                value = textOverlay.rotationY,
                valueRange = -35f..35f,
                displayValue = "${textOverlay.rotationY.toInt()}°",
                onValueChange = { onUpdateText(textOverlay.copy(rotationY = it)) }
            )

            // Light Angle Deg
            SliderControlBox(
                label = "Light Angle",
                value = textOverlay.lightAngleDeg,
                valueRange = 0f..360f,
                displayValue = "${textOverlay.lightAngleDeg.toInt()}°",
                onValueChange = { onUpdateText(textOverlay.copy(lightAngleDeg = it)) }
            )
        }
    }
}

@Composable
private fun Panel3DPresets(
    textOverlay: TextOverlay,
    onUpdateText: (TextOverlay) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ready-to-use 3D Motivational Titles", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text("Tap any preset to apply typography, 3D style & animation:", color = TextSecondary, fontSize = 11.sp)

        TemplateRepository.preset3DTitles.forEach { preset ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(ObsidianSurfaceVariant)
                    .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
                    .clickable {
                        onUpdateText(
                            textOverlay.copy(
                                text = preset.englishText,
                                style3D = preset.style3D,
                                animation = preset.animation,
                                is3D = true,
                                extrusion = 14f,
                                rotationX = 12f,
                                rotationY = -12f
                            )
                        )
                    }
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(preset.englishText, color = MotivationGold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(preset.hindiText, color = TextPrimary, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MotivationGold.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(preset.style3D.displayName, color = MotivationGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PanelFontsAndStyle(
    textOverlay: TextOverlay,
    onUpdateText: (TextOverlay) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Font Family Selector
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp)
        ) {
            Text("Font Family (Hindi & English)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            val fonts = listOf("Cinematic Impact", "Hindi Devanagari", "Modern Bold", "Serif Luxury", "Cyberpunk Heavy")
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fonts.forEach { f ->
                    val isSelected = textOverlay.fontFamily == f
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) ElectricCyan else ObsidianSurfaceVariant)
                            .clickable { onUpdateText(textOverlay.copy(fontFamily = f)) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = f,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Font Size Slider
        SliderControlBox(
            label = "Font Size",
            value = textOverlay.fontSizeSp,
            valueRange = 16f..48f,
            displayValue = "${textOverlay.fontSizeSp.toInt()}sp",
            onValueChange = { onUpdateText(textOverlay.copy(fontSizeSp = it)) }
        )

        // Letter Spacing
        SliderControlBox(
            label = "Letter Spacing",
            value = textOverlay.letterSpacing,
            valueRange = 0f..10f,
            displayValue = "${textOverlay.letterSpacing.toInt()}dp",
            onValueChange = { onUpdateText(textOverlay.copy(letterSpacing = it)) }
        )

        // Bold / Italic Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (textOverlay.isBold) MotivationGold else ObsidianCard)
                    .clickable { onUpdateText(textOverlay.copy(isBold = !textOverlay.isBold)) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("BOLD", color = if (textOverlay.isBold) Color.Black else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (textOverlay.isItalic) MotivationGold else ObsidianCard)
                    .clickable { onUpdateText(textOverlay.copy(isItalic = !textOverlay.isItalic)) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("ITALIC", color = if (textOverlay.isItalic) Color.Black else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun PanelTextAnimation(
    textOverlay: TextOverlay,
    onUpdateText: (TextOverlay) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(10.dp)
    ) {
        Text("Text Entry & Motion Animation", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextAnimationType.values().forEach { anim ->
                val isSelected = textOverlay.animation == anim
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) ElectricPink else ObsidianSurfaceVariant)
                        .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(6.dp))
                        .clickable { onUpdateText(textOverlay.copy(animation = anim)) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = anim.displayName,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun SliderControlBox(
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
            Text(displayValue, color = MotivationGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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

@Composable
private fun ToolTabChip(title: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) MotivationGold else ObsidianCard)
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
