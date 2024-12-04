package model;

import java.util.List;

public interface IRepository {
    void addTask(Task task);
    void removeTask(Task task);
    void modifyTask(Task task);
    List<Task> getAllTasks();
}
