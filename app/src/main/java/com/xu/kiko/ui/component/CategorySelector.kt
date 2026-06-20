package com.xu.kiko.ui.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing
import androidx.compose.material3.MaterialTheme

@Composable
fun CategorySelector(
    selectedCategory: TaskCategory,
    onCategorySelected: (TaskCategory) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.small
        )
    ) {
        TaskCategory.entries.forEach { category ->
            KikoFilterChip(
                text = taskCategoryText(category),
                selected = selectedCategory == category,
                onClick = {
                    onCategorySelected(category)
                },
                enabled = enabled
            )
        }
    }
}

@Composable
private fun taskCategoryText(category: TaskCategory): String {
    return when (category) {
        TaskCategory.STUDY ->
            stringResource(R.string.task_category_study)

        TaskCategory.WORK ->
            stringResource(R.string.task_category_work)

        TaskCategory.READING ->
            stringResource(R.string.task_category_reading)
    }
}

@Preview(showBackground = true)
@Composable
private fun CategorySelectorPreview() {
    KikoTheme {
        CategorySelector(
            selectedCategory = TaskCategory.STUDY,
            onCategorySelected = {}
        )
    }
}