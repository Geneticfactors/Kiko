package com.xu.kiko.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.KikoThemeColor
import com.xu.kiko.ui.theme.spacing

@Composable
fun ThemeColorDialog(
    selectedThemeColor: KikoThemeColor,
    onThemeColorSelected: (KikoThemeColor) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        ThemeColorDialogContent(
            selectedThemeColor = selectedThemeColor,
            onThemeColorSelected = onThemeColorSelected
        )
    }
}

@Composable
private fun ThemeColorDialogContent(
    selectedThemeColor: KikoThemeColor,
    onThemeColorSelected: (KikoThemeColor) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.section,
                vertical = MaterialTheme.spacing.section
            ),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.large
            )
        ) {
            Text(
                text = stringResource(R.string.profile_theme_color_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.small
                )
            ) {
                themeColorOptions().forEach { option ->
                    ThemeColorOptionRow(
                        option = option,
                        selected = option.themeColor == selectedThemeColor,
                        onClick = {
                            onThemeColorSelected(option.themeColor)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeColorOptionRow(
    option: ThemeColorOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDescription =
        stringResource(R.string.profile_selected_theme)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .clickable(onClick = onClick)
            .padding(vertical = MaterialTheme.spacing.small)
            .semantics {
                if (selected) {
                    contentDescription = selectedDescription
                }
            },
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.medium
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(option.themeColor.color)
        )

        Text(
            text = stringResource(option.nameRes),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        RadioButton(
            selected = selected,
            onClick = onClick
        )
    }
}

private data class ThemeColorOption(
    val themeColor: KikoThemeColor,
    val nameRes: Int
)

private fun themeColorOptions(): List<ThemeColorOption> {
    return listOf(
        ThemeColorOption(
            themeColor = KikoThemeColor.BLUE,
            nameRes = R.string.profile_theme_blue
        ),
        ThemeColorOption(
            themeColor = KikoThemeColor.GREEN,
            nameRes = R.string.profile_theme_green
        ),
        ThemeColorOption(
            themeColor = KikoThemeColor.AMBER,
            nameRes = R.string.profile_theme_amber
        ),
        ThemeColorOption(
            themeColor = KikoThemeColor.PINK,
            nameRes = R.string.profile_theme_pink
        ),
        ThemeColorOption(
            themeColor = KikoThemeColor.ORANGE,
            nameRes = R.string.profile_theme_orange
        ),
        ThemeColorOption(
            themeColor = KikoThemeColor.VIOLET,
            nameRes = R.string.profile_theme_violet
        )
    )
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ThemeColorDialogPreview() {
    KikoTheme {
        ThemeColorDialogContent(
            selectedThemeColor = KikoThemeColor.BLUE,
            onThemeColorSelected = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 390)
@Composable
private fun ThemeColorDialogDarkPreview() {
    KikoTheme(darkTheme = true) {
        ThemeColorDialogContent(
            selectedThemeColor = KikoThemeColor.VIOLET,
            onThemeColorSelected = {}
        )
    }
}

