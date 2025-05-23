package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.tags.data.local.TagsDao
import dev.ridill.oar.tags.data.repository.TagsRepositoryImpl
import dev.ridill.oar.tags.domain.repository.TagsRepository
import dev.ridill.oar.tags.presentation.addEditTag.AddEditTagViewModel

@Module
@InstallIn(ViewModelComponent::class)
object TagModule {

    @Provides
    fun provideTagsDao(db: OarDatabase): TagsDao = db.tagsDao()

    @Provides
    fun provideTagsRepository(dao: TagsDao): TagsRepository = TagsRepositoryImpl(dao)

    @Provides
    fun provideAddEditTagEventBus(): EventBus<AddEditTagViewModel.AddEditTagEvent> = EventBus()
}