package com.example.chessproject.model;

/** Represents a Knight chess piece. */
public class Knight extends Piece {
  /**
   * Constructor for the Knight class.
   *
   * @param color the color of the knight piece
   */
  public Knight(boolean color) {
    super(PieceType.KNIGHT, color);
  }

  /**
   * Checks if the Knight can move to the specified square on the chessboard.
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

    // Knight cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }

    int rowDiff = Math.abs(newRow - currentRow);
    int colDiff = Math.abs(newCol - currentCol);

    // Knight can move in an L-shape: 2 squares in one direction and 1 square in another direction,
    // or 1 square in one direction and 2 squares in another direction
    if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
      Square square = board.getSquare(newRow, newCol);
      // The target square must be unoccupied or occupied by an opponent's piece
      return !square.isOccupied() || square.getPiece().getColor() != this.getColor();
    }

    // If none of the above conditions are met, the move is not possible for the Knight
    return false;
  }

  /**
   * Returns the image file path for the Queen based on its color.
   *
   * @return the image file path
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_knight.png";
    } else {
      return "black_knight.png";
    }
  }
}
