package com.wawelska.todo;

import com.wawelska.todo.model.Task;
import com.wawelska.todo.model.Team;
import com.wawelska.todo.model.User;
import com.wawelska.todo.repository.TaskRepository;
import com.wawelska.todo.repository.TeamRepository;
import com.wawelska.todo.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class ToDoServiceTest {

    private ToDoService toDoService;

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private TaskRepository taskRepositoryMock;
    @Mock
    private TeamRepository teamRepositoryMock;


    @Before
    public void setUp() throws Exception {
        toDoService = new ToDoService(taskRepositoryMock, userRepositoryMock, teamRepositoryMock);
    }

    @Test
    public void addNewUserTest() throws Exception {
        final User user = new User();
        toDoService.addNewUser(user);
        verify(userRepositoryMock).save(user);
    }

    @Test
    public void getUserByIdTest() throws Exception {
        final Long id = 1L;
        toDoService.getUserById(id);
        verify(userRepositoryMock).findById(id);
    }

    @Test
    public void getAllUsersFromTeamByTeamIdTest() throws Exception {
        final Long id = 1L;
        final Team team = new Team();
        Mockito.when(teamRepositoryMock.findById(id)).thenReturn(team);
        toDoService.getAllUsersFromTeamByTeamId(id);
        verify(userRepositoryMock).findByTeam(team);
    }

    @Test
    public void getAllUsersNickNameAndId() throws Exception {
        fail("toDO!");
    }

    @Test
    public void getAllUsersInLevelOrderFromTeamTest() throws Exception {
        final Team team = new Team();
        toDoService.getAllUsersInLevelOrderFromTeam(team);
        verify(userRepositoryMock).getAllUsersInLevelOrderFromTeam(team);
    }

    @Test
    public void assignTeamByIdTest() throws Exception {
        final Long id = 1L;
        toDoService.assignTeamById(id);
        verify(teamRepositoryMock).findById(id);
    }

    @Test
    public void addNewTaskTest() throws Exception {
        final Task task = new Task();
        toDoService.saveTask(task);
        verify(taskRepositoryMock).save(task);
    }

    @Test
    public void getTaskByIdTest() throws Exception {
        final Long id = 1L;
        toDoService.getTaskById(id);
        verify(taskRepositoryMock).findById(id);
    }

    @Test
    public void getallTasksBelongToTeamInPriorityOrderTest() throws Exception {
        Team team = new Team();
        Sorting byPriority = Sorting.ByPriority;
        toDoService.getAllTasksBelongToTeam(team,byPriority);
        verify(taskRepositoryMock).getAllTaskForTeamByPriority(team);
    }

    @Test
    public void getallTasksBelongToTeamInPointsOrder() throws Exception {
        Team team = new Team();
        Sorting byPoints = Sorting.ByPoints;
        toDoService.getAllTasksBelongToTeam(team,byPoints);
        verify(taskRepositoryMock).getAllTaskForTeamByPoints(team);
    }

    @Test
    public void getallTasksBelongToTeamInDeadlineOrder() throws Exception {
        Team team = new Team();
        Sorting byDeadline = Sorting.ByDeadline;
        toDoService.getAllTasksBelongToTeam(team,byDeadline);
        verify(taskRepositoryMock).getAllTaskForTeamByDeadline(team);
    }

    @Test
    public void deleteTaskById() throws Exception {
        Long id = 1L;
        toDoService.deleteTaskById(id);
        verify(taskRepositoryMock).deleteById(id);
    }
}
