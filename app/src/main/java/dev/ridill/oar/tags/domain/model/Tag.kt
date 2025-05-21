package dev.ridill.oar.tags.domain.model

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.Empty
import dev.ridill.oar.core.ui.theme.SelectableColorsList
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class Tag(
    val id: Long,
    val name: String,
    val colorCode: Int,
    val createdTimestamp: LocalDateTime,
    val excluded: Boolean
) : Parcelable {
    val color: Color
        get() = Color(colorCode)

    val createdTimestampFormatted: String
        get() = createdTimestamp.format(DateUtil.Formatters.localizedDateMedium)

    companion object {
        val NEW = Tag(
            id = OarDatabase.DEFAULT_ID_LONG,
            name = String.Empty,
            colorCode = SelectableColorsList.first().toArgb(),
            createdTimestamp = DateUtil.now(),
            excluded = false
        )
    }
}