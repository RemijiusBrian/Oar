package dev.ridill.oar.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GDriveFilesListResponse(
    @Expose
    @SerializedName("files")
    val files: List<GDriveFileDto>,
//    @Expose
//    @SerializedName("incompleteSearch")
//    val incompleteSearch: Boolean,
//    @Expose
//    @SerializedName("kind")
//    val kind: String
)