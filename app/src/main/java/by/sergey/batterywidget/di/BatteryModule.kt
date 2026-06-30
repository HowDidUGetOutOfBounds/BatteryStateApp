package by.sergey.batterywidget.di

import android.app.Application
import android.content.Context
import by.sergey.batterywidget.data.BatteryRepositoryImpl
import by.sergey.batterywidget.data.BatteryStatusDataSource
import by.sergey.batterywidget.domain.repository.BatteryRepository
import by.sergey.batterywidget.domain.usecase.ObserveBatteryUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BatteryModule {

    @Provides
    @Singleton
    fun provideBatteryStatusDataSource(
        @ApplicationContext context: Context
    ): BatteryStatusDataSource = BatteryStatusDataSource(context)

    @Provides
    @Singleton
    fun provideBatteryRepository(
        dataSource: BatteryStatusDataSource,
        app: Application
    ): BatteryRepository = BatteryRepositoryImpl(dataSource, app)

    @Provides
    @Singleton
    fun provideObserveBatteryUseCase(
        repository: BatteryRepository
    ): ObserveBatteryUseCase = ObserveBatteryUseCase(repository)
}
