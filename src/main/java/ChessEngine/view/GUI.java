package ChessEngine.view;

import ChessEngine.controller.Controller;
import ChessEngine.model.Model;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Create a new instance of the ModelImpl class
        Model model = new Model();

        // Create a new instance of the ControllerImpl class, passing the model as a parameter
        Controller controller = new Controller(model);

        // Create a new instance of the BoardView class, passing the model and controller as parameters
        ChessboardView chessboardView = new ChessboardView(model, controller);

        // Add the chessboardView as an observer to the model
        model.addObserver(chessboardView);

        // Render the board view and get its root Parent node
        Parent chessboardParent = chessboardView.render();

        // Create a new Scene with the boardParent as its root node
        Scene scene = new Scene(chessboardParent);

        // Set the scene for the primaryStage
        primaryStage.setScene(scene);

        // Set the title of the primaryStage
        primaryStage.setTitle("Chess Game");

        // Show the primaryStage
        primaryStage.show();
    }
}
