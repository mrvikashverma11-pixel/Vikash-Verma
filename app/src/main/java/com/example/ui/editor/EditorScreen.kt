package com.example.ui.editor

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AspectRatioType
import com.example.ui.editor.panels.AiToolsSheet
import com.example.ui.editor.panels.AudioVoicePanel
import com.example.ui.editor.panels.ClipToolsPanel
import com.example.ui.editor.panels.ColorFilterPanel
import com.example.ui.editor.panels.EffectsMotionPanel
import com.example.ui.editor.panels.Text3DToolsPanel
import com.example.ui.theme.AudioTrackColor
import com.example.ui.theme.CyanBorder
import com.example.ui.theme.CyanDark
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.ElectricPink
import com.example.ui.theme.MotivationGold
import com.example.ui.theme.ObsidianBackground
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianBorderLight
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.ObsidianSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTrackColor
import com.example.ui.theme.VideoTrackColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectId: String?,
    templateId: String?,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = viewModel()
) {
    val context = LocalContext.current
    val project by viewModel.projectState.collectAsState()
    val currentPlayheadMs by viewModel.currentPlayheadMs.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val selectedClipId by viewModel.selectedClipId.collectAsState()
    val selectedTextId by viewModel.selectedTextId.collectAsState()
    val selectedAudioId by viewModel.selectedAudioId.collectAsState()
    val selectedEffectId by viewModel.selectedEffectId.collectAsState()
    val activeToolPanel by viewModel.activeToolPanel.collectAsState()
    val isRecordingVoice by viewModel.isRecordingVoice.collectAsState()

    var showSafeGuides by remember { mutableStateOf(false) }
    var showAiSheet by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAspectMenu by remember { mutableStateOf(false) }

    // Media Gallery Picker Launcher
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.addMediaClip(uri.toString(), "Gallery Media")
        }
    }

    LaunchedEffect(projectId, templateId) {
        viewModel.loadOrCreateProject(projectId, templateId)
    }

    if (project == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(ObsidianBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MotivationGold)
        }
        return
    }

    val currentProject = project!!

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Cyan "V" Logo Box from Professional Polish Design
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = Color.Black,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column {
                            Text(
                                text = currentProject.title,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "3D Extrusion Active",
                                color = ElectricCyan.copy(alpha = 0.8f),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Aspect Ratio Chip (Tap to cycle 9:16, 16:9, 1:1, 4:5)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ObsidianCard)
                                .border(1.dp, ObsidianBorderLight, RoundedCornerShape(6.dp))
                                .clickable {
                                    val ratios = AspectRatioType.values()
                                    val nextIndex = (currentProject.aspectRatio.ordinal + 1) % ratios.size
                                    viewModel.setAspectRatio(ratios[nextIndex])
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentProject.aspectRatio.displayName,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Undo & Redo
                    IconButton(onClick = { viewModel.undo() }) {
                        Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = { viewModel.redo() }) {
                        Icon(Icons.AutoMirrored.Filled.Redo, contentDescription = "Redo", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }

                    // AI Assistant Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorderLight, RoundedCornerShape(20.dp))
                            .clickable { showAiSheet = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(13.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("AI Tools", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    // Professional Polish Cyan Pill EXPORT Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(CyanPrimary)
                            .clickable { showExportDialog = true }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text("EXPORT", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black, letterSpacing = 0.5.sp)
                    }

                    Spacer(Modifier.width(6.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ObsidianSurface)
            )
        },
        containerColor = ObsidianBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. VIDEO PREVIEW CANVAS (Constrained framing with safe guide & transport bar)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.15f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                VideoPreviewCanvas(
                    project = currentProject,
                    currentPlayheadMs = currentPlayheadMs,
                    isPlaying = isPlaying,
                    showSafeGuides = showSafeGuides,
                    selectedTextId = selectedTextId,
                    onSelectText = { viewModel.selectText(it) },
                    modifier = Modifier.fillMaxSize()
                )

                // Safe guides toggle button floating on top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (showSafeGuides) ElectricCyan else ObsidianCard.copy(alpha = 0.85f))
                        .border(1.dp, if (showSafeGuides) ElectricCyan else ObsidianBorderLight, RoundedCornerShape(6.dp))
                        .clickable { showSafeGuides = !showSafeGuides }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (showSafeGuides) "Guides ON" else "Guides OFF",
                        color = if (showSafeGuides) Color.Black else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Play / Pause Floating Overlay Pill with Pro Polish styling
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(ObsidianSurface.copy(alpha = 0.9f))
                        .border(1.dp, ObsidianBorderLight, RoundedCornerShape(20.dp))
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.togglePlayPause() },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = ElectricCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Red pulsing dot indicator
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) Color(0xFFEF4444) else TextMuted)
                        )

                        val curSec = currentPlayheadMs / 1000f
                        val totSec = currentProject.totalDurationMs / 1000f
                        Text(
                            text = String.format("%02d:%05.2f / %02d:%05.2f", (curSec / 60).toInt(), curSec % 60, (totSec / 60).toInt(), totSec % 60),
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // 2. MULTI-TRACK TIMELINE
            MultiTrackTimeline(
                project = currentProject,
                currentPlayheadMs = currentPlayheadMs,
                onSeek = { viewModel.seekTo(it) },
                selectedClipId = selectedClipId,
                selectedTextId = selectedTextId,
                selectedAudioId = selectedAudioId,
                selectedEffectId = selectedEffectId,
                onSelectClip = { viewModel.selectClip(it) },
                onSelectText = { viewModel.selectText(it) },
                onSelectAudio = { viewModel.selectAudio(it) },
                onSelectEffect = { viewModel.selectEffect(it) },
                modifier = Modifier.fillMaxWidth()
            )

            // Handle bar divider indicator matching Professional Polish design
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(ObsidianBorderLight)
                )
            }

            // 3. TOOLBAR CATEGORY SELECTOR (Professional Polish Square-Rounded Tool Cards)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ObsidianSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Media Button
                CategoryNavButton(
                    icon = Icons.Default.AddPhotoAlternate,
                    label = "Import",
                    isSelected = false,
                    accentColor = ElectricCyan,
                    onClick = { mediaPickerLauncher.launch("image/*,video/*") }
                )

                // Clips Panel
                CategoryNavButton(
                    icon = Icons.Default.Movie,
                    label = "Edit",
                    isSelected = activeToolPanel == "clips",
                    accentColor = ElectricCyan,
                    onClick = { viewModel.setActiveToolPanel("clips") }
                )

                // 3D Text
                CategoryNavButton(
                    icon = Icons.Default.TextFields,
                    label = "3D Text",
                    isSelected = activeToolPanel == "text",
                    accentColor = ElectricCyan,
                    onClick = { viewModel.setActiveToolPanel("text") }
                )

                // Color Filters
                CategoryNavButton(
                    icon = Icons.Default.Tune,
                    label = "Filters",
                    isSelected = activeToolPanel == "filter",
                    accentColor = ElectricCyan,
                    onClick = { viewModel.setActiveToolPanel("filter") }
                )

                // Audio & Voice
                CategoryNavButton(
                    icon = Icons.Default.Audiotrack,
                    label = "Audio",
                    isSelected = activeToolPanel == "audio",
                    accentColor = ElectricCyan,
                    onClick = { viewModel.setActiveToolPanel("audio") }
                )

                // Motion Effects
                CategoryNavButton(
                    icon = Icons.Default.AutoAwesome,
                    label = "FX",
                    isSelected = activeToolPanel == "effects",
                    accentColor = ElectricCyan,
                    onClick = { viewModel.setActiveToolPanel("effects") }
                )
            }

            // 4. ACTIVE TOOL EDITING PANEL DRAWER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.9f)
                    .background(ObsidianBackground)
            ) {
                when (activeToolPanel) {
                    "clips" -> {
                        val activeClip = currentProject.clips.firstOrNull { it.id == selectedClipId }
                            ?: currentProject.clips.firstOrNull()
                        if (activeClip != null) {
                            ClipToolsPanel(
                                clip = activeClip,
                                onUpdateClip = { viewModel.updateClip(it) },
                                onSplitClip = { viewModel.splitActiveClip() },
                                onDeleteClip = { viewModel.deleteActiveClip() }
                            )
                        } else {
                            EmptyPanelPlaceholder("No clip selected. Tap + Media to import.")
                        }
                    }

                    "filter" -> {
                        val activeClip = currentProject.clips.firstOrNull { it.id == selectedClipId }
                            ?: currentProject.clips.firstOrNull()
                        if (activeClip != null) {
                            ColorFilterPanel(
                                clip = activeClip,
                                onUpdateClip = { viewModel.updateClip(it) }
                            )
                        } else {
                            EmptyPanelPlaceholder("No clip selected for color grading.")
                        }
                    }

                    "text" -> {
                        val activeText = currentProject.texts.firstOrNull { it.id == selectedTextId }
                        if (activeText != null) {
                            Text3DToolsPanel(
                                textOverlay = activeText,
                                onUpdateText = { viewModel.updateText(it) },
                                onDeleteText = { viewModel.deleteActiveText() }
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("No text overlay selected", color = TextSecondary, fontSize = 13.sp)
                                Spacer(Modifier.height(10.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MotivationGold)
                                        .clickable { viewModel.addNewText("CONQUER YOUR LIMITS", is3D = true) }
                                        .padding(horizontal = 16.dp, vertical = 10.dp)
                                ) {
                                    Text("+ Add 3D Motivational Text", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    "audio" -> {
                        val activeAudio = currentProject.audios.firstOrNull { it.id == selectedAudioId }
                            ?: currentProject.audios.firstOrNull()
                        AudioVoicePanel(
                            audioTrack = activeAudio,
                            onUpdateAudio = { viewModel.updateAudio(it) },
                            onAddAudio = { viewModel.addAudioTrack(it) },
                            onStartVoiceRecord = { viewModel.startVoiceRecording() },
                            onStopVoiceRecord = { viewModel.stopVoiceRecording() },
                            isRecording = isRecordingVoice
                        )
                    }

                    "effects" -> {
                        val activeEffect = currentProject.effects.firstOrNull { it.id == selectedEffectId }
                            ?: currentProject.effects.firstOrNull()
                        EffectsMotionPanel(
                            activeEffect = activeEffect,
                            onUpdateEffect = { viewModel.updateEffect(it) },
                            onAddEffect = { viewModel.addEffect(it) },
                            onDeleteEffect = { viewModel.deleteActiveEffect() }
                        )
                    }
                }
            }
        }
    }

    // AI Creator Suite Bottom Sheet
    if (showAiSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAiSheet = false },
            containerColor = ObsidianCard
        ) {
            AiToolsSheet(
                videoDurationMs = currentProject.totalDurationMs,
                onAddAutoCaptions = { captions ->
                    viewModel.addCaptionsFromAi(captions)
                },
                onAddVoiceOver = { track, script ->
                    viewModel.addAudioTrack(track)
                },
                onPlayTts = { script, isHindi ->
                    viewModel.audioEngine.speakText(script, isHindi)
                },
                onDismiss = { showAiSheet = false }
            )
        }
    }

    // Export Resolution & Master Render Dialog
    if (showExportDialog) {
        ExportDialog(
            project = currentProject,
            isPremiumUser = true,
            onDismiss = { showExportDialog = false },
            onShare = {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Check out my motivation reel edited with VV Motivation Editor! 🔥 #Motivation #Grind #Reels")
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, "Share Motivation Reel")
                context.startActivity(shareIntent)
            }
        )
    }
}

@Composable
private fun CategoryNavButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) CyanDark else ObsidianCard)
                .border(
                    1.dp,
                    if (isSelected) CyanBorder else ObsidianBorderLight,
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) ElectricCyan else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            color = if (isSelected) ElectricCyan else TextMuted,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            letterSpacing = 0.3.sp
        )
    }
}

@Composable
private fun EmptyPanelPlaceholder(message: String) {
    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, color = TextMuted, fontSize = 12.sp)
    }
}
