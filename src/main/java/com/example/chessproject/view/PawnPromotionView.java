package com.example.chessproject.view;

import com.example.chessproject.model.PieceType;
import com.example.chessproject.model.Square;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PawnPromotionView {
  private final Stage stage;
  private EventHandler<ActionEvent> promotionEventHandler;
  private PieceType selectedPieceType;

  public PawnPromotionView(Square promotionSquare) {
    // Determine the color of the promoted piece based on the row of the promotion square
    boolean color;
    color = promotionSquare.getRow() == 0;

    // Create a new stage for the pawn promotion view
    stage = new Stage();
    stage.initModality(Modality.APPLICATION_MODAL);
    stage.setTitle("Pawn Promotion");

    // Create image views for the promotion options: queen, rook, bishop, and knight
    ImageView queenImage;
    ImageView rookImage;
    ImageView bishopImage;
    ImageView knightImage;

    // Choose the appropriate image files based on the color
    if (color) {
      queenImage = createPromotionImage("white_queen.png");
      rookImage = createPromotionImage("white_rook.png");
      bishopImage = createPromotionImage("white_bishop.png");
      knightImage = createPromotionImage("white_knight.png");
    } else {
      queenImage = createPromotionImage("black_queen.png");
      rookImage = createPromotionImage("black_rook.png");
      bishopImage = createPromotionImage("black_bishop.png");
      knightImage = createPromotionImage("black_knight.png");
    }

    // Create a tile pane and add the promotion images
    TilePane tilePane = new TilePane(10, 10, queenImage, rookImage, bishopImage, knightImage);
    tilePane.setPrefColumns(4);
    tilePane.setAlignment(Pos.CENTER);

    // Create a scene with the tile pane and set it as the scene for the stage
    Scene scene = new Scene(tilePane);
    stage.setScene(scene);

    // Set the width of the stage
    stage.setWidth(400);
  }

  public void show() {
    // Shows the pawn promotion view and waits for user interaction
    stage.showAndWait();
  }

  public void setOnPromotion(EventHandler<ActionEvent> eventHandler) {
    promotionEventHandler = eventHandler;

    // Trigger the promotion event immediately if an event handler is provided
    if (promotionEventHandler != null) {
      promotionEventHandler.handle(new ActionEvent(this, null));
    }
  }

  private ImageView createPromotionImage(String imagePath) {
    ImageView imageView = new ImageView(imagePath);
    imageView.setPreserveRatio(true);
    imageView.setFitHeight(80);

    // Set mouse click event handler to handle promotion selection
    imageView.setOnMouseClicked(
        event -> {
          if (promotionEventHandler != null) {
            ActionEvent actionEvent = new ActionEvent(event.getSource(), event.getTarget());
            selectedPieceType = getPromotedPieceType(imageView); // Store the selected piece type
            promotionEventHandler.handle(actionEvent);
          }
          stage.close();
        });

    return imageView;
  }

  private PieceType getPromotedPieceType(ImageView imageView) {
    String imagePath = imageView.getImage().getUrl();

    // Determine the selected piece type based on the image path
    if (imagePath.contains("queen")) {
      return PieceType.QUEEN;
    } else if (imagePath.contains("rook")) {
      return PieceType.ROOK;
    } else if (imagePath.contains("bishop")) {
      return PieceType.BISHOP;
    } else if (imagePath.contains("knight")) {
      return PieceType.KNIGHT;
    } else {
      return PieceType.QUEEN;
    }
  }

  public PieceType getSelectedPieceType() {
    return selectedPieceType;
  }
}
