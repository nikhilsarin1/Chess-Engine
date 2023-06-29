package com.example.chessproject.model;

/** Represents a Bishop chess piece. */
public class Bishop extends Piece {
  /**
   * Constructor for the Bishop class.
   *
   * @param color the color of the bishop piece
   */
  public Bishop(boolean color) {
    super(PieceType.BISHOP, color);
  }

  /**
   * Determines if the bishop can move to the specified square on the chessboard.
   *
   * @param newSquare the square to move to
   * @param board the chessboard
   * @return true if the move is possible, false otherwise
   */
  @Override
  public boolean isPossibleMove(Square newSquare, ChessBoard board) {
    int currentRow = this.getSquare().getRow();
    int currentCol = this.getSquare().getCol();
    int newRow = newSquare.getRow();
    int newCol = newSquare.getCol();
    int rowDiff = Math.abs(currentRow - newRow);
    int colDiff = Math.abs(currentCol - newCol);

    // Bishop cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }

    // Bishop can only move diagonally
    if (rowDiff != colDiff) {
      return false;
    }

    // Determine the direction of movement
    int rowIncrement = (newRow > currentRow) ? 1 : -1;
    int colIncrement = (newCol > currentCol) ? 1 : -1;

    // Check for any obstructions along the diagonal path
    for (int i = 1; i <= rowDiff; i++) {
      int row = currentRow + (i * rowIncrement);
      int col = currentCol + (i * colIncrement);
      Square square = board.getSquare(row, col);

      if (square.isOccupied()) {
        if (square.getPiece().getColor() == this.getColor()) {
          // Bishop is blocked by a piece of its own color
          return false;
        } else if (square.getRow() != newRow || square.getCol() != newCol) {
          // Bishop is blocked by a piece of the opposite color
          return false;
        }
      }
    }

    // If no obstructions were found, the move is valid
    return true;
  }

  /**
   * Get the image path of the bishop based on its color.
   *
   * @return the image path of the bishop
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_bishop.png";
    } else {
      return "black_bishop.png";
    }
  }
}
