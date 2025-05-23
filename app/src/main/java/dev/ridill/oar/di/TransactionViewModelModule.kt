package dev.ridill.oar.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.folders.domain.repository.FolderDetailsRepository
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.repository.AddEditTransactionRepositoryImpl
import dev.ridill.oar.transactions.data.repository.AllTransactionsRepositoryImpl
import dev.ridill.oar.transactions.domain.repository.AddEditTransactionRepository
import dev.ridill.oar.transactions.domain.repository.AllTransactionsRepository
import dev.ridill.oar.transactions.domain.repository.TransactionRepository
import dev.ridill.oar.transactions.presentation.addEditTransaction.AddEditTransactionViewModel
import dev.ridill.oar.transactions.presentation.allTransactions.AllTransactionsViewModel

@Module
@InstallIn(ViewModelComponent::class)
object TransactionViewModelModule {

    @Provides
    fun provideAddEditTransactionRepository(
        dao: TransactionDao,
        repo: TransactionRepository,
        schedulesRepo: SchedulesRepository,
        folderRepo: FolderDetailsRepository
    ): AddEditTransactionRepository = AddEditTransactionRepositoryImpl(
        dao = dao,
        repo = repo,
        schedulesRepo = schedulesRepo,
        folderRepo = folderRepo
    )

    @Provides
    fun provideAddEditTransactionEventBus(): EventBus<AddEditTransactionViewModel.AddEditTransactionEvent> =
        EventBus()

    @Provides
    fun provideAllTransactionsRepository(
        db: OarDatabase,
        dao: TransactionDao,
        transactionRepo: TransactionRepository,
        preferencesManager: PreferencesManager,
        currencyRepository: CurrencyRepository
    ): AllTransactionsRepository = AllTransactionsRepositoryImpl(
        db = db,
        dao = dao,
        repo = transactionRepo,
        preferencesManager = preferencesManager,
        currencyPrefRepo = currencyRepository
    )

    @Provides
    fun provideAllTransactionEventBus(): EventBus<AllTransactionsViewModel.AllTransactionsEvent> =
        EventBus()
}