package view;

import com.coti.tools.Esdia;

import controller.Controller;

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
            System.out.println("4. Marcar tarea como completada");
            System.out.println("5. Exportar tareas");
            System.out.println("6. Importar tareas");
            System.out.println("7. Salir");

            int opcion;
            opcion = Esdia.readInt("Elija una opción: ");

            switch (opcion) {
                case 1:
                    crearTarea();
                case 2:
                    listarTareas();
                case 3:
                    listarHistorial();
                case 4:
                    marcarTarea();
                case 5:
                    exportarTarea();
                case 6:
                    importarTarea();
                case 7:
                    exit = true;
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
        System.out.println("Saliendo de la aplicación interactiva. ¿Nos vemos!");
    }

    public void crearTarea() {

    }

    public void listarTareas() {

    }

    public void listarHistorial() {

    }

    public void marcarTarea() {

    }

    public void exportarTarea() {

    }

    public void importarTarea() {
        
    }
}
