package com.deveshsharma.deveshsharma.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.deveshsharma.deveshsharma.data.model.Task
import com.deveshsharma.deveshsharma.ui.viewmodel.TaskViewModel

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onTaskAdded: (Task) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Task") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
            }
        },
        confirmButton = {
            Button(onClick = {
                onTaskAdded(
                    Task(
                        title = title,
                        description = description,
                        status = "Pending"
                    )
                )
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditTaskDialog(task: Task, onDismiss: () -> Unit, onTaskUpdated: (Task) -> Unit) {
    var title by remember { mutableStateOf(task.title) }
    var description by remember { mutableStateOf(task.description) }
    val statuses = listOf("Pending", "In Progress", "Completed")
    var status by remember { mutableStateOf(task.status) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                statuses.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == status),
                                onClick = { status = text }
                            )
                            .padding(horizontal = 16.dp)

                    ) {
                        Box( modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart){
                            RadioButton(
                                selected = (text == status),
                                onClick = { status = text }
                            )
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 45.dp)
                            )
                        }

                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onTaskUpdated(task.copy(title = title, description = description, status = status))
            }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TaskItem(task: Task, onTaskUpdated: (Task) -> Unit, onTaskDeleted: () -> Unit) {
    val color = when (task.status) {
        "Pending" -> Color.Yellow
        "In Progress" -> Color.Cyan
        "Completed" -> Color.Green
        else -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when (task.status) {
        "Pending", "In Progress", "Completed" -> Color.Black
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = color,
            contentColor = contentColor
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = task.title, style = MaterialTheme.typography.titleMedium)
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = { onTaskUpdated(task) }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit Task")
            }
            IconButton(onClick = onTaskDeleted) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete Task")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.allTasks.collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Task?>(null) }
    // Default to true to show the checkbox as checked initially
    var showCompleted by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    IconButton(onClick = { showAddDialog = true }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Task")
                    }
                }
            )
        }
    ) { padding ->
        // Use a Column to arrange UI elements vertically
        Column(modifier = Modifier.padding(padding)) {
            // Row for the "Show Completed" Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = showCompleted,
                    onCheckedChange = { showCompleted = it }
                )
                Text(
                    text = "Show Completed Tasks",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tasks to display")
                }
            } else {
                // 1. Filter the list based on the checkbox state.
                val filteredTasks = if (showCompleted) {
                    tasks
                } else {
                    tasks.filter { it.status != "Completed" }
                }

                // 2. Define the desired order for sorting.
                val statusOrder = mapOf("In Progress" to 1, "Pending" to 2, "Completed" to 3)

                // 3. Sort the filtered list.
                val sortedTasks = filteredTasks.sortedBy { statusOrder[it.status] ?: 4 }

                // 4. Display the final list in a SINGLE LazyColumn.
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(sortedTasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onTaskUpdated = { showEditDialog = it },
                            onTaskDeleted = { taskViewModel.delete(task) }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                onDismiss = { showAddDialog = false },
                onTaskAdded = { task ->
                    taskViewModel.insert(task)
                    showAddDialog = false
                }
            )
        }

        showEditDialog?.let { task ->
            EditTaskDialog(
                task = task,
                onDismiss = { showEditDialog = null },
                onTaskUpdated = { updatedTask ->
                    taskViewModel.update(updatedTask)
                    showEditDialog = null
                }
            )
        }
    }
}

