package controller;

import model.Model;
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

    public void end() {
        view.end();
    }
}
