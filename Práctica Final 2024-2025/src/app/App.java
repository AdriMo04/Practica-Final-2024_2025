import controller.Controller;
import model.Model;
import model.BinaryRepository;
import view.InteractiveView;

public class App {
    public static void main(String[] args) throws Exception {
        BinaryRepository repository = new BinaryRepository();
        Model model = new Model(repository);
        InteractiveView view = new InteractiveView(null);
        Controller controller = new Controller(model, view);

        view.setController(controller);
        view.init();
    }
}
