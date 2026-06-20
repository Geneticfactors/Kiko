package com.xu.kiko.domain.repository

import com.xu.kiko.domain.model.StatisticsData
import com.xu.kiko.domain.model.StatisticsPeriod
import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun observeStatistics(
        period: StatisticsPeriod
    ): Flow<StatisticsData>
}
