package dev.ridill.oar.folders.presentation.folderDetails

import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.Empty
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.folders.domain.model.AggregateType
import java.time.LocalDateTime
import java.util.Currency

data class FolderDetailsState(
    val folderName: String = String.Empty,
    val createdTimestamp: LocalDateTime = DateUtil.now(),
    val isExcluded: Boolean = false,
    val aggregateAmount: Double = Double.Zero,
    val currency: Currency = LocaleUtil.defaultCurrency,
    val aggregateType: AggregateType = AggregateType.BALANCED,
    val shouldShowActionPreview: Boolean = false,
    val showDeleteConfirmation: Boolean = false
) {
    val createdTimestampFormatted: String
        get() = createdTimestamp.format(DateUtil.Formatters.localizedDateMedium)
}