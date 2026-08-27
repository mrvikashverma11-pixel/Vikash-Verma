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
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.Rotate90DegreesCcw
import androidx.compose.material.icons.filled.ShapeLine
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MaskType
import com.example.model.TransitionType
import com.example.model.VideoClip
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
fun ClipToolsPanel(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit,
    onSplitClip: () -> Unit,
    onDeleteClip: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf("basic") } // "basic", "transform", "mask", "transitions"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ToolTabChip("Basic & Speed", selectedTab == "basic") { selectedTab = "basic" }
            ToolTabChip("Transform & PIP", selectedTab == "transform") { selectedTab = "transform" }
            ToolTabChip("Mask & Chroma", selectedTab == "mask") { selectedTab = "mask" }
            ToolTabChip("Transitions", selectedTab == "transitions") { selectedTab = "transitions" }
        }

        Spacer(Modifier.height(12.dp))

        when (selectedTab) {
            "basic" -> {
                BasicClipTools(
                    clip = clip,
                    onUpdateClip = onUpdateClip,
                    onSplitClip = onSplitClip,
                    onDeleteClip = onDeleteClip
                )
            }
            "transform" -> {
                TransformClipTools(clip = clip, onUpdateClip = onUpdateClip)
            }
            "mask" -> {
                MaskAndChromaTools(clip = clip, onUpdateClip = onUpdateClip)
            }
            "transitions" -> {
                TransitionsClipTools(clip = clip, onUpdateClip = onUpdateClip)
            }
        }
    }
}

@Composable
private fun BasicClipTools(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit,
    onSplitClip: () -> Unit,
    onDeleteClip: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Quick Action Buttons (Split, Reverse, Freeze, Delete)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionTile(
                icon = Icons.Default.CallSplit,
                label = "Split",
                color = MotivationGold,
                onClick = onSplitClip,
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                icon = Icons.Default.Transform,
                label = if (clip.isReversed) "Reversed" else "Reverse",
                color = if (clip.isReversed) ElectricPink else ElectricCyan,
                onClick = { onUpdateClip(clip.copy(isReversed = !clip.isReversed)) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                icon = Icons.Default.StopCircle,
                label = if (clip.isFrozen) "Frozen" else "Freeze",
                color = if (clip.isFrozen) ElectricPink else ElectricCyan,
                onClick = { onUpdateClip(clip.copy(isFrozen = !clip.isFrozen)) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                icon = Icons.Default.Delete,
                label = "Delete",
                color = Color.Red,
                onClick = onDeleteClip,
                modifier = Modifier.weight(1f)
            )
        }

        // Speed Slider (0.1x to 10x)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = MotivationGold, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Playback Speed", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Text(
                    text = "${String.format("%.1f", clip.speed)}x",
                    color = MotivationGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = clip.speed,
                onValueChange = { onUpdateClip(clip.copy(speed = (it * 10).toInt() / 10f)) },
                valueRange = 0.1f..5.0f,
                steps = 49,
                colors = SliderDefaults.colors(
                    thumbColor = MotivationGold,
                    activeTrackColor = MotivationGold,
                    inactiveTrackColor = ObsidianBorder
                )
            )

            // Speed Presets
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(0.5f, 1.0f, 1.5f, 2.0f, 3.0f).forEach { spd ->
                    Text(
                        text = "${spd}x",
                        color = if (clip.speed == spd) MotivationGold else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (clip.speed == spd) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (clip.speed == spd) ObsidianSurfaceVariant else Color.Transparent)
                            .clickable { onUpdateClip(clip.copy(speed = spd)) }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransformClipTools(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Rotate & Flip buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ActionTile(
                icon = Icons.AutoMirrored.Filled.RotateRight,
                label = "Rotate +90°",
                color = ElectricCyan,
                onClick = { onUpdateClip(clip.copy(rotateDeg = (clip.rotateDeg + 90f) % 360f)) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                icon = Icons.Default.Flip,
                label = "Flip H",
                color = if (clip.isFlippedH) MotivationGold else ElectricCyan,
                onClick = { onUpdateClip(clip.copy(isFlippedH = !clip.isFlippedH)) },
                modifier = Modifier.weight(1f)
            )
            ActionTile(
                icon = Icons.Default.Flip,
                label = "Flip V",
                color = if (clip.isFlippedV) MotivationGold else ElectricCyan,
                onClick = { onUpdateClip(clip.copy(isFlippedV = !clip.isFlippedV)) },
                modifier = Modifier.weight(1f)
            )
        }

        // Zoom / Crop Scale Slider
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
                Text("Zoom / Crop Scale", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("${String.format("%.2f", clip.zoomScale)}x", color = ElectricCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Slider(
                value = clip.zoomScale,
                onValueChange = { onUpdateClip(clip.copy(zoomScale = it)) },
                valueRange = 0.5f..2.5f,
                colors = SliderDefaults.colors(thumbColor = ElectricCyan, activeTrackColor = ElectricCyan)
            )
        }

        // Picture-in-Picture Toggle & Scale
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureInPicture, contentDescription = null, tint = MotivationGold, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Picture-in-Picture (PIP)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Switch(
                    checked = clip.isOverlayPip,
                    onCheckedChange = { onUpdateClip(clip.copy(isOverlayPip = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = MotivationGold, checkedTrackColor = ObsidianBorder)
                )
            }

            if (clip.isOverlayPip) {
                Spacer(Modifier.height(6.dp))
                Text("PIP Window Scale: ${String.format("%.2f", clip.pipScale)}", color = TextSecondary, fontSize = 11.sp)
                Slider(
                    value = clip.pipScale,
                    onValueChange = { onUpdateClip(clip.copy(pipScale = it)) },
                    valueRange = 0.2f..0.8f,
                    colors = SliderDefaults.colors(thumbColor = MotivationGold, activeTrackColor = MotivationGold)
                )
            }
        }
    }
}

@Composable
private fun MaskAndChromaTools(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Chroma Key / Green Screen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.InvertColors, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Green Screen / Chroma Key", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Switch(
                    checked = clip.chromaKeyEnabled,
                    onCheckedChange = { onUpdateClip(clip.copy(chromaKeyEnabled = it)) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Green, checkedTrackColor = ObsidianBorder)
                )
            }
        }

        // Masking Shapes
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(ObsidianCard)
                .padding(10.dp)
        ) {
            Text("Clip Masking Shape", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MaskType.values().forEach { mask ->
                    val isSelected = clip.maskType == mask
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) MotivationGold else ObsidianSurfaceVariant)
                            .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(6.dp))
                            .clickable { onUpdateClip(clip.copy(maskType = mask)) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = mask.displayName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TransitionsClipTools(
    clip: VideoClip,
    onUpdateClip: (VideoClip) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .padding(10.dp)
    ) {
        Text("Transition Effect (Entry)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TransitionType.values().forEach { trans ->
                val isSelected = clip.transitionIn == trans
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) ElectricCyan else ObsidianSurfaceVariant)
                        .border(1.dp, if (isSelected) Color.White else ObsidianBorder, RoundedCornerShape(6.dp))
                        .clickable { onUpdateClip(clip.copy(transitionIn = trans)) }
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = trans.displayName,
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

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.height(4.dp))
        Text(label, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
