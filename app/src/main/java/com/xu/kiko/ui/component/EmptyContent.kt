package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun EmptyContent(
    title: String,
    message: String,
    actionText: String?,
    onAction: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (actionText != null && onAction != null) {
            PrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyContentPreview() {
    KikoTheme {
        EmptyContent(
            title = "还没有任务",
            message = "创建一个任务，开始规划今天的专注",
            actionText = "创建任务",
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyContentDarkPreview() {
    KikoTheme(darkTheme = true) {
        EmptyContent(
            title = "还没有任务",
            message = "创建一个任务，开始规划今天的专注",
            actionText = "创建任务",
            onAction = {}
        )
    }
}