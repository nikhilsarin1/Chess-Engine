package com.example.chessproject.view;

import com.example.chessproject.controller.ControllerImpl;
import com.example.chessproject.model.ModelImpl;
import com.example.chessproject.model.ModelObserver;
import com.example.chessproject.model.PieceType;
import com.example.chessproject.model.Square;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Objects;

public class BoardView implements FXComponent, ModelObserver {
  private final ControllerImpl controller;
  private final GridPane gridPane;
  private ModelImpl model;
  private Rectangle originRectangle;
  private boolean isFlashing;
  private boolean orientation;
  private Square lastMoveOrigin;
  private Square lastMoveDestination;

  public BoardView(ModelImpl model, ControllerImpl controller) {
    this.model = model;
    this.controller = controller;
    this.gridPane = new GridPane();
    this.originRectangle = null;
    this.isFlashing = false;
    this.model.startingBoard();

    showColorSelectionDialog();
  }

  public Parent render() {
    gridPane.setPadding(new Insets(10));

    // Create column and row constraints for the GridPane
    for (int i = 0; i < 8; i++) {
      ColumnConstraints colConstraints = new ColumnConstraints(80);
      RowConstraints rowConstraints = new RowConstraints(80);
      gridPane.getColumnConstraints().add(colConstraints);
      gridPane.getRowConstraints().add(rowConstraints);
    }

    // Iterate through each square on the chessboard
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {

        // Renders in the board based on the specified orientation
        Square square =
            model
                .getBoard()
                .getSquare((orientation ? 7 - row : row), (orientation ? 7 - col : col));

        // Create a rectangle to represent the square
        Rectangle rectangle =
            new Rectangle(
                80, 80, (row + col) % 2 == 0 ? Color.web("#F0D9B5") : Color.web("#B58863"));
        rectangle.setUserData(rectangle.getFill());
        gridPane.add(rectangle, col, row);

        // Highlight the square if it is the last moved origin or destination
        if (square == lastMoveOrigin || square == lastMoveDestination) {
          highlightSquare(rectangle);
        }

        // Set mouse event handlers for square highlighting and piece movement
        rectangle.setOnMouseEntered(event -> highlightOutline(rectangle));
        rectangle.setOnMouseExited(event -> unhighlightOutline(rectangle));

        // Event handler for square and piece click and movement
        EventHandler<MouseEvent> eventHandler =
            event -> {
              if (controller.getOrigin() != null
                  && controller.getOrigin().getRow() == square.getRow()
                  && controller.getOrigin().getCol() == square.getCol()
                  && !isFlashing) {
                // Clicked on the same square as the origin, reset the origin and unhighlight the
                // square
                controller.resetOrigin();
                unhighlightSquare(rectangle);
                originRectangle = null;
              } else if (controller.getOrigin() == null
                  && square.isOccupied()
                  && square.getPiece().getColor() == model.getCurrentTurn()
                  && !isFlashing) {
                // Clicked on an occupied square to set the origin and highlight the square
                controller.clickOrigin(square);
                highlightSquare(rectangle);
                originRectangle = rectangle;
              } else if (controller.getOrigin() != null
                  && controller.getOrigin() != square
                  && !isFlashing) {
                // Clicked on a different square as the destination, attempt to move the piece
                try {
                  controller.clickDestination(square);
                } catch (IllegalArgumentException e) {
                  // Invalid move, flash the squares and reset the origin
                  flashSquares(originRectangle, rectangle);
                  controller.resetOrigin();
                  unhighlightSquare(originRectangle);
                }
              }
            };

        // Add a piece image to the square if it is occupied
        if (square.getPiece() != null) {
          ImageView pieceImage = new ImageView(square.getPiece().getImagePath());
          pieceImage.setFitHeight(80);
          pieceImage.setFitWidth(80);
          GridPane.setHalignment(pieceImage, HPos.CENTER);
          GridPane.setValignment(pieceImage, VPos.CENTER);
          GridPane.setMargin(pieceImage, new Insets(10));

          pieceImage.setOnMouseEntered(event -> highlightOutline(rectangle));
          pieceImage.setOnMouseExited(event -> unhighlightOutline(rectangle));

          // Selects square if piece is clicked
          pieceImage.setOnMouseClicked(eventHandler);

          gridPane.add(pieceImage, col, row);
        }

        // Selects square if rectangle is clicked
        rectangle.setOnMouseClicked(eventHandler);
      }
    }

    return gridPane;
  }

  private void showColorSelectionDialog() {
    // Create an alert dialog
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Color Selection");
    alert.setHeaderText("Choose your color");
    alert.setContentText("Select the color you want to play as:");

    // Set the stage properties
    Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.initStyle(StageStyle.UNDECORATED);

    // Create button types for color selection
    ButtonType whiteButton = new ButtonType("White");
    ButtonType blackButton = new ButtonType("Black");

    // Set the button types for the dialog
    alert.getButtonTypes().setAll(whiteButton, blackButton);

    // Handle the dialog close event
    stage.setOnCloseRequest(
        event -> {
          Platform.exit();
          System.exit(0);
        });

    // Show the dialog and handle the button selection
    alert
        .showAndWait()
        .ifPresent(
            buttonType -> {
              if (buttonType == blackButton) {
                // Flip the board if Black is selected
                this.flipBoard();
                model.setSelectedPlayer(false);
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1500), event -> controller.botTurn()));
                timeline.play();
              }
            });
  }

  private void flipBoard() {
    // Flips view of the board
    this.orientation = !this.orientation;
  }

  private void highlightSquare(Rectangle rectangle) {
    if (!isFlashing) {
      Color baseColor = (Color) rectangle.getFill();

      // Calculate the highlighted color by interpolating between the base color and yellow
      Color highlightedColor = baseColor.interpolate(Color.YELLOW, 0.3);

      // Set the highlighted color as the new fill color for the square
      rectangle.setFill(highlightedColor);

      // Store the original base color in the user data property of the rectangle
      rectangle.setUserData(baseColor);
    }
  }

  private void unhighlightSquare(Rectangle rectangle) {
    // Retrieve the original base color from the user data property of the rectangle
    Color originalColor = (Color) rectangle.getUserData();

    // Set the original color as the original fill color for the square
    rectangle.setFill(originalColor);
  }

  private void highlightOutline(Rectangle rectangle) {
    // Bring the rectangle to the front
    rectangle.toFront();

    // Get the piece image associated with the rectangle, if any
    Node pieceImage = getPieceImage(rectangle);

    // Bring the piece image to the front if it exists
    if (pieceImage != null) {
      pieceImage.toFront();
    }

    // Set the stroke color and width to highlight the outline of the square
    rectangle.setStroke(Color.LIGHTBLUE);
    rectangle.setStrokeWidth(2.0);
  }

  private void unhighlightOutline(Rectangle rectangle) {
    // Clear the stroke color to remove the outline highlight
    rectangle.setStroke(null);
  }

  private Node getPieceImage(Rectangle rectangle) {
    ObservableList<Node> children = gridPane.getChildren();
    for (Node child : children) {
      // Check if the child is an ImageView and its position matches the specified rectangle
      if (child instanceof ImageView
          && Objects.equals(GridPane.getRowIndex(child), GridPane.getRowIndex(rectangle))
          && Objects.equals(GridPane.getColumnIndex(child), GridPane.getColumnIndex(rectangle))) {
        return child; // Return the ImageView representing the piece image
      }
    }
    return null; // Return null if no matching piece image is found
  }

  private void flashSquares(Rectangle origin, Rectangle destination) {
    if (isFlashing) {
      return; // Ignore the request if animation is already in progress
    }

    int numFlashes = 3; // Number of times the squares will flash
    Duration flashDuration = Duration.millis(200); // Duration of each flash

    // Create a Timeline animation for the flashing effect
    Timeline timeline = new Timeline();

    disableMouseEvents(true); // Disable mouse events during the animation

    isFlashing = true; // Set the flag to indicate animation is in progress

    int destRow = GridPane.getRowIndex(destination);
    int destCol = GridPane.getColumnIndex(destination);
    Square destSquare = model.getBoard().getSquare(destRow, destCol);

    // Set up keyframes for each flash
    for (int i = 0; i < numFlashes; i++) {
      KeyFrame flashOn =
          new KeyFrame(
              flashDuration.multiply(i),
              event -> {
                // Set the origin and destination squares to a light red color with transparency
                origin.setFill(Color.rgb(255, 0, 0, 0.6));
                destination.setFill(Color.rgb(255, 0, 0, 0.6));
              });

      KeyFrame flashOff =
          new KeyFrame(
              flashDuration.multiply(i + 0.5),
              event -> {
                // Restore the original fill color of the origin and destination squares
                origin.setFill((Color) origin.getUserData());
                destination.setFill((Color) destination.getUserData());
              });

      timeline.getKeyFrames().addAll(flashOn, flashOff);
    }

    timeline.setOnFinished(
        event -> {
          disableMouseEvents(false); // Re-enable mouse events after animation is complete
          isFlashing = false; // Reset the flag after animation is complete

          if (destSquare == lastMoveOrigin || destSquare == lastMoveDestination) {
            highlightSquare(
                destination); // Highlight the destination square if it was the last moved origin or
                              // destination
          }
        });

    // Start the animation
    timeline.play();
  }

  private void disableMouseEvents(boolean disable) {
    // Disables or enables mouse events for all rectangles in the gridPane
    for (Node node : gridPane.getChildren()) {
      if (node instanceof Rectangle) {
        node.setDisable(disable);
      }
    }
  }

  public void updateMove(ModelImpl model) {
    this.model = model;

    // Retrieve last move origin and destination from the model
    lastMoveOrigin = model.getLastMoveOrigin();
    lastMoveDestination = model.getLastMoveDestination();

    // Clear the current view and render the new board state after the move
    gridPane.getChildren().clear();
    this.render();

    // Check if the game has ended in checkmate
    if (model.isCheckmate()) {
      String winnerColor = !model.getCurrentTurn() ? "White" : "Black";
      String message = "Checkmate! " + winnerColor + " wins the game.";
      showAlertWithChoice(message);
    }
    // Check if the game has ended in a draw
    else if (model.isDraw()) {
      String drawReason = getDrawReason(model);
      String message = "The game ended in a draw due to " + drawReason;
      showAlertWithChoice(message);
    } else {
      Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), event -> controller.botTurn()));
      timeline.play();
    }
  }

  private String getDrawReason(ModelImpl model) {
    // Determine reason for the game ending in a draw
    if (model.isStalemate()) {
      return "stalemate";
    } else if (model.isFiftyMoveDraw()) {
      return "fifty-move rule";
    } else if (model.isInsufficientMaterial()) {
      return "insufficient material";
    } else if (model.isThreeFoldRepetition()) {
      return "three-fold repetition";
    } else {
      return "unknown reason";
    }
  }

  private void startNewGame() {
    Stage currentStage = (Stage) gridPane.getScene().getWindow();
    currentStage.close(); // Close the current stage

    // Create a new instance of the game model, controller, and board view
    ModelImpl newModel = new ModelImpl();
    ControllerImpl newController = new ControllerImpl(newModel);
    BoardView newBoardView = new BoardView(newModel, newController);

    // Register the new board view as an observer of the new model
    newModel.addObserver(newBoardView);

    // Render the new game board
    Parent newBoardParent = newBoardView.render();

    // Create a new scene with the rendered game board
    Scene newScene = new Scene(newBoardParent);

    // Create a new stage for the game window
    Stage newStage = new Stage();

    // Set the scene and title for the new stage
    newStage.setScene(newScene);
    newStage.setTitle("Chess Game");

    // Show the new game window
    newStage.show();
  }

  private void showAlertWithChoice(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Game Over");
    alert.setHeaderText(null);
    alert.setContentText(message);

    ButtonType playAgainButton = new ButtonType("Play Again");
    ButtonType exitButton = new ButtonType("Exit");
    alert.getButtonTypes().setAll(playAgainButton, exitButton);

    // Show the alert dialog and wait for it to close
    Platform.runLater(() -> alert
        .showAndWait()
        .ifPresent(
            buttonType -> {
              // Check the selected button and perform the corresponding action
              if (buttonType == playAgainButton) {
                startNewGame();
              } else if (buttonType == exitButton) {
                System.exit(0);
              }
            }));
  }

  public void updatePromotion(Square promotionSquare) {
    if (model.getCurrentTurn() != model.getSelectedPlayer() || model.searching) {
      model.setPromotedPieceType(PieceType.QUEEN);
      return;
    }
    // Create a PawnPromotionView for selecting the promoted piece type
    PawnPromotionView pawnPromotionView = new PawnPromotionView(promotionSquare);

    // Disable mouse events on the board during pawn promotion
    disableMouseEvents(true);

    // Set the onPromotion event listener for handling the promotion selection
    pawnPromotionView.setOnPromotion(
        event -> {
          // Get the selected piece type from the PawnPromotionView
          PieceType promotedPieceType = pawnPromotionView.getSelectedPieceType();

          // Enable mouse events on the board again
          disableMouseEvents(false);

          // Update the model with the promoted piece type (or default to QUEEN)
          model.setPromotedPieceType(
              Objects.requireNonNullElse(promotedPieceType, PieceType.QUEEN));
        });

    // Show the PawnPromotionView to allow the player to select the promoted piece type
    pawnPromotionView.show();
  }
}
