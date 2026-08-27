package com.example.ui.editor

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.TemplateRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ProjectEntity
import com.example.data.local.ProjectJsonUtils
import com.example.model.AspectRatioType
import com.example.model.AudioTrack
import com.example.model.EffectOverlay
import com.example.model.FilterType
import com.example.model.MotionEffectType
import com.example.model.ProjectData
import com.example.model.Style3DType
import com.example.model.TextAnimationType
import com.example.model.TextOverlay
import com.example.model.TransitionType
import com.example.model.VideoClip
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class EditorViewModel(application: Application) : AndroidViewModel(application) {

    private val projectDao = AppDatabase.getDatabase(application).projectDao()
    val audioEngine = AudioEngine(application)

    private val _projectState = MutableStateFlow<ProjectData?>(null)
    val projectState: StateFlow<ProjectData?> = _projectState.asStateFlow()

    private val _currentPlayheadMs = MutableStateFlow(0L)
    val currentPlayheadMs: StateFlow<Long> = _currentPlayheadMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _selectedClipId = MutableStateFlow<String?>(null)
    val selectedClipId: StateFlow<String?> = _selectedClipId.asStateFlow()

    private val _selectedTextId = MutableStateFlow<String?>(null)
    val selectedTextId: StateFlow<String?> = _selectedTextId.asStateFlow()

    private val _selectedAudioId = MutableStateFlow<String?>(null)
    val selectedAudioId: StateFlow<String?> = _selectedAudioId.asStateFlow()

    private val _selectedEffectId = MutableStateFlow<String?>(null)
    val selectedEffectId: StateFlow<String?> = _selectedEffectId.asStateFlow()

    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _activeToolPanel = MutableStateFlow("clips") // "clips", "filter", "text", "audio", "effects", "ai", "export"
    val activeToolPanel: StateFlow<String> = _activeToolPanel.asStateFlow()

    private val undoStack = mutableListOf<ProjectData>()
    private val redoStack = mutableListOf<ProjectData>()

    private var playbackJob: Job? = null

    fun loadOrCreateProject(projectId: String?, templateId: String? = null) {
        viewModelScope.launch {
            if (!projectId.isNullOrBlank()) {
                val entity = projectDao.getProjectById(projectId)
                if (entity != null) {
                    val project = ProjectData(
                        id = entity.id,
                        title = entity.title,
                        aspectRatio = runCatching { AspectRatioType.valueOf(entity.aspectRatioName) }.getOrDefault(AspectRatioType.REEL_9_16),
                        clips = ProjectJsonUtils.jsonToClips(entity.clipsJson),
                        texts = ProjectJsonUtils.jsonToTexts(entity.textsJson),
                        audios = ProjectJsonUtils.jsonToAudios(entity.audiosJson),
                        effects = ProjectJsonUtils.jsonToEffects(entity.effectsJson),
                        isAutosaved = true,
                        updatedAt = entity.updatedAt
                    )
                    _projectState.value = project
                    _selectedClipId.value = project.clips.firstOrNull()?.id
                    _selectedTextId.value = project.texts.firstOrNull()?.id
                    return@launch
                }
            }

            // Create from template or default
            val project = if (!templateId.isNullOrBlank()) {
                val tmpl = TemplateRepository.templates.firstOrNull { it.id == templateId }
                    ?: TemplateRepository.templates.first()
                TemplateRepository.createProjectFromTemplate(tmpl)
            } else {
                TemplateRepository.createBlankProject("My Motivation Reel", AspectRatioType.REEL_9_16)
            }

            _projectState.value = project
            _selectedClipId.value = project.clips.firstOrNull()?.id
            _selectedTextId.value = project.texts.firstOrNull()?.id
            saveProjectToDb(project)
        }
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        val proj = _projectState.value ?: return
        _isPlaying.value = true

        // Play synthetic background rhythm if enabled
        val firstAudio = proj.audios.firstOrNull()
        if (firstAudio != null) {
            audioEngine.playSyntheticBeat(firstAudio.sampleTrackKey)
        }

        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val total = proj.totalDurationMs
            val intervalMs = 40L
            while (_isPlaying.value) {
                delay(intervalMs)
                val next = _currentPlayheadMs.value + intervalMs
                if (next >= total) {
                    _currentPlayheadMs.value = 0L // Loop
                } else {
                    _currentPlayheadMs.value = next
                }
            }
        }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        audioEngine.stopSyntheticBeat()
    }

    fun seekTo(ms: Long) {
        val total = _projectState.value?.totalDurationMs ?: 10000L
        _currentPlayheadMs.value = ms.coerceIn(0L, total)
    }

    fun setActiveToolPanel(panel: String) {
        _activeToolPanel.value = panel
    }

    fun selectClip(id: String) {
        _selectedClipId.value = id
        _activeToolPanel.value = "clips"
    }

    fun selectText(id: String) {
        _selectedTextId.value = id
        _activeToolPanel.value = "text"
    }

    fun selectAudio(id: String) {
        _selectedAudioId.value = id
        _activeToolPanel.value = "audio"
    }

    fun selectEffect(id: String) {
        _selectedEffectId.value = id
        _activeToolPanel.value = "effects"
    }

    private fun pushUndo(currentState: ProjectData) {
        if (undoStack.size > 20) undoStack.removeAt(0)
        undoStack.add(currentState)
        redoStack.clear()
    }

    fun undo() {
        val current = _projectState.value ?: return
        if (undoStack.isNotEmpty()) {
            redoStack.add(current)
            val prev = undoStack.removeAt(undoStack.lastIndex)
            _projectState.value = prev
            saveProjectToDb(prev)
        }
    }

    fun redo() {
        val current = _projectState.value ?: return
        if (redoStack.isNotEmpty()) {
            undoStack.add(current)
            val next = redoStack.removeAt(redoStack.lastIndex)
            _projectState.value = next
            saveProjectToDb(next)
        }
    }

    // Clip Operations
    fun updateClip(updatedClip: VideoClip) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newClips = current.clips.map { if (it.id == updatedClip.id) updatedClip else it }
        val newProject = current.copy(clips = newClips, updatedAt = System.currentTimeMillis())
        _projectState.value = newProject
        saveProjectToDb(newProject)
    }

    fun splitActiveClip() {
        val current = _projectState.value ?: return
        val clipId = _selectedClipId.value ?: return
        val clipIndex = current.clips.indexOfFirst { it.id == clipId }
        if (clipIndex == -1) return

        val clip = current.clips[clipIndex]
        val playhead = _currentPlayheadMs.value
        if (playhead <= clip.startTimeMs || playhead >= clip.endTimeMs) return

        pushUndo(current)
        val clip1 = clip.copy(endTimeMs = playhead)
        val clip2 = clip.copy(
            id = UUID.randomUUID().toString(),
            name = "${clip.name} (Part 2)",
            startTimeMs = playhead
        )

        val newClips = current.clips.toMutableList().apply {
            set(clipIndex, clip1)
            add(clipIndex + 1, clip2)
        }
        val newProj = current.copy(clips = newClips, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedClipId.value = clip2.id
        saveProjectToDb(newProj)
    }

    fun deleteActiveClip() {
        val current = _projectState.value ?: return
        val clipId = _selectedClipId.value ?: return
        if (current.clips.size <= 1) return // Keep at least one clip

        pushUndo(current)
        val newClips = current.clips.filter { it.id != clipId }
        val newProj = current.copy(clips = newClips, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedClipId.value = newClips.firstOrNull()?.id
        saveProjectToDb(newProj)
    }

    fun addMediaClip(uri: String?, name: String = "Media Clip") {
        val current = _projectState.value ?: return
        pushUndo(current)
        val lastEnd = current.clips.maxOfOrNull { it.endTimeMs } ?: 0L
        val newClip = VideoClip(
            id = UUID.randomUUID().toString(),
            name = name,
            uri = uri,
            sampleVisualType = if (current.clips.size % 2 == 0) "gym" else "success",
            startTimeMs = lastEnd,
            endTimeMs = lastEnd + 5000L,
            sourceDurationMs = 5000L,
            filter = FilterType.CINEMATIC_GOLD
        )
        val newProj = current.copy(clips = current.clips + newClip, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedClipId.value = newClip.id
        saveProjectToDb(newProj)
    }

    // Text Operations
    fun updateText(updatedText: TextOverlay) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newTexts = current.texts.map { if (it.id == updatedText.id) updatedText else it }
        val newProj = current.copy(texts = newTexts, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        saveProjectToDb(newProj)
    }

    fun addNewText(text: String = "UNSTOPPABLE", is3D: Boolean = true) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newText = TextOverlay(
            id = UUID.randomUUID().toString(),
            text = text,
            startTimeMs = _currentPlayheadMs.value,
            endTimeMs = (_currentPlayheadMs.value + 4000L).coerceAtMost(current.totalDurationMs),
            fontSizeSp = 30f,
            is3D = is3D,
            style3D = Style3DType.METALLIC_GOLD,
            animation = TextAnimationType.BOUNCE_POP
        )
        val newProj = current.copy(texts = current.texts + newText, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedTextId.value = newText.id
        _activeToolPanel.value = "text"
        saveProjectToDb(newProj)
    }

    fun deleteActiveText() {
        val current = _projectState.value ?: return
        val textId = _selectedTextId.value ?: return
        pushUndo(current)
        val newTexts = current.texts.filter { it.id != textId }
        val newProj = current.copy(texts = newTexts, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedTextId.value = newTexts.firstOrNull()?.id
        saveProjectToDb(newProj)
    }

    // Audio & Voice Operations
    fun updateAudio(updatedAudio: AudioTrack) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newAudios = current.audios.map { if (it.id == updatedAudio.id) updatedAudio else it }
        val newProj = current.copy(audios = newAudios, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        saveProjectToDb(newProj)
    }

    fun addAudioTrack(audio: AudioTrack) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newProj = current.copy(audios = current.audios + audio, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedAudioId.value = audio.id
        saveProjectToDb(newProj)
    }

    fun startVoiceRecording() {
        val current = _projectState.value ?: return
        audioEngine.startVoiceRecording(current.id)
        _isRecordingVoice.value = true
    }

    fun stopVoiceRecording() {
        val current = _projectState.value ?: return
        val recFile = audioEngine.stopVoiceRecording()
        _isRecordingVoice.value = false

        if (recFile != null && recFile.exists()) {
            val audioTrack = AudioTrack(
                id = UUID.randomUUID().toString(),
                title = "My Voice Recording",
                uri = recFile.absolutePath,
                startTimeMs = _currentPlayheadMs.value,
                durationMs = 8000L,
                isVoiceOver = true
            )
            addAudioTrack(audioTrack)
        }
    }

    // Effects Operations
    fun updateEffect(updatedEffect: EffectOverlay) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newEffects = current.effects.map { if (it.id == updatedEffect.id) updatedEffect else it }
        val newProj = current.copy(effects = newEffects, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        saveProjectToDb(newProj)
    }

    fun addEffect(effect: EffectOverlay) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newProj = current.copy(effects = current.effects + effect, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedEffectId.value = effect.id
        saveProjectToDb(newProj)
    }

    fun deleteActiveEffect() {
        val current = _projectState.value ?: return
        val effId = _selectedEffectId.value ?: return
        pushUndo(current)
        val newEffects = current.effects.filter { it.id != effId }
        val newProj = current.copy(effects = newEffects, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedEffectId.value = newEffects.firstOrNull()?.id
        saveProjectToDb(newProj)
    }

    fun setAspectRatio(ratio: AspectRatioType) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newProj = current.copy(aspectRatio = ratio, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        saveProjectToDb(newProj)
    }

    fun addCaptionsFromAi(captions: List<TextOverlay>) {
        val current = _projectState.value ?: return
        pushUndo(current)
        val newProj = current.copy(texts = current.texts + captions, updatedAt = System.currentTimeMillis())
        _projectState.value = newProj
        _selectedTextId.value = captions.firstOrNull()?.id
        saveProjectToDb(newProj)
    }

    private fun saveProjectToDb(project: ProjectData) {
        viewModelScope.launch {
            val entity = ProjectEntity(
                id = project.id,
                title = project.title,
                aspectRatioName = project.aspectRatio.name,
                durationMs = project.totalDurationMs,
                clipsJson = ProjectJsonUtils.clipsToJson(project.clips),
                textsJson = ProjectJsonUtils.textsToJson(project.texts),
                audiosJson = ProjectJsonUtils.audiosToJson(project.audios),
                effectsJson = ProjectJsonUtils.effectsToJson(project.effects),
                updatedAt = System.currentTimeMillis()
            )
            projectDao.insertProject(entity)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
