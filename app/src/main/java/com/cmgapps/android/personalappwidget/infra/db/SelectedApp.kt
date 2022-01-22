/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.infra.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = Database.SELECTED_APP_TABLE)
data class SelectedApp(
    @PrimaryKey
    @ColumnInfo(name = Database.SELECTED_APP_TABLE_PACKAGE_NAME_COLUMN)
    val packageName: String,
    @ColumnInfo(name = Database.SELECTED_APP_TABLE_ACTIVITY_NAME_COLUMN)
    val activityName: String
)
