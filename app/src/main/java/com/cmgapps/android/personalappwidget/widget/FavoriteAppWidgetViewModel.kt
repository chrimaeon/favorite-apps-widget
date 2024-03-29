/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.personalappwidget.widget

import com.cmgapps.android.personalappwidget.repository.AppsRepository
import javax.inject.Inject

class FavoriteAppWidgetViewModel
    @Inject
    constructor(
        private val appsRepository: AppsRepository,
    ) {
        suspend fun getFavoriteApps() = appsRepository.getFavoriteApps()
    }
