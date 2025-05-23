package dev.ridill.oar.settings.data.remote.dto

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class GDriveErrorDto(
    @Expose
    @SerializedName("error")
    val data: ErrorData
)

data class ErrorData(
    @Expose
    @SerializedName("errors")
    val reasons: List<ErrorReason>,
    @Expose
    @SerializedName("code")
    val code: Int,
    @Expose
    @SerializedName("message")
    val message: String
)

data class ErrorReason(
    @Expose
    @SerializedName("domain")
    val domain: String,
    @Expose
    @SerializedName("reason")
    val reason: String,
    @Expose
    @SerializedName("message")
    val message: String
)