package model;

import java.util.List;

public interface IExporter {
    void export(List<Task> tasks, String filePath);
    List<Task> importTasks(String filePath);
}
