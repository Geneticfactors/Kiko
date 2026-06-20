package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing

@Composable
fun KikoCard(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues =
        PaddingValues(MaterialTheme.spacing.large),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            content = content
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun KikoCardPreview() {
    KikoTheme {
        KikoCard {
            Text(text = "Kiko Card")
        }
    }
}
