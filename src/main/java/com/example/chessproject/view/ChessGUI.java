package com.example.chessproject.view;

import com.example.chessproject.controller.ControllerImpl;
import com.example.chessproject.model.ModelImpl;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChessGUI extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    // Create a new instance of the ModelImpl class
    ModelImpl model = new ModelImpl();

    // Create a new instance of the ControllerImpl class, passing the model as a parameter
    ControllerImpl controller = new ControllerImpl(model);

    // Create a new instance of the BoardView class, passing the model and controller as parameters
    BoardView boardView = new BoardView(model, controller);

    // Add the boardView as an observer to the model
    model.addObserver(boardView);

    // Render the board view and get its root Parent node
    Parent boardParent = boardView.render();

    // Create a new Scene with the boardParent as its root node
    Scene scene = new Scene(boardParent);

    // Set the scene for the primaryStage
    primaryStage.setScene(scene);

    // Set the title of the primaryStage
    primaryStage.setTitle("Chess Game");

    // Show the primaryStage
    primaryStage.show();
  }
}
