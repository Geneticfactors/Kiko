package com.xu.kiko.ui.component

import androidx.compose.material3.Button
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 56.dp),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.extraLarge
    ){
        if (loading){
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }else{
            Text(text = text)
        }
    }
}

@Preview(
    name = "Primary Button - Light",
    showBackground = true
)
@Composable
private fun PrimaryButtonLightPreview() {
    KikoTheme(darkTheme = false) {
        PrimaryButton(
            text = "开始",
            onClick = {}
        )
    }
}

@Preview(
    name = "Primary Button - Dark",
    showBackground = true
)
@Composable
private fun PrimaryButtonDarkPreview() {
    KikoTheme(darkTheme = true) {
        PrimaryButton(
            text = "开始",
            onClick = {},
            loading = true
        )
    }
}