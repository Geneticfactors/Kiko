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

/**
 * 个人中心头部组件
 * 展示用户头像、昵称和注册天数
 */
@Composable
fun ProfileHeader(
    // 头像文本（昵称首字）
    avatarText: String,

    // 头像图片路径
    avatarImagePath: String?,

    // 用户昵称
    nickname: String,

    // 注册天数
    joinedDays: Int,

    // 头像点击回调
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
        // 头像组件
        ProfileAvatar(
            avatarText = avatarText,
            avatarImagePath = avatarImagePath,
            onClick = onAvatarClick
        )

        // 用户信息列
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

/**
 * 用户头像组件
 * 支持图片头像和文本头像两种模式
 */
@Composable
private fun ProfileAvatar(
    // 头像文本
    avatarText: String,

    // 头像图片路径
    avatarImagePath: String?,

    // 点击回调
    onClick: () -> Unit,

    modifier: Modifier = Modifier
) {
    // 异步加载头像位图
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
            // 显示图片头像
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // 显示文本头像
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

/**
 * 从文件路径加载头像位图
 * 执行采样以避免内存溢出
 *
 * @param path 头像文件路径
 * @return 头像位图，加载失败返回 null
 */
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

/**
 * 计算位图采样大小
 * 根据目标尺寸计算合适的采样比例，避免加载过大的位图
 *
 * @param width 原始宽度
 * @param height 原始高度
 * @return 采样大小（2的幂次方）
 */
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

// 头像解码的最大尺寸
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

