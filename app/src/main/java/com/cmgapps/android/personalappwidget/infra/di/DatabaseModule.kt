/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.infra.di

import android.content.Context
import androidx.room.Room
import com.cmgapps.android.personalappwidget.infra.db.Database
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): Database = Room.databaseBuilder(context, Database::class.java, "widget-database.db").build()

    @Provides
    fun provideAppDao(database: Database) = database.appDao()
}
