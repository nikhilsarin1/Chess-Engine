package com.example.chessproject.model;

/** Represents a Queen chess piece. */
public class Queen extends Piece {
  /**
   * Constructor for the Queen class.
   *
   * @param color the color of the queen piece
   */
  public Queen(boolean color) {
    super(PieceType.QUEEN, color);
  }

  /**
   * Determines if the queen can move to the specified square on the chessboard.
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
    int rowIncrement = (newRow > currentRow) ? 1 : -1;
    int colIncrement = (newCol > currentCol) ? 1 : -1;

    // Queen cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }

    // Moving along a row
    if (currentRow == newRow) {
      for (int i = 1; i <= colDiff; i++) {
        int col = currentCol + (i * colIncrement);
        Square square = board.getSquare(currentRow, col);

        if (square.isOccupied()) {
          if (square.getPiece().getColor() == this.getColor()) {
            return false; // Queen is blocked by a piece of its own color
          } else if (square.getRow() != newRow || square.getCol() != newCol) {
            return false; // Queen is blocked by a piece of the opposite color
          }
        }
      }
      return true; // Queen can move along the same row
    }

    // Moving along a column
    if (currentCol == newCol) {
      for (int i = 1; i <= rowDiff; i++) {
        int row = currentRow + (i * rowIncrement);
        Square square = board.getSquare(row, currentCol);

        if (square.isOccupied()) {
          if (square.getPiece().getColor() == this.getColor()) {
            return false; // Queen is blocked by a piece of its own color
          } else if (square.getRow() != newRow || square.getCol() != newCol) {
            return false; // Queen is blocked by a piece of the opposite color
          }
        }
      }
      return true; // Queen can move along the same column
    }

    // Moving along a diagonal
    if (rowDiff == colDiff) {
      for (int i = 1; i <= rowDiff; i++) {
        int row = currentRow + (i * rowIncrement);
        int col = currentCol + (i * colIncrement);
        Square square = board.getSquare(row, col);

        if (square.isOccupied()) {
          if (square.getPiece().getColor() == this.getColor()) {
            return false; // Queen is blocked by a piece of its own color
          } else if (square.getRow() != newRow || square.getCol() != newCol) {
            return false; // Queen is blocked by a piece of the opposite color
          }
        }
      }
      return true; // Queen can move along the same diagonal
    }

    return false; // Queen can only move along rows, columns, or diagonals
  }

  /**
   * Get the image path of the queen based on its color.
   *
   * @return the image path of the queen
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_queen.png";
    } else {
      return "black_queen.png";
    }
  }
}
