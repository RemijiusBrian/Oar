package dev.ridill.oar.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GDriveFileDto(
    @Expose
    @SerializedName("id")
    val id: String,

    @Expose
    @SerializedName("name")
    val name: String,

    @Expose
    @SerializedName("parents")
    val parents: List<String>,

    @Expose
    @SerializedName("appProperties")
    val appProperties: Map<String, Any>,
)