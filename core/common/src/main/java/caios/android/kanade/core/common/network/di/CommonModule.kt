package caios.android.kanade.core.common.network.di

import caios.android.kanade.core.common.network.DateTimeProvider
import caios.android.kanade.core.common.network.DefaultDateTimeProvider
import caios.android.kanade.core.common.network.moshi.DurationAdapter
import caios.android.kanade.core.common.network.moshi.InstantAdapter
import caios.android.kanade.core.common.network.moshi.LocalDateAdapter
import caios.android.kanade.core.common.network.moshi.OffsetDateTimeAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class CommonModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .add(LocalDateAdapter)
            .add(DurationAdapter)
            .add(OffsetDateTimeAdapter)
            .add(InstantAdapter)
            .build()

    @Provides
    @Singleton
    fun provideStateFlow(): MutableSharedFlow<Int> = MutableSharedFlow()
}

@Module
@InstallIn(SingletonComponent::class)
internal interface CommonModuleBinds {

    @Binds
    @Singleton
    fun provideTimeProvider(dateTimeProvider: DefaultDateTimeProvider): DateTimeProvider
}
 const val EVENT_RESTART_APP = 1