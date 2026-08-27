package com.example.ai

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

object GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateMotivationalCaptions(
        topic: String,
        language: String = "English and Hindi (Hinglish)",
        tone: String = "Aggressive Hustle & High Energy"
    ): List<String> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackCaptions(topic, tone)
        }

        val prompt = """
            You are a viral social media video editor for motivational Instagram Reels, YouTube Shorts and TikTok.
            Generate 5 powerful, punchy, viral motivational video quotes/captions for topic '$topic'.
            Tone: $tone.
            Language: $language.
            Return ONLY a raw JSON array of 5 strings. Example: ["Caption 1", "Caption 2", "Caption 3", "Caption 4", "Caption 5"]
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return@withContext getFallbackCaptions(topic, tone)
            }

            val jsonResp = JSONObject(respBody)
            val text = jsonResp.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanJson = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val array = JSONArray(cleanJson)
            val result = mutableListOf<String>()
            for (i in 0 until array.length()) {
                result.add(array.getString(i))
            }
            if (result.isNotEmpty()) result else getFallbackCaptions(topic, tone)
        } catch (_: Exception) {
            getFallbackCaptions(topic, tone)
        }
    }

    suspend fun generateAutoCaptions(
        audioTheme: String,
        videoDurationMs: Long,
        language: String
    ): List<AutoCaptionSegment> = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackAutoCaptions(audioTheme, videoDurationMs, language)
        }

        val prompt = """
            Create auto-captions for a ${videoDurationMs / 1000} second motivational video about '$audioTheme'.
            Language: $language.
            Generate timed segments between 0ms and ${videoDurationMs}ms.
            Return ONLY a raw JSON array of objects with keys: "startTimeMs" (int), "endTimeMs" (int), "text" (string).
            Example: [{"startTimeMs": 0, "endTimeMs": 2500, "text": "PAIN IS TEMPORARY"}, {"startTimeMs": 2600, "endTimeMs": 5000, "text": "PRIDE IS FOREVER"}]
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""
            if (!response.isSuccessful) return@withContext getFallbackAutoCaptions(audioTheme, videoDurationMs, language)

            val jsonResp = JSONObject(respBody)
            val text = jsonResp.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")

            val cleanJson = text.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
            val array = JSONArray(cleanJson)
            val list = mutableListOf<AutoCaptionSegment>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    AutoCaptionSegment(
                        startTimeMs = obj.getLong("startTimeMs"),
                        endTimeMs = obj.getLong("endTimeMs"),
                        text = obj.getString("text")
                    )
                )
            }
            if (list.isNotEmpty()) list else getFallbackAutoCaptions(audioTheme, videoDurationMs, language)
        } catch (_: Exception) {
            getFallbackAutoCaptions(audioTheme, videoDurationMs, language)
        }
    }

    suspend fun generateVoiceOverScript(
        topic: String,
        persona: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (_: Exception) { "" }
        if (apiKey.isNullOrBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getFallbackVoiceOverScript(topic, persona)
        }

        val prompt = """
            Write a 15-second powerful spoken voice-over script for a motivational vertical reel on '$topic'.
            Speaker persona: $persona.
            Keep it punchy, rhythmic, and high-impact.
            Return ONLY the voice-over script text.
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply { put("text", prompt) })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val respBody = response.body?.string() ?: ""
            if (!response.isSuccessful) return@withContext getFallbackVoiceOverScript(topic, persona)

            val jsonResp = JSONObject(respBody)
            jsonResp.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text").trim()
        } catch (_: Exception) {
            getFallbackVoiceOverScript(topic, persona)
        }
    }

    private fun getFallbackCaptions(topic: String, tone: String): List<String> {
        return listOf(
            "DON'T STOP UNTIL YOU'RE PROUD.",
            "हार मानने का ख्याल भी दिमाग से निकाल दो!",
            "DISCIPLINE IS THE BRIDGE BETWEEN GOALS AND ACCOMPLISHMENT.",
            "मेहनत इतनी खामोशी से करो कि कामयाबी शोर मचा दे।",
            "THEY LAUGHED AT YOUR DREAMS. NOW MAKE THEM WATCH YOUR SUCCESS."
        )
    }

    private fun getFallbackAutoCaptions(
        audioTheme: String,
        videoDurationMs: Long,
        language: String
    ): List<AutoCaptionSegment> {
        val segDuration = (videoDurationMs / 3).coerceAtLeast(2000L)
        return if (language.contains("Hindi", ignoreCase = true)) {
            listOf(
                AutoCaptionSegment(0L, segDuration, "मेहनत कभी बेकार नहीं जाती!"),
                AutoCaptionSegment(segDuration, segDuration * 2, "सपनों को हकीकत में बदलना है।"),
                AutoCaptionSegment(segDuration * 2, videoDurationMs, "रुकना मना है, जीतना तय है!")
            )
        } else {
            listOf(
                AutoCaptionSegment(0L, segDuration, "PAIN IS TEMPORARY."),
                AutoCaptionSegment(segDuration, segDuration * 2, "DISCIPLINE IS FOREVER."),
                AutoCaptionSegment(segDuration * 2, videoDurationMs, "CONQUER YOUR MIND.")
            )
        }
    }

    private fun getFallbackVoiceOverScript(topic: String, persona: String): String {
        return "When you feel like quitting, remember why you started. Every drop of sweat, every late night, every setback is building the warrior inside you. Keep pushing. The crown belongs to the relentless."
    }
}

data class AutoCaptionSegment(
    val startTimeMs: Long,
    val endTimeMs: Long,
    val text: String
)
