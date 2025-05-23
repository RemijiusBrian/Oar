package dev.ridill.oar.folders.data

import dev.ridill.oar.folders.data.local.entity.FolderEntity
import dev.ridill.oar.folders.data.local.views.FolderAndAggregateView
import dev.ridill.oar.folders.domain.model.Folder
import dev.ridill.oar.folders.domain.model.FolderDetails

fun FolderAndAggregateView.toFolderDetails(): FolderDetails = FolderDetails(
    id = id,
    name = name,
    createdTimestamp = createdTimestamp,
    excluded = excluded,
    aggregate = aggregate
)

fun FolderEntity.toFolder(): Folder = Folder(
    id = id,
    name = name,
    createdTimestamp = createdTimestamp,
    excluded = isExcluded
)