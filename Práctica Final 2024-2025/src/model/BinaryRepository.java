package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BinaryRepository implements IRepository {
    private static final String FILE_PATH = System.getProperty("user.home") + "/tasks.bin";
    private List<Task> tasks = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public void modifyTask(Task task) {
        // ¿?
    }

    @Override
    public List<Task> getAllTasks() {
        return tasks;
    }
}
