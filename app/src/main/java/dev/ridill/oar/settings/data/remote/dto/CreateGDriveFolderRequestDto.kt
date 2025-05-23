package dev.ridill.oar.settings.data.remote.dto

import com.google.errorprone.annotations.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import dev.ridill.oar.settings.data.repository.APP_DATA_SPACE
import dev.ridill.oar.settings.data.repository.G_DRIVE_FOLDER_MIME_TYPE

@Keep
data class CreateGDriveFolderRequestDto(
    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("parents")
    val parents: List<String> = listOf(APP_DATA_SPACE),

    @Expose
    @SerializedName("mimeType")
    val mimeType: String = G_DRIVE_FOLDER_MIME_TYPE
)