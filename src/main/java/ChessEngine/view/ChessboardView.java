package ChessEngine.view;

import ChessEngine.controller.Controller;
import ChessEngine.model.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import java.util.Objects;

public class ChessboardView implements ModelObserver, FXComponent {
    private Model model;
    private Controller controller;
    private GridPane gridPane;
    private char[] chessboard;
    private Rectangle originRectangle;
    private int lastMoveOrigin;
    private int lastMoveDestination;
    private boolean isFlashing;
    private boolean orientation;
    public ChessboardView(Model model, Controller controller) {
        this.model = model;
        this.controller = controller;
        this.gridPane = new GridPane();
        this.chessboard = convertBitboardsToCharArray(model.getBitboard().pieceBitboards);
        this.isFlashing = false;
        this.lastMoveOrigin = -1;
        this.lastMoveDestination = -1;
        this.orientation = true;
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
        for (int i = 0 ; i < 64; i++) {
            int square = orientation ? i : 63 - i;
            int row = i / 8;
            int col = i % 8;
            Rectangle rectangle =
                    new Rectangle(
                            80, 80, (row + col) % 2 == 0 ? Color.web("#F0D9B5") : Color.web("#B58863"));
            rectangle.setUserData(rectangle.getFill());
            gridPane.add(rectangle, col, row);

            if (square == lastMoveOrigin || square == lastMoveDestination) {
                highlightSquare(rectangle);
            }

            // Set mouse event handlers for square highlighting and piece movement
            rectangle.setOnMouseEntered(event -> highlightOutline(rectangle));
            rectangle.setOnMouseExited(event -> unhighlightOutline(rectangle));

            EventHandler<MouseEvent> eventHandler =
                    event -> {
                        if (controller.getOrigin() != -1
                                && controller.getOrigin() == square && !isFlashing
                                ) {
                            // Clicked on the same square as the origin, reset the origin and unhighlight the
                            // square
                            controller.resetOrigin();
                            unhighlightSquare(rectangle);
                            originRectangle = null;
                        } else if (controller.getOrigin() == -1
                                && model.getBitboard().isOccupiedSquare(square)
                                && ((model.getCurrentTurn() && Character.isUpperCase(chessboard[square])) || (!model.getCurrentTurn() && Character.isLowerCase(chessboard[square]))) && !isFlashing) {
                            // Clicked on an occupied square to set the origin and highlight the square
                            controller.clickOrigin(square, chessboard[square]);
                            highlightSquare(rectangle);
                            originRectangle = rectangle;
                        } else if (controller.getOrigin() != -1
                                && controller.getOrigin() != square && !isFlashing
                                ) {
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

            if (chessboard[square] != ' ') {
                ImageView pieceImage = null;

                switch (chessboard[square]) {
                    case 'K' -> pieceImage = new ImageView("white_king.png");
                    case 'Q' -> pieceImage = new ImageView("white_queen.png");
                    case 'R' -> pieceImage = new ImageView("white_rook.png");
                    case 'B' -> pieceImage = new ImageView("white_bishop.png");
                    case 'N' -> pieceImage = new ImageView("white_knight.png");
                    case 'P' -> pieceImage = new ImageView("white_pawn.png");
                    case 'k' -> pieceImage = new ImageView("black_king.png");
                    case 'q' -> pieceImage = new ImageView("black_queen.png");
                    case 'r' -> pieceImage = new ImageView("black_rook.png");
                    case 'b' -> pieceImage = new ImageView("black_bishop.png");
                    case 'n' -> pieceImage = new ImageView("black_knight.png");
                    case 'p' -> pieceImage = new ImageView("black_pawn.png");
                }
                if (pieceImage != null) {
                    pieceImage.setFitHeight(80);
                    pieceImage.setFitWidth(80);
                    GridPane.setHalignment(pieceImage, HPos.CENTER);
                    GridPane.setValignment(pieceImage, VPos.CENTER);
                    GridPane.setMargin(pieceImage, new Insets(10));

                    pieceImage.setOnMouseEntered(event -> highlightOutline(rectangle));
                    pieceImage.setOnMouseExited(event -> unhighlightOutline(rectangle));

                    pieceImage.setOnMouseClicked(eventHandler);

                    gridPane.add(pieceImage, col, row);
                }
            }
            rectangle.setOnMouseClicked(eventHandler);
        }
        return gridPane;
    }

    public void updateMove(Model model) {
        this.model = model;

        lastMoveOrigin = model.getLastMoveOrigin();
        lastMoveDestination = model.getLastMoveDestination();

        gridPane.getChildren().clear();
        chessboard = convertBitboardsToCharArray(model.getBitboard().pieceBitboards);
        this.render();
    }

    public char[] convertBitboardsToCharArray(long[] pieceBitboards) {
        char[] board = new char[64];
        for (int i = 0; i < 64; i++) {
            board[i] = ' ';
        }

        for (int i = 0; i < 64; i++) {
            int index = (7 - (i / 8)) * 8 + (i % 8);
            if ((pieceBitboards[0] >> i & 1) == 1) {
                board[index] = 'K';
            }
            if ((pieceBitboards[1] >> i & 1) == 1) {
                board[index] = 'Q';
            }
            if ((pieceBitboards[2] >> i & 1) == 1) {
                board[index] = 'R';
            }
            if ((pieceBitboards[3] >> i & 1) == 1) {
                board[index] = 'B';
            }
            if ((pieceBitboards[4] >> i & 1) == 1) {
                board[index] = 'N';
            }
            if ((pieceBitboards[5] >> i & 1) == 1) {
                board[index] = 'P';
            }
            if ((pieceBitboards[6] >> i & 1) == 1) {
                board[index] = 'k';
            }
            if ((pieceBitboards[7] >> i & 1) == 1) {
                board[index] = 'q';
            }
            if ((pieceBitboards[8] >> i & 1) == 1) {
                board[index] = 'r';
            }
            if ((pieceBitboards[9] >> i & 1) == 1) {
                board[index] = 'b';
            }
            if ((pieceBitboards[10] >> i & 1) == 1) {
                board[index] = 'n';
            }
            if ((pieceBitboards[11] >> i & 1) == 1) {
                board[index] = 'p';
            }
        }

        return board;
    }

    private void highlightSquare(Rectangle rectangle) {
        Color baseColor = (Color) rectangle.getFill();

        // Calculate the highlighted color by interpolating between the base color and yellow
        Color highlightedColor = baseColor.interpolate(Color.YELLOW, 0.3);

        // Set the highlighted color as the new fill color for the square
        rectangle.setFill(highlightedColor);

        // Store the original base color in the user data property of the rectangle
        rectangle.setUserData(baseColor);
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
        int destSquare = destRow * 8 + destCol;

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

    private void flipBoard() {
        orientation = !orientation;
    }
}