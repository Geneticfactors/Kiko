package com.xu.kiko.ui.screen.focus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.PrimaryButton
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDurationSheet(
    currentMinutes: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var selectedMinutes by rememberSaveable(currentMinutes) {
        mutableIntStateOf(currentMinutes.coerceIn(5,120))
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(
                    horizontal = MaterialTheme.spacing.extraLarge
                )
        ) {
            Text(
                text = stringResource(
                    R.string.focus_custom_duration_title
                ),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Text(
                text = stringResource(
                    R.string.focus_custom_duration_value,
                    selectedMinutes
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            Slider(
                value = selectedMinutes.toFloat(),
                onValueChange = { value ->
                    selectedMinutes = value
                        .roundToInt()
                        .coerceIn(5,120)
                },
                valueRange = 5f..120f
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.medium
                )
            ) {
                OutlinedButton(
                    onClick = {
                        selectedMinutes =
                            (selectedMinutes - 5).coerceIn(5,120)
                    },
                    enabled = selectedMinutes > 5,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(
                            R.string.focus_custom_duration_decrease
                        )
                    )
                }

                OutlinedButton(
                    onClick = {
                        selectedMinutes =
                            (selectedMinutes + 5).coerceIn(5,120)
                    },
                    enabled = selectedMinutes < 120,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(
                            R.string.focus_custom_duration_increase
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))

            PrimaryButton(
                text = stringResource(R.string.focus_confirm),
                onClick = {
                    onConfirm(selectedMinutes)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.large))
        }
    }
}

@Preview(
    name = "Custom Duration Sheet",
    showBackground = true
)
@Composable
private fun CustomDurationSheetPreview() {
    KikoTheme {
        CustomDurationSheet(
            currentMinutes = 30,
            onDismiss = {},
            onConfirm = {}
        )
    }
}