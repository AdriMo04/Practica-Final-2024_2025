package model;

import java.util.List;

import model.exceptions.ExporterException;
import model.exceptions.RepositoryException;

public class Model {
    private final IRepository repository;
    private IExporter exporter;
    
    public Model(IRepository repository) {
        this.repository = repository;
    }

    public void setExporter(IExporter exporter) {
        this.exporter = exporter;
    }

    public void addTask(Task task) throws RepositoryException {
        repository.addTask(task);
    }

    public void removeTask(Task task) throws RepositoryException {
        repository.removeTask(task);
    }

    public void modifyTask(Task task) throws RepositoryException {
        repository.modifyTask(task);
    }

    public List<Task> getAllTasks() throws RepositoryException {
        return repository.getAllTasks();
    }

    public void exportTasks(List<Task> tasks, String filePath) throws ExporterException {
        if (exporter == null) {
            throw new ExporterException("No se ha configurado un exportador.");
        }

        exporter.exportTasks(tasks, filePath);
    }

    public List<Task> importTasks() throws ExporterException {
        if (exporter == null) {
            throw new ExporterException("No se ha configurado un exportador.");
        }

        return exporter.importTasks(null);
    }
}