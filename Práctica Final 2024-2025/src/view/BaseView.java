package view;

import controller.Controller;

public abstract class BaseView {
    protected Controller controller;

    // Constructor:
    public BaseView (Controller controller) {
        this.controller = controller;
    }

    // Setter
    public void setController(Controller controller) {
        this.controller = controller;
    }

    // Métodos
    public abstract void init(); // Inicia la vista y desencadena la lógica asociada
    public abstract void showMessage(String Message); // Muestra mensajes genéricos al usuario
    public abstract void showErrorMessage(String errorMessage); // Muestra mensajes de error
    public abstract void end(); // Finaliza la vista ordenadamente
}
