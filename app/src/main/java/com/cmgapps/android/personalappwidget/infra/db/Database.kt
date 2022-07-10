/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.infra.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * from selected_app")
    fun getAll(): Flow<List<SelectedApp>>

    @Query("SELECT * from selected_app")
    fun getFavoriteApps(): List<SelectedApp>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(selectedApp: SelectedApp)

    @Delete
    suspend fun delete(selectedApp: SelectedApp)
}

@Database(entities = [SelectedApp::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun appDao(): AppDao

    companion object {
        const val SELECTED_APP_TABLE = "selected_app"
        const val SELECTED_APP_TABLE_PACKAGE_NAME_COLUMN = "package_name"
        const val SELECTED_APP_TABLE_ACTIVITY_NAME_COLUMN = "activity_name"
    }
}
