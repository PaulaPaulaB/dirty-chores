package com.wawelska.todo;

import com.wawelska.todo.model.Task;
import com.wawelska.todo.model.TaskFormDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class ToDoRestControler {
    private final ToDoService toDoService;

    @Autowired
    public ToDoRestControler(ToDoService toDoService) {
        this.toDoService = toDoService;
    }


    @GetMapping("/tasks")
    public List<TaskFormDTO> getAllTasks() {
        return toDoService.getAllTasks();
    }


    @GetMapping("/tasks/{taskId}")
    public TaskFormDTO getTask(@PathVariable Long taskId) {
        return toDoService.getTaskFormDto(toDoService.getTaskById(taskId));
    }

    @DeleteMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Long taskId) {
        toDoService.deleteTaskById(taskId);
    }

    @PostMapping("/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTask(@RequestBody TaskFormDTO taskFormDTO) {
        Task task = toDoService.convertToNewTask(taskFormDTO);
        toDoService.saveTask(task);
    }

    @PutMapping("/tasks/{taskId}")
    @ResponseStatus(HttpStatus.OK)
    public void createTask(@RequestBody TaskFormDTO taskFormDTO, @PathVariable Long taskId) {
        Task task = toDoService.convertToNewTask(taskFormDTO);
        final Task foundTask = toDoService.getTaskById(taskId);
        foundTask.setPriority(task.getPriority());
        foundTask.setPoints(task.getPoints());
        toDoService.saveTask(foundTask);
    }


}
