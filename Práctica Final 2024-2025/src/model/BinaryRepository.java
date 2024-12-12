package model;

import java.io.*;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.coti.tools.Rutas;

import model.exceptions.RepositoryException;

public class BinaryRepository implements IRepository {
    // private static final String filePath = System.getProperty("user.home") + "/tasks.bin";
    Path rutaFichero = Rutas.pathToFileOnDesktop("tasks.bin");
    File fichero = rutaFichero.toFile();
    private List<Task> tasks = new ArrayList<>();

    @Override
    public void addTask(Task task) throws RepositoryException {
        tasks.add(task);
        save();
    }

    @Override
    public void removeTask(Task task) throws RepositoryException {
        tasks.remove(task);
        save();
    }

    @Override
    public void modifyTask(Task task) throws RepositoryException {
        int index = tasks.indexOf(task);
        if (index != -1) {
            tasks.set(index, task);
            save();
        } else {
            throw new RepositoryException("Tarea nno encontrada");
        }
    }

    @Override
    public List<Task> getAllTasks() throws RepositoryException {
        load();
        return tasks;
    }

    public void save() throws RepositoryException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichero))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            throw new RepositoryException("Error al guardar.", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void load() throws RepositoryException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichero))) {
            tasks = (List<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            tasks = new ArrayList<>();
        }
    }
}
