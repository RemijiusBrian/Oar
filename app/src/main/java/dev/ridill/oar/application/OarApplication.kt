package dev.ridill.oar.application

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import dev.ridill.oar.core.domain.util.BuildUtil
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class OarApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    private fun setupTimber() {
        if (BuildUtil.isDebug || BuildUtil.isInternal) {
            Timber.plant(Timber.DebugTree())
        }
    }
}