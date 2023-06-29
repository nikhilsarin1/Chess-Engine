package com.example.chessproject.model;

/** Represents a Rook chess piece. */
public class Rook extends Piece {
  /**
   * Constructor for the Rook class.
   *
   * @param color the color of the rook piece
   */
  public Rook(boolean color) {
    super(PieceType.ROOK, color);
  }

  /**
   * Determines if the rook can move to the specified square on the chessboard.
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

    // Rook cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }

    int rowIncrement = (newRow > currentRow) ? 1 : -1;
    int colIncrement = (newCol > currentCol) ? 1 : -1;

    // Moving along a row
    if (currentRow == newRow) {
      for (int i = 1; i <= colDiff; i++) {
        int col = currentCol + (i * colIncrement);
        Square square = board.getSquare(currentRow, col);

        if (square.isOccupied()) {
          if (square.getPiece().getColor() == this.getColor()) {
            return false; // Rook is blocked by a piece of its own color
          } else if (square.getRow() != newRow || square.getCol() != newCol) {
            return false; // Rook is blocked by a piece of the opposite color
          }
        }
      }
      return true; // Rook can move along the same row
    }

    // Moving along a column
    if (currentCol == newCol) {
      for (int i = 1; i <= rowDiff; i++) {
        int row = currentRow + (i * rowIncrement);
        Square square = board.getSquare(row, currentCol);

        if (square.isOccupied()) {
          if (square.getPiece().getColor() == this.getColor()) {
            return false; // Rook is blocked by a piece of its own color
          } else if (square.getRow() != newRow || square.getCol() != newCol) {
            return false; // Rook is blocked by a piece of the opposite color
          }
        }
      }
      return true; // Rook can move along the same column
    }

    return false; // Rook can only move along rows or columns
  }

  /**
   * Get the image path of the rook based on its color.
   *
   * @return the image path of the rook
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_rook.png";
    } else {
      return "black_rook.png";
    }
  }
}
