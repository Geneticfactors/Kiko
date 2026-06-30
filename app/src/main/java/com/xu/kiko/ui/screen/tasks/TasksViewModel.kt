package com.xu.kiko.ui.screen.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.domain.model.Task
import com.xu.kiko.domain.model.TaskCategory
import com.xu.kiko.domain.repository.TaskRepository
import com.xu.kiko.domain.usecase.task.TaskInput
import com.xu.kiko.domain.usecase.task.TaskValidationRules
import com.xu.kiko.domain.usecase.task.ValidateTaskUseCase
import java.util.Calendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 任务页面 ViewModel
 * 负责管理任务列表、筛选、编辑和删除逻辑
 */
class TasksViewModel(
    // 任务仓库
    private val taskRepository: TaskRepository,

    // 任务验证用例
    private val validateTask: ValidateTaskUseCase
) : ViewModel() {

    // 当前选中的筛选条件
    private val selectedFilter =
        MutableStateFlow(TaskFilter.ALL)

    // 内部 UI 状态流
    private val _uiState =
        MutableStateFlow(TasksUiState(isLoading = true))

    // 暴露给 UI 层的只读状态流
    val uiState: StateFlow<TasksUiState> =
        _uiState.asStateFlow()

    // 内部副作用通道
    private val _effects =
        Channel<TasksUiEffect>(Channel.BUFFERED)

    // 暴露给 UI 层的副作用流
    val effects: Flow<TasksUiEffect> =
        _effects.receiveAsFlow()

    // 观察任务列表的协程任务
    private var observeTasksJob: Job? = null

    /**
     * ViewModel 初始化
     * 启动任务列表观察
     */
    init {
        observeTasks()
    }

    /**
     * 处理用户操作
     * 根据 [TasksUiAction] 分发到对应的处理方法
     *
     * @param action 用户操作意图
     */
    fun onAction(action: TasksUiAction) {
        when (action) {
            is TasksUiAction.SelectFilter ->
                selectFilter(action.filter)

            TasksUiAction.OpenCreateTask ->
                openCreateTask()

            is TasksUiAction.OpenEditTask ->
                openEditTask(action.taskId)

            TasksUiAction.CloseEditor ->
                closeEditor()

            is TasksUiAction.EditorAction ->
                onEditorAction(action.action)

            is TasksUiAction.SetTaskCompleted ->
                setTaskCompleted(
                    taskId = action.taskId,
                    completed = action.completed
                )

            is TasksUiAction.RequestDeleteTask ->
                requestDeleteTask(action.taskId)

            TasksUiAction.CancelDeleteTask ->
                cancelDeleteTask()

            TasksUiAction.ConfirmDeleteTask ->
                confirmDeleteTask()

            TasksUiAction.Retry ->
                observeTasks()

            TasksUiAction.DismissMessage ->
                Unit
        }
    }

    /**
     * 观察任务列表
     * 监听任务变化和筛选条件变化，实时更新 UI 状态
     */
    private fun observeTasks() {
        observeTasksJob?.cancel()
        observeTasksJob = viewModelScope.launch {
            combine(
                taskRepository.observeTasks(),
                selectedFilter
            ) { tasks, filter ->
                filter to tasks
            }
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            errorMessage = TASKS_LOAD_ERROR
                        )
                    }
                }
                .collect { (filter, tasks) ->
                    val filteredTasks = tasks.filterBy(filter)

                    _uiState.update { state ->
                        state.copy(
                            selectedFilter = filter,
                            sections = filteredTasks.toSections(),
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    /**
     * 选择筛选条件
     * 更新筛选状态并触发任务列表重新加载
     *
     * @param filter 筛选条件
     */
    private fun selectFilter(filter: TaskFilter) {
        selectedFilter.value = filter
        _uiState.update { state ->
            state.copy(selectedFilter = filter)
        }
    }

    /**
     * 打开创建任务编辑器
     * 创建新的空白编辑器状态
     */
    private fun openCreateTask() {
        _uiState.update { state ->
            state.copy(
                editor = TaskEditorUiState(),
                pendingDeleteTask = null
            )
        }
    }

    /**
     * 打开编辑任务编辑器
     * 根据任务 ID 加载任务数据到编辑器
     *
     * @param taskId 任务 ID
     */
    private fun openEditTask(taskId: String) {
        val task = findTask(taskId) ?: return

        _uiState.update { state ->
            state.copy(
                editor = TaskEditorUiState(
                    mode = TaskEditorMode.EDIT,
                    editingTaskId = task.id,
                    title = task.title,
                    note = task.note.orEmpty(),
                    category = task.category,
                    estimatedPomodoros = task.estimatedPomodoros
                ),
                pendingDeleteTask = null
            )
        }
    }

    /**
     * 关闭任务编辑器
     * 保存操作进行中时不允许关闭
     */
    private fun closeEditor() {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.update { state ->
            state.copy(editor = null)
        }
    }

    /**
     * 处理编辑器操作
     * 根据 [TaskEditorUiAction] 更新编辑器状态
     *
     * @param action 编辑器操作意图
     */
    private fun onEditorAction(action: TaskEditorUiAction) {
        when (action) {
            is TaskEditorUiAction.TitleChanged ->
                updateEditor { editor ->
                    editor.copy(
                        title = action.value,
                        titleError = null
                    )
                }

            is TaskEditorUiAction.NoteChanged ->
                updateEditor { editor ->
                    editor.copy(
                        note = action.value,
                        noteError = null
                    )
                }

            is TaskEditorUiAction.CategoryChanged ->
                updateEditor { editor ->
                    editor.copy(category = action.category)
                }

            TaskEditorUiAction.DecreasePomodoros ->
                updateEditor { editor ->
                    editor.copy(
                        estimatedPomodoros =
                            (editor.estimatedPomodoros - 1)
                                .coerceAtLeast(
                                    TaskValidationRules
                                        .MIN_ESTIMATED_POMODOROS
                                ),
                        pomodoroError = null
                    )
                }

            TaskEditorUiAction.IncreasePomodoros ->
                updateEditor { editor ->
                    editor.copy(
                        estimatedPomodoros =
                            (editor.estimatedPomodoros + 1)
                                .coerceAtMost(
                                    TaskValidationRules
                                        .MAX_ESTIMATED_POMODOROS
                                ),
                        pomodoroError = null
                    )
                }

            TaskEditorUiAction.Save ->
                saveTask()
        }
    }

    /**
     * 更新编辑器状态
     * 通过 transform 函数修改当前编辑器状态
     *
     * @param transform 状态转换函数
     */
    private fun updateEditor(
        transform: (TaskEditorUiState) -> TaskEditorUiState
    ) {
        _uiState.update { state ->
            state.copy(
                editor = state.editor?.let(transform)
            )
        }
    }

    /**
     * 保存任务
     * 先验证输入，验证通过后保存到仓库
     */
    private fun saveTask() {
        val editor = _uiState.value.editor ?: return
        val input = TaskInput(
            title = editor.title.trim(),
            note = editor.note.trim(),
            category = editor.category,
            estimatedPomodoros = editor.estimatedPomodoros
        )
        val validation = validateTask(input)

        _uiState.update { state ->
            state.copy(
                editor = state.editor?.copy(
                    titleError = validation.titleError,
                    noteError = validation.noteError,
                    pomodoroError = validation.pomodoroError
                )
            )
        }

        if (!validation.isValid) {
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isSaving = true)
            }

            runCatching {
                taskRepository.saveTask(
                    taskId = editor.editingTaskId,
                    input = input
                )
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(editor = null)
                }
                _effects.trySend(
                    TasksUiEffect.ShowMessage(TASK_SAVED)
                )
            }.onFailure {
                _effects.trySend(
                    TasksUiEffect.ShowMessage(TASK_OPERATION_FAILED)
                )
            }

            _uiState.update { state ->
                state.copy(isSaving = false)
            }
        }
    }

    /**
     * 设置任务完成状态
     *
     * @param taskId 任务 ID
     * @param completed 是否完成
     */
    private fun setTaskCompleted(
        taskId: String,
        completed: Boolean
    ) {
        viewModelScope.launch {
            runCatching {
                taskRepository.setTaskCompleted(
                    taskId = taskId,
                    completed = completed
                )
            }.onFailure {
                _effects.trySend(
                    TasksUiEffect.ShowMessage(TASK_OPERATION_FAILED)
                )
            }
        }
    }

    /**
     * 请求删除任务
     * 查找任务并设置为待删除状态，显示确认对话框
     *
     * @param taskId 任务 ID
     */
    private fun requestDeleteTask(taskId: String) {
        val task = findTask(taskId) ?: return

        _uiState.update { state ->
            state.copy(pendingDeleteTask = task)
        }
    }

    /**
     * 取消删除任务
     * 删除操作进行中时不允许取消
     */
    private fun cancelDeleteTask() {
        if (_uiState.value.isDeleting) {
            return
        }

        _uiState.update { state ->
            state.copy(pendingDeleteTask = null)
        }
    }

    /**
     * 确认删除任务
     * 从仓库删除任务并更新状态
     */
    private fun confirmDeleteTask() {
        val task = _uiState.value.pendingDeleteTask ?: return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isDeleting = true)
            }

            runCatching {
                taskRepository.deleteTask(task.id)
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(pendingDeleteTask = null)
                }
                _effects.trySend(
                    TasksUiEffect.ShowMessage(TASK_DELETED)
                )
            }.onFailure {
                _effects.trySend(
                    TasksUiEffect.ShowMessage(TASK_OPERATION_FAILED)
                )
            }

            _uiState.update { state ->
                state.copy(isDeleting = false)
            }
        }
    }

    /**
     * 根据任务 ID 查找任务
     * 在当前任务列表中搜索匹配的任务
     *
     * @param taskId 任务 ID
     * @return 任务 UI 模型（找不到返回 null）
     */
    private fun findTask(taskId: String): TaskUiModel? {
        return _uiState.value.sections
            .asSequence()
            .flatMap { section -> section.tasks.asSequence() }
            .firstOrNull { task -> task.id == taskId }
    }

    /**
     * 按筛选条件过滤任务列表
     *
     * @param filter 筛选条件
     * @return 过滤后的任务列表
     */
    private fun List<Task>.filterBy(filter: TaskFilter): List<Task> {
        return when (filter) {
            TaskFilter.ALL -> this
            TaskFilter.STUDY ->
                filter { task -> task.category == TaskCategory.STUDY }

            TaskFilter.WORK ->
                filter { task -> task.category == TaskCategory.WORK }

            TaskFilter.READING ->
                filter { task -> task.category == TaskCategory.READING }
        }
    }

    /**
     * 将任务列表转换为分组 UI 模型
     * 按日期分组（今日/昨日/更早）
     *
     * @return 任务分组列表
     */
    private fun List<Task>.toSections(): List<TaskSectionUiModel> {
        return map { task -> task.toUiModel() }
            .groupBy { task -> task.dateSection }
            .let { grouped ->
                listOf(
                    TaskDateSection.TODAY,
                    TaskDateSection.YESTERDAY,
                    TaskDateSection.EARLIER
                ).mapNotNull { section ->
                    val tasks = grouped[section].orEmpty()
                    if (tasks.isEmpty()) {
                        null
                    } else {
                        TaskSectionUiModel(
                            section = section,
                            tasks = tasks
                        )
                    }
                }
            }
    }

    /**
     * 将 Domain [Task] 转换为 UI 模型 [TaskUiModel]
     */
    private fun Task.toUiModel(): TaskUiModel {
        return TaskUiModel(
            id = id,
            title = title,
            note = note,
            category = category,
            estimatedPomodoros = estimatedPomodoros,
            completedPomodoros = completedPomodoros,
            isCompleted = isCompleted,
            dateSection =
                createdAtEpochMillis.toTaskDateSection()
        )
    }

    /**
     * 将时间戳转换为日期分组
     *
     * @return 日期分组
     */
    private fun Long.toTaskDateSection(): TaskDateSection {
        val taskDay = dayOfYear(this)
        val today = dayOfYear(System.currentTimeMillis())

        return when (today - taskDay) {
            0 -> TaskDateSection.TODAY
            1 -> TaskDateSection.YESTERDAY
            else -> TaskDateSection.EARLIER
        }
    }

    /**
     * 计算时间戳对应的年内天数（含年份，避免跨年问题）
     *
     * @param epochMillis 时间戳
     * @return 年内天数（含年份）
     */
    private fun dayOfYear(epochMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = epochMillis
        return calendar.get(Calendar.YEAR) * DAYS_PER_YEAR +
            calendar.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 常量定义
     */
    private companion object {
        // 一年内最大天数（用于计算 dayOfYear）
        const val DAYS_PER_YEAR = 366

        // 任务加载失败提示消息
        const val TASKS_LOAD_ERROR = "任务加载失败"

        // 任务保存成功提示消息
        const val TASK_SAVED = "任务已保存"

        // 任务删除成功提示消息
        const val TASK_DELETED = "任务已删除"

        // 通用操作失败提示消息
        const val TASK_OPERATION_FAILED = "操作失败，请重试"
    }
}
