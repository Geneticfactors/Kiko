package com.xu.kiko.ui.screen.statistics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.StatisticsRepository

/**
 * 统计页面 ViewModel 工厂类
 * 负责创建 [StatisticsViewModel] 并注入所需依赖
 */
class StatisticsViewModelFactory(
    // 统计数据仓库
    private val statisticsRepository: StatisticsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(statisticsRepository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        /**
         * 从 [Context] 创建 [StatisticsViewModelFactory]
         * 通过 [AppDependencies] 获取所需依赖
         */
        fun fromContext(
            context: Context,
            currentUserId: String
        ): StatisticsViewModelFactory {
            return StatisticsViewModelFactory(
                AppDependencies.statisticsRepository(
                    context = context,
                    currentUserId = currentUserId
                )
            )
        }
    }
}
