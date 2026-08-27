package com.example.ui.editor

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AudioTrack
import com.example.model.EffectOverlay
import com.example.model.ProjectData
import com.example.model.TextOverlay
import com.example.model.VideoClip
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianBorderLight
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.PlayheadWhite
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VideoTrackBorder
import com.example.ui.theme.VideoTrackColor

@Composable
fun MultiTrackTimeline(
    project: ProjectData,
    currentPlayheadMs: Long,
    onSeek: (Long) -> Unit,
    selectedClipId: String?,
    selectedTextId: String?,
    selectedAudioId: String?,
    selectedEffectId: String?,
    onSelectClip: (String) -> Unit,
    onSelectText: (String) -> Unit,
    onSelectAudio: (String) -> Unit,
    onSelectEffect: (String) -> Unit,
    timelineZoom: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val totalDurationMs = project.totalDurationMs
    val pxPerMs = (60f * timelineZoom) / 1000f
    val totalTimelineWidthDp = (totalDurationMs * pxPerMs).dp.coerceAtLeast(340.dp)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ObsidianSurface)
            .border(1.dp, ObsidianBorder)
    ) {
        // Timecode and Ruler Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ObsidianSurfaceVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val curSec = currentPlayheadMs / 1000f
            val totSec = totalDurationMs / 1000f
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (currentPlayheadMs > 0) ElectricCyan else TextMuted)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = String.format("%02d:%05.2f / %02d:%05.2f", (curSec / 60).toInt(), curSec % 60, (totSec / 60).toInt(), totSec % 60),
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "${project.clips.size} clips • ${project.texts.size} texts • ${project.audios.size} audio",
                color = TextMuted,
                fontSize = 10.sp
            )
        }

        // Timeline Scrollable Content Area with Track Header Indicators
        Row(modifier = Modifier.fillMaxWidth().height(168.dp)) {
            // Fixed Left Track Labels (Professional Polish Design)
            Column(
                modifier = Modifier
                    .width(42.dp)
                    .fillMaxHeight()
                    .background(ObsidianSurfaceVariant)
                    .padding(top = 24.dp, bottom = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TrackLabelBadge("TEXT", 28.dp, ElectricCyan)
                TrackLabelBadge("OVERLAY", 24.dp, TextSecondary)
                TrackLabelBadge("VIDEO", 36.dp, TextPrimary)
                TrackLabelBadge("AUDIO", 28.dp, EmeraldGreen)
            }

            // Scrollable Timeline Tracks
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .horizontalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .width(totalTimelineWidthDp + 200.dp)
                        .fillMaxHeight()
                        .pointerInput(totalDurationMs, pxPerMs) {
                            detectTapGestures { offset ->
                                val clickedMs = (offset.x / pxPerMs).toLong().coerceIn(0L, totalDurationMs)
                                onSeek(clickedMs)
                            }
                        }
                ) {
                    // Time Ruler Markings
                    TimelineRuler(totalDurationMs = totalDurationMs, pxPerMs = pxPerMs)

                    // Track Layers Stack
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 24.dp, bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Track 1: 3D Text Layer
                        TextTrackRow(
                            texts = project.texts,
                            selectedTextId = selectedTextId,
                            onSelectText = onSelectText,
                            pxPerMs = pxPerMs
                        )

                        // Track 2: Effects & Overlay Layer
                        EffectTrackRow(
                            effects = project.effects,
                            selectedEffectId = selectedEffectId,
                            onSelectEffect = onSelectEffect,
                            pxPerMs = pxPerMs
                        )

                        // Track 3: Video / Media Layer
                        VideoTrackRow(
                            clips = project.clips,
                            selectedClipId = selectedClipId,
                            onSelectClip = onSelectClip,
                            pxPerMs = pxPerMs
                        )

                        // Track 4: Audio & Waveform Layer
                        AudioTrackRow(
                            audios = project.audios,
                            selectedAudioId = selectedAudioId,
                            onSelectAudio = onSelectAudio,
                            pxPerMs = pxPerMs
                        )
                    }

                    // Playhead Scrubber Needle with White Glow from Professional Polish Design
                    val playheadOffsetDp = (currentPlayheadMs * pxPerMs).dp
                    Box(
                        modifier = Modifier
                            .offset(x = playheadOffsetDp)
                            .fillMaxHeight()
                            .width(2.dp)
                            .background(PlayheadWhite)
                    ) {
                        // Needle head top indicator
                        Box(
                            modifier = Modifier
                                .size(width = 10.dp, height = 8.dp)
                                .offset(x = (-4).dp, y = 0.dp)
                                .clip(RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                                .background(PlayheadWhite)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackLabelBadge(label: String, height: androidx.compose.ui.unit.Dp, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(end = 4.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Text(
            text = label,
            color = color.copy(alpha = 0.7f),
            fontSize = 7.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun TimelineRuler(totalDurationMs: Long, pxPerMs: Float) {
    Canvas(modifier = Modifier.fillMaxWidth().height(22.dp).background(ObsidianSurfaceVariant)) {
        val stepMs = 1000L // 1 sec tick
        var t = 0L
        while (t <= totalDurationMs) {
            val x = t * pxPerMs
            val isMajor = (t % 5000L) == 0L
            drawLine(
                color = if (isMajor) ElectricCyan.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.2f),
                start = Offset(x, if (isMajor) 4f else 12f),
                end = Offset(x, size.height),
                strokeWidth = if (isMajor) 2f else 1f
            )
            t += stepMs
        }
    }
}

@Composable
private fun TextTrackRow(
    texts: List<TextOverlay>,
    selectedTextId: String?,
    onSelectText: (String) -> Unit,
    pxPerMs: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(ObsidianCard.copy(alpha = 0.3f))
    ) {
        for (text in texts) {
            val leftDp = (text.startTimeMs * pxPerMs).dp
            val widthDp = ((text.endTimeMs - text.startTimeMs) * pxPerMs).dp.coerceAtLeast(36.dp)
            val isSelected = text.id == selectedTextId

            Box(
                modifier = Modifier
                    .offset(x = leftDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(CyanDark.copy(alpha = 0.8f))
                    .border(
                        1.dp,
                        if (isSelected) ElectricCyan else CyanBorder,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectText(text.id) },
                contentAlignment = Alignment.CenterStart
            ) {
                // Accent indicator bar on left
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(4.dp)
                        .background(CyanPrimary)
                )

                Row(
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Title,
                        contentDescription = null,
                        tint = ElectricCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (text.is3D) "3D: ${text.text}" else text.text,
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun EffectTrackRow(
    effects: List<EffectOverlay>,
    selectedEffectId: String?,
    onSelectEffect: (String) -> Unit,
    pxPerMs: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(ObsidianCard.copy(alpha = 0.3f))
    ) {
        for (effect in effects) {
            val leftDp = (effect.startTimeMs * pxPerMs).dp
            val widthDp = ((effect.endTimeMs - effect.startTimeMs) * pxPerMs).dp.coerceAtLeast(36.dp)
            val isSelected = effect.id == selectedEffectId

            Box(
                modifier = Modifier
                    .offset(x = leftDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(ObsidianCard)
                    .border(
                        1.dp,
                        if (isSelected) ElectricPink else ObsidianBorderLight,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectEffect(effect.id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = ElectricPink,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = effect.effectType.displayName,
                        color = TextSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoTrackRow(
    clips: List<VideoClip>,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit,
    pxPerMs: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(ObsidianCard.copy(alpha = 0.4f))
    ) {
        for (clip in clips) {
            val leftDp = (clip.startTimeMs * pxPerMs).dp
            val widthDp = ((clip.endTimeMs - clip.startTimeMs) * pxPerMs).dp.coerceAtLeast(36.dp)
            val isSelected = clip.id == selectedClipId

            Box(
                modifier = Modifier
                    .offset(x = leftDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(VideoTrackColor)
                    .border(
                        if (isSelected) 2.dp else 1.dp,
                        if (isSelected) PlayheadWhite else VideoTrackBorder,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectClip(clip.id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${clip.name} (${clip.speed}x)",
                        color = TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioTrackRow(
    audios: List<AudioTrack>,
    selectedAudioId: String?,
    onSelectAudio: (String) -> Unit,
    pxPerMs: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(ObsidianCard.copy(alpha = 0.3f))
    ) {
        for (audio in audios) {
            val leftDp = (audio.startTimeMs * pxPerMs).dp
            val widthDp = (audio.durationMs * pxPerMs).dp.coerceAtLeast(40.dp)
            val isSelected = audio.id == selectedAudioId

            Box(
                modifier = Modifier
                    .offset(x = leftDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(EmeraldDark.copy(alpha = 0.7f))
                    .border(
                        1.dp,
                        if (isSelected) EmeraldGreen else Color(0xFF047857),
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onSelectAudio(audio.id) }
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = audio.title,
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Emerald Waveform mini bars matching Professional Polish specs
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (pt in audio.waveformPoints.take(8)) {
                            Box(
                                modifier = Modifier
                                    .width(1.5.dp)
                                    .height((pt * 14).dp.coerceAtLeast(3.dp))
                                    .background(EmeraldGreen)
                            )
                        }
                    }
                }
            }
        }
    }
}
