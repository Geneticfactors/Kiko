package com.xu.kiko.ui.screen.profile

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xu.kiko.R
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.spacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ProfileHeader(
    avatarText: String,
    avatarImagePath: String?,
    nickname: String,
    joinedDays: Int,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.large
        )
    ) {
        ProfileAvatar(
            avatarText = avatarText,
            avatarImagePath = avatarImagePath,
            onClick = onAvatarClick
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                MaterialTheme.spacing.extraSmall
            )
        ) {
            Text(
                text = nickname,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = stringResource(
                    R.string.profile_joined_days,
                    joinedDays
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    avatarText: String,
    avatarImagePath: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarBitmap = produceState<ImageBitmap?>(
        initialValue = null,
        avatarImagePath
    ) {
        value = avatarImagePath?.let { path ->
            loadAvatarBitmap(path)
        }
    }
    val avatarDescription =
        stringResource(R.string.profile_change_avatar)

    Box(
        modifier = modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = avatarDescription
                role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        val bitmap = avatarBitmap.value
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = avatarText,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

private suspend fun loadAvatarBitmap(path: String): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(path, bounds)

        val options = BitmapFactory.Options().apply {
            inSampleSize = sampleSizeFor(
                width = bounds.outWidth,
                height = bounds.outHeight
            )
        }
        BitmapFactory.decodeFile(path, options)?.asImageBitmap()
    }
}

private fun sampleSizeFor(
    width: Int,
    height: Int
): Int {
    var sampleSize = 1
    while (
        width / sampleSize > AVATAR_DECODE_SIZE ||
        height / sampleSize > AVATAR_DECODE_SIZE
    ) {
        sampleSize *= 2
    }
    return sampleSize
}

private const val AVATAR_DECODE_SIZE = 256

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderPreview() {
    KikoTheme {
        ProfileHeader(
            avatarText = "FF",
            avatarImagePath = null,
            nickname = "用户昵称",
            joinedDays = 32,
            onAvatarClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 320)
@Composable
private fun ProfileHeaderLongNamePreview() {
    KikoTheme {
        ProfileHeader(
            avatarText = "刻刻",
            avatarImagePath = null,
            nickname = "这是一个非常非常长的用户昵称",
            joinedDays = 128,
            onAvatarClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileHeaderDarkPreview() {
    KikoTheme(darkTheme = true) {
        ProfileHeader(
            avatarText = "FF",
            avatarImagePath = null,
            nickname = "用户昵称",
            joinedDays = 32,
            onAvatarClick = {}
        )
    }
}

