package com.xu.kiko.ui.screen.tasks

import com.xu.kiko.domain.model.TaskCategory

/**
 * 任务筛选类型
 * 用于按分类筛选任务列表
 */
enum class TaskFilter {
    // 显示所有任务
    ALL,

    // 仅显示学习类任务
    STUDY,

    // 仅显示工作类任务
    WORK,

    // 仅显示阅读类任务
    READING
}

/**
 * 任务日期分组
 * 用于将任务按时间分组显示
 */
enum class TaskDateSection {
    // 今日任务
    TODAY,

    // 昨日任务
    YESTERDAY,

    // 更早的任务
    EARLIER
}

/**
 * 任务 UI 模型
 * 任务列表中展示的任务数据
 */
data class TaskUiModel(
    // 任务 ID
    val id: String,

    // 任务标题
    val title: String,

    // 任务备注
    val note: String?,

    // 任务分类
    val category: TaskCategory,

    // 预计需要的番茄数
    val estimatedPomodoros: Int,

    // 已完成的番茄数
    val completedPomodoros: Int,

    // 任务是否已完成
    val isCompleted: Boolean,

    // 任务所属日期分组
    val dateSection: TaskDateSection
)

/**
 * 任务分组 UI 模型
 * 包含一个日期分组及其下的任务列表
 */
data class TaskSectionUiModel(
    // 日期分组
    val section: TaskDateSection,

    // 该分组下的任务列表
    val tasks: List<TaskUiModel>
)

/**
 * 任务页面完整 UI 状态
 * 包含页面所有显示数据和状态标志
 */
data class TasksUiState(
    // 当前选中的筛选条件
    val selectedFilter: TaskFilter = TaskFilter.ALL,

    // 任务分组列表
    val sections: List<TaskSectionUiModel> = emptyList(),

    // 是否正在加载任务
    val isLoading: Boolean = false,

    // 错误提示消息
    val errorMessage: String? = null,

    // 任务编辑器状态（为空时隐藏编辑器）
    val editor: TaskEditorUiState? = null,

    // 待删除的任务（为空时隐藏删除确认对话框）
    val pendingDeleteTask: TaskUiModel? = null,

    // 是否正在保存任务
    val isSaving: Boolean = false,

    // 是否正在删除任务
    val isDeleting: Boolean = false
)