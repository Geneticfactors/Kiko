package com.xu.kiko.ui.screen.focus

import com.xu.kiko.domain.model.Task

/**
 * 专注计时器状态枚举
 * 表示和管理专注计时器当前状态
 */
enum class FocusTimerStatus {
    // 空闲状态，未开始计时
    IDLE,

    // 运行状态，正在计时
    RUNNING,

    // 暂停状态，计时已暂停
    PAUSED
}

/**
 * 专注时长选项
 * 包含 25/45 分钟两个固定选项，以及一个自定义时长选项
 */
sealed interface FocusDurationOption {
    // 25分钟（标准番茄钟）
    data object TwentyFiveMinutes : FocusDurationOption

    // 45分钟
    data object FortyFiveMinutes : FocusDurationOption

    // 自定义时长
    data class Custom(val minutes: Int) : FocusDurationOption
}

/**
 * 今日任务 UI 模型
 * 包含任务 ID、标题、完成进度和完成状态
 */
data class FocusTaskUiModel(
    // 任务 ID
    val id: String,

    // 任务标题
    val title: String,

    // 已完成的番茄数
    val completedPomodoros: Int,

    // 预计需要的番茄数
    val estimatedPomodoros: Int,

    // 任务是否已完成
    val isCompleted: Boolean
)

/**
 * 专注页面完整 UI 状态
 * 包含页面所有显示数据和状态标志
 */
data class FocusUiState(
    // 当前正在进行的专注会话 ID
    val activeSessionId: String? = null,

    // 当前选中的任务 ID
    val selectedTaskId: String? = null,

    // 日期显示文本（如 "6月13日 星期六"）
    val dateText: String = "",

    // 用户选择的时长配置，默认为 25 分钟
    val selectedDuration: FocusDurationOption = FocusDurationOption.TwentyFiveMinutes,

    // 专注总时长（秒）
    val totalSeconds: Long = 25 * 60L,

    // 剩余时长（秒）
    val remainingSeconds: Long = 25 * 60L,

    // 计时器状态
    val timerStatus: FocusTimerStatus = FocusTimerStatus.IDLE,

    // 今日完成的番茄数
    val todayPomodoroCount: Int = 0,

    // 今日专注总分钟数
    val todayFocusMinutes: Int = 0,

    // 今日任务列表
    val todayTasks: List<FocusTaskUiModel> = emptyList(),

    // 自定义时长弹窗是否可见
    val isCustomDurationSheetVisible: Boolean = false,

    // 完成弹窗是否可见
    val showFinishedOverlay: Boolean = false,

    // 中断确认弹窗是否可见
    val showInterruptConfirmSheet: Boolean = false,

    // 错误提示消息
    val focusErrorMessage: String? = null
)

/**
 * 将 Domain [Task] 转换为 UI 模型 [FocusTaskUiModel]
 */
fun Task.toFocusTaskUiModel(): FocusTaskUiModel {
    return FocusTaskUiModel(
        id = id,
        title = title,
        completedPomodoros = completedPomodoros,
        estimatedPomodoros = estimatedPomodoros,
        isCompleted = isCompleted
    )
}