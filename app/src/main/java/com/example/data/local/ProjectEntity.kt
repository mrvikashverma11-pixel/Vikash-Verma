package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AspectRatioType
import com.example.model.ExportFps
import com.example.model.ExportResolution

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val aspectRatioName: String = AspectRatioType.REEL_9_16.name,
    val resolutionName: String = ExportResolution.FHD_1080P.name,
    val fpsName: String = ExportFps.FPS_30.name,
    val durationMs: Long,
    val thumbnailUri: String? = null,
    val sampleVisualType: String = "gym",
    val clipsJson: String,
    val textsJson: String,
    val audiosJson: String,
    val effectsJson: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDraft: Boolean = true
)
