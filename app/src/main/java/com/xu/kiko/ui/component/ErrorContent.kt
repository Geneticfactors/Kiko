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
import com.xu.kiko.ui.theme.spacing

@Composable
fun ErrorContent(
    message: String,
    retryText: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.large,
            Alignment.CenterVertically
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )

        SecondaryButton(
            text = retryText,
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentPreview() {
    KikoTheme {
        ErrorContent(
            message = "任务加载失败",
            retryText = "重试",
            onRetry = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorContentDarkPreview() {
    KikoTheme(darkTheme = true) {
        ErrorContent(
            message = "任务加载失败",
            retryText = "重试",
            onRetry = {}
        )
    }
}