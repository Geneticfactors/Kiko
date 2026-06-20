package com.xu.kiko.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**全局圆角规范，对应输入框、卡片、按钮和 Bottom Sheet。*/
val KikoShapes = Shapes(
    //小型图标容器和紧凑控件
    extraSmall = RoundedCornerShape(8.dp),
    //输入框和小型卡片
    small = RoundedCornerShape(12.dp),
    //常规卡片和筛选胶囊
    medium = RoundedCornerShape(16.dp),
    //大型内容卡片
    large = RoundedCornerShape(18.dp),
    //主按钮和 Bottom Sheet 顶部圆角
    extraLarge = RoundedCornerShape(28.dp)
)
