package controller;

import java.util.List;

import model.Model;
import model.Task;
import model.exceptions.ExporterException;
import model.exceptions.RepositoryException;
import view.BaseView;

public class Controller {
    private final Model model;
    private final BaseView view;

    public Controller(Model model, BaseView view) {
        this.model = model;
        this.view = view;
    }

    public void start() {
        view.init();
    }

    public void addTask(Task task) throws RepositoryException {
        model.addTask(task);
    }

    public void deleteTask(Task task) throws RepositoryException {
        model.removeTask(task);
    }

    public void updateTask(Task task) throws RepositoryException {
        model.modifyTask(task);
    }

    public List<Task> getAllTasks() throws RepositoryException {
        return model.getAllTasks();
    }

    public List<Task> getPendingTasks() throws RepositoryException {
        return model.getAllTasks().stream()
                .filter(task -> !task.isCompleted())
                .sorted((t1, t2) -> Integer.compare(t2.getPriority(), t1.getPriority()))
                .toList();
    }

    public Task getTaskById(int taskId) throws RepositoryException {
        return model.getAllTasks().stream()
                .filter(task -> task.getIdentifier() == taskId)
                .findFirst()
                .orElse(null);
    }

    public void exportTasks(String format, String filePath) throws ExporterException, RepositoryException {
        model.setExporter(getExporter(format));
        List<Task> tasks = model.getAllTasks();
        model.exportTasks(tasks, filePath);
    }

    private model.IExporter getExporter(String format) throws ExporterException {
        return switch (format.toLowerCase()) {
            case "json" -> new model.JSONExporter();
            case "csv" -> new model.CSVExporter();
            default -> throw new ExporterException("Formato de exportación no soportado: " + format);
        };
    }

    public void importTasks(String format, String filePath) throws ExporterException {
        model.setExporter(getExporter(format));
        List<Task> tasks = model.importTasks();

        for (Task task : tasks) {
            try {
                model.addTask(task);
            } catch (RepositoryException e) {
                throw new ExporterException("Error al agregar tarea importada: " + e.getMessage(), e);
            }
        }
    }

    public void end() {
        view.end();
    }
}
