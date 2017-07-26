package com.wawelska.todo;

import com.wawelska.todo.model.*;
import com.wawelska.todo.repository.TaskRepository;
import com.wawelska.todo.repository.TeamRepository;
import com.wawelska.todo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ToDoService {

    static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public ToDoService(TaskRepository taskRepository, UserRepository userRepository, TeamRepository teamRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
    }


    public void addNewUser(User user) {
        userRepository.save(user);
    }


    public User getUserById(Long id) {
        return userRepository.findById(id);
    }


    public User getCurrentLoggedUser() {
        final UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByNickName(principal.getUsername());
    }


    public List<User> getAllUsersFromTeamByTeamId(Long teamId) {
        final Team team = teamRepository.findById(teamId);
        return userRepository.findByTeam(team);
    }


    public Map<Long, String> getAllUsersNickNameAndId(Team team) {
        return userRepository.findByTeam(team)
                .stream().collect(Collectors.toMap(User::getId, User::getNickName));
    }


    public List<User> getAllUsersInLevelOrderFromTeam(Team team) {
        return userRepository.getAllUsersInLevelOrderFromTeam(team);
    }


    public Team assignTeamById(Long id) {
        return teamRepository.findById(id);
    }


    public void saveTask(Task task) {
        taskRepository.save(task);
    }


    public Task getTaskById(Long id) {
        return taskRepository.findById(id);
    }


    public List<Task> getAllTasksBelongToTeam(Team team, Sorting sortingBy) {
        List<Task> listOfTasks;

        switch (sortingBy) {
            case ByPriority:
                listOfTasks = taskRepository.getAllTaskForTeamByPriority(team);
                break;
            case ByPoints:
                listOfTasks = taskRepository.getAllTaskForTeamByPoints(team);
                break;
            case ByDeadline:
                listOfTasks = taskRepository.getAllTaskForTeamByDeadline(team);
                break;
            case ByStatus:
                listOfTasks = taskRepository.getAllTaskForTeamByStatus(team);
                break;
            default:
                listOfTasks = taskRepository.getAllTaskForTeamByPriority(team);
                break;
        }
        return listOfTasks;
    }


    public void deleteTaskById(Long id) {
        taskRepository.deleteById(id);
    }


    public void updateTask(TaskFormDTO taskFormDTO, Long taskId) {
        Task editedTask = getTaskById(taskId);
        editedTask.setSpecification(taskFormDTO.getSpecification());
        if (taskFormDTO.getAssigneeId() == -1L) {
            editedTask.setAssignee(null);
        } else {
            editedTask.setAssignee(getUserById(taskFormDTO.getAssigneeId()));
        }
        editedTask.setPriority(taskFormDTO.getPriority());
        editedTask.setPoints(taskFormDTO.getPoints());

        try {
            editedTask.setDeadlineDate(DATE_FORMAT.parse(taskFormDTO.getDeadlineDate()));
            editedTask.setCreationDate(DATE_FORMAT.parse(taskFormDTO.getCreationDate()));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        editedTask.setStatus(taskFormDTO.getStatus());

        saveTask(editedTask);
        checkIfTaskCompletedAndUpdatePoints(editedTask);
    }

    public List<TaskFormDTO> getAllTasks() {
        return getAllTasksBelongToTeam(assignTeamById(getCurrentLoggedUser().getTeam().getId()),
                Sorting.ByPriority).stream()
                .map(this::getTaskFormDto)
                .collect(Collectors.toList());
    }


    public TaskFormDTO getTaskFormDto(Task editTask) {
        TaskFormDTO taskFormDTO = new TaskFormDTO();
        taskFormDTO.setId(editTask.getId());
        taskFormDTO.setSpecification(editTask.getSpecification());
        taskFormDTO.setPriority(editTask.getPriority());
        if (!Objects.isNull(editTask.getAssignee())) {
            taskFormDTO.setAssigneeId(editTask.getAssignee().getId());
        }
        taskFormDTO.setPoints(editTask.getPoints());
        taskFormDTO.setStatus(editTask.getStatus());
        taskFormDTO.setCreatorId(editTask.getCreator().getId());
        taskFormDTO.setDeadlineDate(DATE_FORMAT.format(editTask.getDeadlineDate()));
        taskFormDTO.setCreationDate(DATE_FORMAT.format(editTask.getCreationDate()));
        return taskFormDTO;
    }


    public Task convertToNewTask(TaskFormDTO taskFormDTO) {
        Task task = new Task();
        task.setSpecification(taskFormDTO.getSpecification());
        task.setCreator(this.getCurrentLoggedUser());
        task.setAssignee(this.getUserById(taskFormDTO.getAssigneeId()));
        task.setPriority(taskFormDTO.getPriority());
        task.setCreationDate(new Date());
        task.setPoints(taskFormDTO.getPoints());
        try {
            task.setDeadlineDate(DATE_FORMAT.parse(taskFormDTO.getDeadlineDate()));
        } catch (ParseException e) {
            throw new ToDoException("Unknown date format error!");
        }
        return task;
    }


    public void checkIfTaskCompletedAndUpdatePoints(Task task) {
        if (task.getStatus() == TaskStatus.Completed) {
            User userWhoCompletedTask = getUserById(task.getAssignee().getId());
            int newAmountOfPoints = userWhoCompletedTask.getLevel() + task.getPoints();
            userWhoCompletedTask.setLevel(newAmountOfPoints);
            addNewUser(userWhoCompletedTask);
        }
    }


    @PostConstruct
    public void init() {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        final Date today = new Date();

        Calendar todayInCalender = Calendar.getInstance();
        todayInCalender.setTime(today);
        todayInCalender.add(Calendar.DATE, 1);

        final Date tomorrow = todayInCalender.getTime();

        todayInCalender.add(Calendar.DATE, 7);
        final Date anotherDay = todayInCalender.getTime();

        todayInCalender.add(Calendar.DATE, -9);
        final Date yesterday = todayInCalender.getTime();


        final Team team = new Team();
        team.setTeamName("domownicy");
        teamRepository.save(team);

        final User user = new User();
        user.setEmail("alaMa@koty.pl");
        user.setNickName("Ala");
        user.setTeam(team);
        user.setPasswordHash(bCryptPasswordEncoder.encode("haslo"));
        user.setLevel(90);
        userRepository.save(user);

        final User user2 = new User();
        user2.setEmail("antonioMa@koty.pl");
        user2.setNickName("Antek");
        user2.setTeam(team);
        user2.setLevel(120);
        userRepository.save(user2);

        final Task task = new Task();
        task.setSpecification("umyc okna");
        task.setCreator(user);
        task.setAssignee(user2);
        task.setStatus(TaskStatus.In_Progress);
        task.setPoints(10);
        task.setCreationDate(yesterday);
        task.setDeadlineDate(anotherDay);
        task.setPriority(Priority.Low);
        saveTask(task);

        final Task task3 = new Task();
        task3.setSpecification("umyc toalete");
        task3.setCreator(user2);
        task3.setStatus(TaskStatus.New);
        task3.setPoints(5);
        task3.setCreationDate(today);
        task3.setDeadlineDate(tomorrow);
        task3.setPriority(Priority.High);
        saveTask(task3);

        final Task task4 = new Task();
        task4.setSpecification("wyniesc smieci");
        task4.setCreator(user2);
        task4.setAssignee(user);
        task4.setStatus(TaskStatus.Completed);
        task4.setPoints(2);
        task4.setCreationDate(today);
        task4.setDeadlineDate(today);
        task4.setPriority(Priority.Mid);
        saveTask(task4);
    }

}
