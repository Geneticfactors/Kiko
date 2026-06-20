package com.xu.kiko.ui.component

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun DangerTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.heightIn(min = 48.dp),
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Text(text = text)
    }
}

@Preview(showBackground = true)
@Composable
private fun DangerTextButtonPreview() {
    KikoTheme {
        DangerTextButton(
            text = "删除",
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DangerTextButtonDarkPreview() {
    KikoTheme(darkTheme = true) {
        DangerTextButton(
            text = "删除",
            onClick = {}
        )
    }
}

