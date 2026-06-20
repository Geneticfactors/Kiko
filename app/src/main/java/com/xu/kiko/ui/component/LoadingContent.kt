package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentPreview() {
    KikoTheme {
        LoadingContent()
    }
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentDarkPreview() {
    KikoTheme(darkTheme = true) {
        LoadingContent()
    }
}