package com.pmirkelam.tatatechnodemo.di

import android.content.Context
import androidx.room.Room
import com.pmirkelam.tatatechnodemo.data.local.AppDatabase
import com.pmirkelam.tatatechnodemo.data.providers.IavAppDataProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

/**
 * Provide Database and ContentProvider object
 * mark it singleton as both objects having lifecycle same as application
 * */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "random_texts_db").build()
    }

    @Provides
    fun provideIavAppDataProvider(@ApplicationContext context: Context): IavAppDataProvider {
        return IavAppDataProvider(context)
    }

}