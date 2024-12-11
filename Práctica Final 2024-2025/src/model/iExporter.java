package model;

import java.util.List;

import model.exceptions.ExporterException;

public interface IExporter {
    void exportTasks(List<Task> tasks, String filePath) throws ExporterException;
    List<Task> importTasks(String filePath) throws ExporterException;
}
