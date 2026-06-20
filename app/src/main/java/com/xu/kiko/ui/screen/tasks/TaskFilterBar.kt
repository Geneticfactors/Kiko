package com.xu.kiko.ui.screen.tasks

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoFilterChip
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@Composable
fun TaskFilterBar(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.small
        )
    ) {
        taskFiltersInDisplayOrder().forEach { filter ->
            KikoFilterChip(
                text = taskFilterText(filter),
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                },
                enabled = enabled
            )
        }
    }
}

private fun taskFiltersInDisplayOrder(): List<TaskFilter> {
    return listOf(
        TaskFilter.ALL,
        TaskFilter.STUDY,
        TaskFilter.WORK,
        TaskFilter.READING
    )
}

@Composable
private fun taskFilterText(filter: TaskFilter): String {
    return when (filter) {
        TaskFilter.ALL ->
            stringResource(R.string.tasks_filter_all)

        TaskFilter.STUDY ->
            stringResource(R.string.tasks_filter_study)

        TaskFilter.WORK ->
            stringResource(R.string.tasks_filter_work)

        TaskFilter.READING ->
            stringResource(R.string.tasks_filter_reading)
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskFilterBarPreview() {
    KikoTheme {
        TaskFilterBar(
            selectedFilter = TaskFilter.ALL,
            onFilterSelected = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun TaskFilterBarNarrowPreview() {
    KikoTheme {
        TaskFilterBar(
            selectedFilter = TaskFilter.READING,
            onFilterSelected = {}
        )
    }
}
