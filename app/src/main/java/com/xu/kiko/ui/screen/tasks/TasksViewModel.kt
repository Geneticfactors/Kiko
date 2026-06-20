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

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val validateTask: ValidateTaskUseCase
) : ViewModel() {

    private val selectedFilter =
        MutableStateFlow(TaskFilter.ALL)

    private val _uiState =
        MutableStateFlow(TasksUiState(isLoading = true))
    val uiState: StateFlow<TasksUiState> =
        _uiState.asStateFlow()

    private val _effects =
        Channel<TasksUiEffect>(Channel.BUFFERED)
    val effects: Flow<TasksUiEffect> =
        _effects.receiveAsFlow()

    private var observeTasksJob: Job? = null

    init {
        observeTasks()
    }

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

    private fun selectFilter(filter: TaskFilter) {
        selectedFilter.value = filter
        _uiState.update { state ->
            state.copy(selectedFilter = filter)
        }
    }

    private fun openCreateTask() {
        _uiState.update { state ->
            state.copy(
                editor = TaskEditorUiState(),
                pendingDeleteTask = null
            )
        }
    }

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

    private fun closeEditor() {
        if (_uiState.value.isSaving) {
            return
        }

        _uiState.update { state ->
            state.copy(editor = null)
        }
    }

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

    private fun updateEditor(
        transform: (TaskEditorUiState) -> TaskEditorUiState
    ) {
        _uiState.update { state ->
            state.copy(
                editor = state.editor?.let(transform)
            )
        }
    }

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

    private fun requestDeleteTask(taskId: String) {
        val task = findTask(taskId) ?: return

        _uiState.update { state ->
            state.copy(pendingDeleteTask = task)
        }
    }

    private fun cancelDeleteTask() {
        if (_uiState.value.isDeleting) {
            return
        }

        _uiState.update { state ->
            state.copy(pendingDeleteTask = null)
        }
    }

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

    private fun findTask(taskId: String): TaskUiModel? {
        return _uiState.value.sections
            .asSequence()
            .flatMap { section -> section.tasks.asSequence() }
            .firstOrNull { task -> task.id == taskId }
    }

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

    private fun Long.toTaskDateSection(): TaskDateSection {
        val taskDay = dayOfYear(this)
        val today = dayOfYear(System.currentTimeMillis())

        return when (today - taskDay) {
            0 -> TaskDateSection.TODAY
            1 -> TaskDateSection.YESTERDAY
            else -> TaskDateSection.EARLIER
        }
    }

    private fun dayOfYear(epochMillis: Long): Int {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = epochMillis
        return calendar.get(Calendar.YEAR) * DAYS_PER_YEAR +
            calendar.get(Calendar.DAY_OF_YEAR)
    }

    private companion object {
        const val DAYS_PER_YEAR = 366
        const val TASKS_LOAD_ERROR = "任务加载失败"
        const val TASK_SAVED = "任务已保存"
        const val TASK_DELETED = "任务已删除"
        const val TASK_OPERATION_FAILED = "操作失败，请重试"
    }
}
