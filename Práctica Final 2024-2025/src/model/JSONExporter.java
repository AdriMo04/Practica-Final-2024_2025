package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

import model.exceptions.ExporterException;

public class JSONExporter implements IExporter{
    private final Gson gson;

    public JSONExporter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void export(List<Task> tasks, String filePath) throws ExporterException {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            throw new ExporterException("Error exportando tareas a JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Task> importTasks(String filePath) throws ExporterException {
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<Task>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (IOException e) {
            throw new ExporterException("Error importando tareas desde JSON: " + e.getMessage(), e);
        }
    }

}
