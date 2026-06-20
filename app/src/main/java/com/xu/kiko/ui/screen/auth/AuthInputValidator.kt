package com.xu.kiko.ui.screen.auth

enum class AuthFieldError {
    REQUIRED,
    INVALID_LENGTH,
    INVALID_PHONE,
    PASSWORD_TOO_SHORT,
    PASSWORD_MISMATCH
}

internal object AuthValidationRules {
    const val MIN_NICKNAME_LENGTH = 2
    const val MAX_NICKNAME_LENGTH = 20
    const val PHONE_LENGTH = 11
    const val MIN_PASSWORD_LENGTH = 8
}

//规范电话号码格式
internal fun normalizePhone(value: String): String {
    return value.filterNot { it == ' ' || it =='-' || it == '(' || it == ')' }
}

//校验昵称长度
internal fun validateNickname(value: String): AuthFieldError? {
    return when {
        value.isBlank() -> AuthFieldError.REQUIRED
        value.length !in AuthValidationRules.MIN_NICKNAME_LENGTH..
            AuthValidationRules.MAX_NICKNAME_LENGTH -> AuthFieldError.INVALID_LENGTH
        else -> null
    }
}

//校验电话号码长度
internal fun validatePhone(value: String): AuthFieldError? {
    if (value.isBlank()){
        return AuthFieldError.REQUIRED
    }
    val normalizedPhone = normalizePhone(value)

    return if (
        normalizedPhone.length != AuthValidationRules.PHONE_LENGTH || !normalizedPhone.all {it.isDigit()}
    ){
        AuthFieldError.INVALID_PHONE
    }else {
        null
    }
}

//校验密码长度
internal fun validatePassword(value: String): AuthFieldError? {
    return when{
        value.isBlank() -> AuthFieldError.REQUIRED
        value.length < AuthValidationRules.MIN_PASSWORD_LENGTH -> AuthFieldError.PASSWORD_TOO_SHORT

        else -> null
    }
}

//校验确认密码与密码是否一致
internal fun validatePasswordConfirmation(
    password: String,
    confirmation: String,
): AuthFieldError?{
    return when {
        confirmation.isBlank() -> AuthFieldError.REQUIRED
        password != confirmation -> AuthFieldError.PASSWORD_MISMATCH
        else -> null
    }
}
