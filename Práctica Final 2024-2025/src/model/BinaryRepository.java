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
        save();
    }

    @Override
    public void removeTask(Task task) {
        tasks.remove(task);
        save();
    }

    @Override
    public void modifyTask(Task task) {
        int index = tasks.indexOf(task);
        if (index != -1) {
            tasks.set(index, task);
            save();
        }
    }

    @Override
    public List<Task> getAllTasks() {
        load();
        return tasks;
    }

    public void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            tasks = (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            tasks = new ArrayList<>();
        }
    }
}
