package com.xu.kiko.ui.screen.statistics

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.StatisticsRepository

class StatisticsViewModelFactory(
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
