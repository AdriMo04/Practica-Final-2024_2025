package model;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import model.exceptions.ExporterException;

public class CSVExporter implements IExporter {
    private static final String delimitador = ",";

    @Override
    public void exportTasks(List<Task> tasks, String filePath) throws ExporterException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                writer.write(String.format("%d%s%s%s%s%s%d%s%d%s%b", 
                    task.getIdentifier(), delimitador,
                    escapeCSV(task.getTitle()), delimitador,
                    task.getDate().getTime(), delimitador,
                    escapeCSV(task.getContent()), delimitador,
                    task.getPriority(), delimitador,
                    task.getEstimatedDuration(), delimitador,
                    task.isCompleted()));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ExporterException("Error exportando tareas a CSV", e);
        }
    }

    @Override
    public List<Task> importTasks(String filePath) throws ExporterException {
        List<Task> tasks = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String linea = reader.readLine();
            while ((linea = reader.readLine()) != null) {
                String[] fields = linea.split(delimitador);
                Task task = new Task(
                    Integer.parseInt(fields[0]),
                    unescapeCSV(fields[1]),
                    new java.util.Date(Long.parseLong(fields[2])),
                    unescapeCSV(fields[3]),
                    Integer.parseInt(fields[4]),
                    Integer.parseInt(fields[5]),
                    Boolean.parseBoolean(fields[5])
                );

                tasks.add(task);
            }
        } catch (IOException | NumberFormatException e) {
            throw new ExporterException("Error importando tareas desde CSV", e);
        }

        return tasks;
    }

    private String escapeCSV(String input) {
        if (input.contains(",") || input.contains("\"") || input.contains("\n")) {
            input = input.replace("\"", "\"\"");
            return "\"" + input + "\"";
        }

        return input;
    }

    private String unescapeCSV(String input) {
        if (input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1).replace("\"\"", "\"");
        }

        return input;
    }
}
