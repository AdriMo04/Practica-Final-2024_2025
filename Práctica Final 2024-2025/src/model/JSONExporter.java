package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.exceptions.ExporterException;

public class JSONExporter implements IExporter{
    private final Gson gson;

    public JSONExporter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void exportTasks(List<Task> tasks, String filePath) throws ExporterException {
        if (tasks == null || tasks.isEmpty()) {
            throw new ExporterException("No hay tareas para exportar.");
        }

        if (filePath == null || filePath.isEmpty()) {
            throw new ExporterException("La ruta del archivo no es válida.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            throw new ExporterException("Error exportando tareas a JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> importTasks(String filePath) throws ExporterException {
        if (filePath == null || filePath.isEmpty()) {
            throw new ExporterException("La ruta del archivo no es válida.");
        }
        
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Task>>() {}.getType();
            tasks = gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new ExporterException("Error importando tareas desde JSON: " + e.getMessage(), e);
        }

        return tasks;
    }
}
