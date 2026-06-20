package com.xu.kiko.domain.usecase.task

import com.xu.kiko.domain.model.TaskCategory


data class TaskInput(
    val title: String,
    val note: String,
    val category: TaskCategory,
    val estimatedPomodoros: Int
)

enum class TaskFieldError{
    REQUIRED,
    TITLE_TOO_LONG,
    NOTE_TOO_LONG,
    INVALID_POMODORO_COUNT
}

data class TaskValidationResult(
    val titleError: TaskFieldError? = null,
    val noteError: TaskFieldError? = null,
    val pomodoroError: TaskFieldError? = null
) {
    val isValid: Boolean
        get() = titleError == null &&
                noteError == null &&
                pomodoroError == null
}

object TaskValidationRules {
    const val MAX_TITLE_LENGTH = 80
    const val MAX_NOTE_LENGTH = 300
    const val MIN_ESTIMATED_POMODOROS = 1
    const val MAX_ESTIMATED_POMODOROS = 20
    const val DEFAULT_ESTIMATED_POMODOROS = 4
}

class ValidateTaskUseCase {
    operator fun invoke(
        input: TaskInput
    ): TaskValidationResult {
        val normalizedTitle = input.title.trim()

        val titleError = when{
            normalizedTitle.isEmpty() ->
                TaskFieldError.REQUIRED

            normalizedTitle.length >
                    TaskValidationRules.MAX_TITLE_LENGTH ->
                TaskFieldError.TITLE_TOO_LONG

            else -> null
        }
        val noteError =
            if (
                input.note.length >
                TaskValidationRules.MAX_NOTE_LENGTH
            ){
                TaskFieldError.NOTE_TOO_LONG
            }else{
                null
            }
        val pomodoroError =
            if (
                input.estimatedPomodoros !in
                TaskValidationRules.MIN_ESTIMATED_POMODOROS..TaskValidationRules.MAX_ESTIMATED_POMODOROS
            ){
                TaskFieldError.INVALID_POMODORO_COUNT
            }else{
                null
            }

        return TaskValidationResult(
            titleError = titleError,
            noteError = noteError,
            pomodoroError = pomodoroError
        )
    }
}
