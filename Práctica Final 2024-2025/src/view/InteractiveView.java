package view;

import com.coti.tools.Esdia;

import controller.Controller;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class InteractiveView extends BaseView{
    public InteractiveView(Controller controller) {
        super(controller);
    }

    @Override
    public void init() {
        System.out.println("¡Bienvenido!");
        boolean exit = false;

        while (!exit) {
            System.out.println("\nMenú Principal:");
            System.out.println("1. Crear tarea");
            System.out.println("2. Listar tareas pendientes");
            System.out.println("3. Listar historial completo");
            System.out.println("4. Detallar una tarea");
            System.out.println("5. Exportar tareas");
            System.out.println("6. Importar tareas");
            System.out.println("7. Salir");

            int opcion;
            opcion = Esdia.readInt("Elija una opción: ");

            switch (opcion) {
                case 1:
                    crearTarea();
                    break;
                case 2:
                    listarTareasPendientes();
                    break;
                case 3:
                    listarHistorialCompleto();
                    break;
                case 4:
                    detallarTarea();
                    break;
                case 5:
                    exportarTarea();
                    break;
                case 6:
                    importarTarea();
                    break;
                case 7:
                    exit = true;
                    break;
                default:
                    System.out.println("Opción no válida. Prueba con otra opción.");
            }
        }

        end();
    }

    @Override
    public void showMessage(String message) {
        System.out.println("Mensaje: " + message);
    }

    @Override
    public void showErrorMessage(String errorMessage) {
        System.err.println("Error: " + errorMessage);
    }

    @Override
    public void end() {
        System.out.println("Saliendo de la aplicación interactiva. ¡Nos vemos!");
    }

    // Métodos:
    public void crearTarea() {
        System.out.println("\nCrear nueva tarea: ");

        try {
            String titulo = Esdia.readString("Título: ");
            String descripcion = Esdia.readString("Descripción: ");
            int prioridad = Esdia.readInt("Prioridad (1-5): ");
            int duracion = Esdia.readInt("Duración estimada (minutos): ");
            
            Task task = new Task (0, titulo, new Date(), descripcion, prioridad, duracion, false);
            controller.addTask(task);

            showMessage("Tarea creada con éxito.");
        } catch (Exception e) {
            showErrorMessage("Error al crear la tarea: " + e.getMessage());
        }
    }

    public void listarTareasPendientes() {
        StringBuilder output = new StringBuilder();
        output.append("\nTareas pendientes (ordenadas por prioridad):\n");

        try {
            List<Task> tasks = controller.getPendingTasks();

            if (tasks.isEmpty()) {
                showMessage("No hay tareas pendientes.");
            } else {
                tasks.forEach(task -> output.append(task.toString()).append("\n"));
            }
        } catch (Exception e) {
            showErrorMessage("Error al listar las tareas pendientes: " + e.getMessage());
        }

        System.out.println(output.toString());
    }

    public void listarHistorialCompleto() {
        StringBuilder output = new StringBuilder();
        output.append("\nHistorial completo de tareas:\n");

        try {
            List<Task> tasks = controller.getAllTasks();

            if (tasks.isEmpty()) {
                showMessage("No hay tareas registradas.");
            } else {
                tasks.forEach(task -> output.append(task.toString()).append("\n"));
            }
        } catch (Exception e) {
            showErrorMessage("Error al listar todas las tareas: " + e.getMessage());
        }

        System.out.println(output.toString());
    }

    public void detallarTarea() {
        System.out.println("\nDetalle de tarea:");

        try {
            int id = Esdia.readInt("Ingrese el ID de la tarea: ");
            
            Task task = controller.getTaskById(id);

            if (task == null) {
                showMessage("No se encontró la tarea con ID: " + id);
                return;
            }

            System.out.println(task);

            System.out.println("\nOpciones:");
            System.out.println("1. Marcar como completada/incompleta");
            System.out.println("2. Modificar información");
            System.out.println("3. Eliminar tarea");
            System.out.println("4. Volver al menú principal");

            int opcion = Esdia.readInt("Elija una opción: ");

            switch (opcion) {
                case 1:
                    marcarTarea(task);
                    break;
                case 2:
                    modificarTarea(task);
                    break;
                case 3:
                    eliminarTarea(task);
                    break;
                case 4:
                    System.out.println("Regresando al menú principal...");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        } catch (Exception e) {
            showErrorMessage("Error al detallar la tarea: " + e.getMessage());
        }
    }

    public void marcarTarea(Task task) {
        try {
            task.setCompleted(!task.isCompleted());
            controller.updateTask(task);
            showMessage("El estado de la tarea se actualizó correctamente.");
        } catch (Exception e) {
            showErrorMessage("Error al cambiar el estado de la tarea: " + e.getMessage());
        }
    }

    public void modificarTarea(Task task) {
        try {
            String nuevoTitulo = Esdia.readString("Nuevo título (no introduzcas nada para no cambiar): ");
            if (!nuevoTitulo.isEmpty()) {
                task.setTitle(nuevoTitulo);
            }

            String nuevaDescripcion = Esdia.readString("Nueva descripción (no introduzcas nada para no cambiar): ");
            if (!nuevaDescripcion.isEmpty()) {
                task.setContent(nuevaDescripcion);
            }

            String nuevaPrioridad = Esdia.readString("Nueva prioridad (1-5, no introduzcas nada para no cambiar): ");
            if (!nuevaPrioridad.isEmpty()) {
                task.setPriority(Integer.parseInt(nuevaPrioridad));
            }

            controller.updateTask(task);
            showMessage("Tarea actualizada correctamente.");
        } catch (Exception e) {
            showErrorMessage("Error al modificar la tarea: " + e.getMessage());
        }
    }

    public void eliminarTarea(Task task) {
        try {
            controller.deleteTask(task);
            showMessage("Tarea eliminada con éxito.");
        } catch (Exception e) {
            showErrorMessage("Error al eliminar la tarea: " + e.getMessage());
        }
    }

    public void exportarTarea() {
        System.out.println("\nExportar tareas:");

        try {
            String formato = Esdia.readString("Formato (json/csv): ");

            if (!formato.equals("json") && !formato.equals("csv")) {
                throw new IllegalArgumentException("Formato no válido. Debe ser 'json' o 'csv'.");
            }

            String homePath = System.getProperty("user.home");
            String fileName = "output." + formato;
            String filePath = homePath + "" + fileName;

            File file = new File(filePath);
            if (!file.getParentFile().exists()) {
                throw new IOException("La carpeta de destino no existe.");
            }

            controller.exportTasks(formato, filePath);
            showMessage("Tareas exportadas exitosamente en " + filePath);
        } catch (Exception e) {
            showErrorMessage("Error al exportar tareas: " + e.getMessage());
        } 
    }

    public void importarTarea() {
        System.out.println("\nImportar tareas:");

        try {
            String formato = Esdia.readString("Formato (json/csv): ");

            String homePath = System.getProperty("user.home");
            String fileName = "output." + formato;
            String filePath = homePath + "/" + fileName;

            controller.importTasks(formato, filePath);
            showMessage("Tareas importadas exitosamente en " + filePath);
        } catch (Exception e) {
            showErrorMessage("Error al importar tareas: " + e.getMessage());
        }
    }
}
