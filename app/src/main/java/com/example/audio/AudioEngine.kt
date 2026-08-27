package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.AudioTrack as ModelAudioTrack
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale
import kotlin.math.sin

class AudioEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private var mediaRecorder: MediaRecorder? = null
    private var currentRecordingFile: File? = null
    private var synthTrack: AudioTrack? = null
    private var isSynthPlaying = false

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.ENGLISH
                isTtsReady = true
            }
        }
    }

    fun startVoiceRecording(projectId: String): File? {
        return try {
            val audioDir = File(context.cacheDir, "voice_recordings").apply { mkdirs() }
            val file = File(audioDir, "rec_${projectId}_${System.currentTimeMillis()}.m4a")
            currentRecordingFile = file

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(file.absolutePath)
                prepare()
                start()
            }
            file
        } catch (_: Exception) {
            currentRecordingFile = null
            null
        }
    }

    fun stopVoiceRecording(): File? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            currentRecordingFile
        } catch (_: Exception) {
            mediaRecorder = null
            currentRecordingFile
        }
    }

    fun speakText(text: String, isHindi: Boolean = false, onDone: () -> Unit = {}) {
        if (!isTtsReady || tts == null) {
            onDone()
            return
        }
        if (isHindi) {
            tts?.language = Locale("hi", "IN")
        } else {
            tts?.language = Locale.US
        }
        tts?.setPitch(0.95f) // Slightly deeper cinematic voice
        tts?.setSpeechRate(0.95f)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                onDone()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                onDone()
            }
        })
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TTS_${System.currentTimeMillis()}")
    }

    fun stopTts() {
        tts?.stop()
    }

    // Play synthetic motivational background frequencies (Deep Bass Pulse / Synth Arp)
    fun playSyntheticBeat(theme: String) {
        stopSyntheticBeat()
        isSynthPlaying = true
        Thread {
            try {
                val sampleRate = 44100
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                synthTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                synthTrack?.play()

                val buffer = ShortArray(1024)
                var angle = 0.0
                val baseFreq = when (theme) {
                    "gym_trap" -> 110.0 // Low A power
                    "phonk_drive" -> 130.81 // C3
                    "deep_stoic" -> 73.42 // D2 Sub
                    "epic_orchestra" -> 98.0 // G2
                    else -> 120.0
                }

                while (isSynthPlaying) {
                    for (i in buffer.indices) {
                        val sample = (sin(angle) * 0.35 * Short.MAX_VALUE).toInt().toShort()
                        buffer[i] = sample
                        angle += 2 * Math.PI * baseFreq / sampleRate
                    }
                    synthTrack?.write(buffer, 0, buffer.size)
                }
            } catch (_: Exception) {}
        }.start()
    }

    fun stopSyntheticBeat() {
        isSynthPlaying = false
        try {
            synthTrack?.stop()
            synthTrack?.release()
            synthTrack = null
        } catch (_: Exception) {}
    }

    fun generateWaveformForTrack(durationMs: Long, sampleKey: String): List<Float> {
        val count = 28
        val basePattern = when (sampleKey) {
            "gym_trap" -> listOf(0.4f, 0.9f, 0.6f, 1.0f, 0.7f, 0.95f, 0.4f, 0.8f, 0.5f, 1.0f)
            "phonk_drive" -> listOf(0.7f, 0.85f, 1.0f, 0.9f, 0.75f, 1.0f, 0.8f, 0.6f, 0.95f, 0.9f)
            "deep_stoic" -> listOf(0.2f, 0.4f, 0.5f, 0.7f, 0.8f, 0.6f, 0.5f, 0.75f, 0.4f, 0.3f)
            "epic_orchestra" -> listOf(0.3f, 0.5f, 0.7f, 0.9f, 1.0f, 0.85f, 0.6f, 0.95f, 0.7f, 0.5f)
            else -> listOf(0.3f, 0.6f, 0.8f, 0.7f, 0.9f, 0.5f, 0.4f, 0.85f, 0.6f, 0.3f)
        }
        return List(count) { i ->
            basePattern[i % basePattern.size] * (0.8f + (i % 3) * 0.1f)
        }
    }

    fun release() {
        stopSyntheticBeat()
        stopTts()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        try {
            mediaRecorder?.release()
        } catch (_: Exception) {}
    }
}
