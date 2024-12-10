package model;

import java.util.List;

import model.exceptions.RepositoryException;

public interface IRepository {
    void addTask(Task task) throws RepositoryException;
    void removeTask(Task task) throws RepositoryException;
    void modifyTask(Task task) throws RepositoryException;
    List<Task> getAllTasks() throws RepositoryException;
}
