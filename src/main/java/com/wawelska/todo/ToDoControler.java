package com.wawelska.todo;

import com.wawelska.todo.model.Task;
import com.wawelska.todo.model.TaskFormDTO;
import com.wawelska.todo.model.User;
import com.wawelska.todo.model.UserFormDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;


@Controller
@Slf4j
public class ToDoControler {

    private final ToDoService toDoService;

    @Autowired
    public ToDoControler(ToDoService toDoService) {
        this.toDoService = toDoService;
    }


    @ExceptionHandler(value = ToDoException.class)
    public String handleException(ToDoException e, Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @RequestMapping(value = "main")
    public String main() {
        return "view-main";
    }

    @RequestMapping(value = "/myProfile")
    public String checkMyProfile(Model model) {
        User user = toDoService.getCurrentLoggedUser();
        model.addAttribute("userObject", user);

        List<User> rankingOfUsers = toDoService.getAllUsersInLevelOrderFromTeam(toDoService.getCurrentLoggedUser().getTeam());
        model.addAttribute("ranking", rankingOfUsers);

        return "/view-myProfile";
    }

    @RequestMapping(value = "editMyProfile", method = RequestMethod.GET)
    public String editMyProfile(Model model) {
        UserFormDTO user = new UserFormDTO();

        user.setNickName(toDoService.getCurrentLoggedUser().getNickName());
        user.setEmail(toDoService.getCurrentLoggedUser().getEmail());

        model.addAttribute("user", user);
        return "/view-editProfile";
    }

    @RequestMapping(value = "editMyProfile", method = RequestMethod.POST)
    public String saveChangesInMyProfile(
            @ModelAttribute("user") @Valid UserFormDTO userFormDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "/view-editProfile";
        } else {
            User newUser = new User();
            newUser.setNickName(userFormDTO.getNickName());
            newUser.setEmail(userFormDTO.getEmail());
        }
        return "/view-myProfile";
    }

    @RequestMapping(value = "/newTask", method = RequestMethod.GET)
    public String addNewTask(Model model) {
        TaskFormDTO taskFormDTO = new TaskFormDTO();
        Long teamId = toDoService.getCurrentLoggedUser().getTeam().getId();
        model.addAttribute("taskObject", taskFormDTO);
        model.addAttribute("userOptions", toDoService.getAllUsersFromTeamByTeamId(teamId));
        return "/view-newTask";
    }

    @RequestMapping(value = "/newTask", method = RequestMethod.POST)
    public String saveNewTask(@ModelAttribute("taskObject") @Valid TaskFormDTO taskFormDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            return "/view-newTask";
        } else {
            Task task = toDoService.convertToNewTask(taskFormDTO);
            toDoService.saveTask(task);

            toDoService.checkIfTaskCompletedAndUpdatePoints(task);
        }

        return "redirect:/allTasks";
    }

    @RequestMapping(value = "/allTasks")
    public String displayAllTaskForTeam(@RequestParam(value = "sortBy", defaultValue = "ByStatus", required = false) String sortingBy, Model model) {
        Long teamId = toDoService.getCurrentLoggedUser().getTeam().getId();
        List<Task> listOfTasks = toDoService.getAllTasksBelongToTeam(toDoService.assignTeamById(teamId), Sorting.valueOf(sortingBy));
        model.addAttribute("listOfTasks", listOfTasks);

        return "/view-allTasks";
    }

    @RequestMapping(value = "/task/takeIt/{taskId}")
    public String assigmTaskToCurrentUser(@PathVariable Long taskId){
        final Task editTask = toDoService.getTaskById(taskId);
        editTask.setAssignee(toDoService.getCurrentLoggedUser());
        toDoService.saveTask(editTask);

        return "redirect:/allTasks";
    }

    @RequestMapping(value = "/task/edit/{taskId}", method = RequestMethod.GET)
    public String editTask(@PathVariable Long taskId, Model model) {
        TaskFormDTO taskFormDTO = toDoService.getTaskFormDto(toDoService.getTaskById(taskId));
        Long teamId = toDoService.getCurrentLoggedUser().getTeam().getId();
        model.addAttribute("taskObject", taskFormDTO);
        model.addAttribute("userOptions", toDoService.getAllUsersFromTeamByTeamId(teamId));

        return "view-editTask";
    }

    @RequestMapping(value = "/task/edit/{taskId}", method = RequestMethod.POST)
    public String saveChangesInTask(
            @PathVariable Long taskId,
            @Valid TaskFormDTO taskFormDTO,
            Model model,
            BindingResult result
    ) {
        Long teamId = toDoService.getCurrentLoggedUser().getTeam().getId();
        if (result.hasErrors()) {
            model.addAttribute("taskObject", taskFormDTO);
            model.addAttribute("userOptions", toDoService.getAllUsersFromTeamByTeamId(teamId));
            return "view-editTask";
        } else {
            toDoService.updateTask(taskFormDTO, taskId);
        }
        return "redirect:/allTasks";
    }

    @RequestMapping(value = "/task/delete/{taskId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTask(@PathVariable Long taskId) {
        toDoService.deleteTaskById(taskId);
    }
}
