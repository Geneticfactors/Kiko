package com.xu.kiko.ui.screen.auth

/**
 * 登录/注册表单输入校验错误类型枚举
 * 用于标识不同字段的校验失败原因，供 UI 层显示对应的错误提示
 */
enum class AuthFieldError {
    // 字段为空或仅包含空白字符
    REQUIRED,

    // 长度不符合要求（适用于昵称）
    INVALID_LENGTH,

    // 手机号格式不正确
    INVALID_PHONE,

    // 密码长度不足
    PASSWORD_TOO_SHORT,

    // 确认密码与原密码不一致
    PASSWORD_MISMATCH
}

/**
 * 认证输入校验规则常量
 * 集中管理所有校验阈值，便于统一修改和维护
 */
internal object AuthValidationRules {
    // 昵称最小长度
    const val MIN_NICKNAME_LENGTH = 2

    // 昵称最大长度
    const val MAX_NICKNAME_LENGTH = 20

    // 手机号标准长度（中国大陆）
    const val PHONE_LENGTH = 11

    // 密码最小长度
    const val MIN_PASSWORD_LENGTH = 8
}

/**
 * 规范化电话号码格式
 * 移除用户输入中的空格、连字符、括号等分隔符，只保留数字
 *
 * @param value 用户输入的原始电话号码
 * @return 规范化后的纯数字字符串
 */
internal fun normalizePhone(value: String): String {
    return value.filterNot { it == ' ' || it == '-' || it == '(' || it == ')' }
}

/**
 * 校验昵称输入
 *
 * @param value 用户输入的昵称
 * @return 校验错误类型，[null] 表示校验通过
 */
internal fun validateNickname(value: String): AuthFieldError? {
    return when {
        value.isBlank() -> AuthFieldError.REQUIRED
        value.length !in AuthValidationRules.MIN_NICKNAME_LENGTH..AuthValidationRules.MAX_NICKNAME_LENGTH -> AuthFieldError.INVALID_LENGTH
        else -> null
    }
}

/**
 * 校验电话号码输入
 * 先调用 [normalizePhone] 规范化，再检查长度是否为 11 位且全为数字
 *
 * @param value 用户输入的电话号码
 * @return 校验错误类型，[null] 表示校验通过
 */
internal fun validatePhone(value: String): AuthFieldError? {
    if (value.isBlank()) {
        return AuthFieldError.REQUIRED
    }
    val normalizedPhone = normalizePhone(value)

    return if (
        normalizedPhone.length != AuthValidationRules.PHONE_LENGTH || !normalizedPhone.all { it.isDigit() }
    ) {
        AuthFieldError.INVALID_PHONE
    } else {
        null
    }
}

/**
 * 校验密码输入
 *
 * @param value 用户输入的密码
 * @return 校验错误类型，[null] 表示校验通过
 */
internal fun validatePassword(value: String): AuthFieldError? {
    return when {
        value.isBlank() -> AuthFieldError.REQUIRED
        value.length < AuthValidationRules.MIN_PASSWORD_LENGTH -> AuthFieldError.PASSWORD_TOO_SHORT
        else -> null
    }
}

/**
 * 校验确认密码与原密码是否一致
 *
 * @param password 原密码
 * @param confirmation 确认密码
 * @return 校验错误类型，[null] 表示校验通过
 */
internal fun validatePasswordConfirmation(
    password: String,
    confirmation: String,
): AuthFieldError? {
    return when {
        confirmation.isBlank() -> AuthFieldError.REQUIRED
        password != confirmation -> AuthFieldError.PASSWORD_MISMATCH
        else -> null
    }
}
