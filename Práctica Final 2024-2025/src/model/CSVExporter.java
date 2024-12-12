package model;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import model.exceptions.ExporterException;

public class CSVExporter implements IExporter {
    private static final String delimitador = ",";

    @Override
    public void exportTasks(List<Task> tasks, String filePath) throws ExporterException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Task task : tasks) {
                writer.write(String.format("%s%s%s%s%s%s%s%s%s%s%s%s%s", 
                    String.valueOf(task.getIdentifier()), delimitador,  // Convertimos el ID a String
                    escapeCSV(task.getTitle()), delimitador,            // Título
                    Long.toString(task.getDate().getTime()), delimitador, // Convertimos la fecha a milisegundos
                    escapeCSV(task.getContent()), delimitador,         // Descripción
                    String.valueOf(task.getPriority()), delimitador,    // Prioridad
                    String.valueOf(task.getEstimatedDuration()), delimitador, // Duración
                    String.valueOf(task.isCompleted())));              // Estado completado
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
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] fields = linea.split(delimitador);
                
                if (fields.length == 7) {
                    int id = Integer.parseInt(fields[0]);
                    String title = unescapeCSV(fields[1]);
                    Date date = new Date(Long.parseLong(fields[2]));
                    String content = unescapeCSV(fields[3]);
                    int priority = Integer.parseInt(fields[4]);
                    int estimatedDuration = Integer.parseInt(fields[5]);
                    boolean isCompleted = Boolean.parseBoolean(fields[6]);

                    Task task = new Task(id, title, date, content, priority, estimatedDuration, isCompleted);
                    tasks.add(task);
                } else {
                    throw new ExporterException("La línea no tiene el formato esperado: " + linea);
                }
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
