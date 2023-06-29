package com.example.chessproject.model;

/** Represents a King chess piece. */
public class King extends Piece {
  private boolean kingSideCastle;
  private boolean queenSideCastle;
  /**
   * Constructor for the King class.
   *
   * @param color the color of the king piece
   */
  public King(boolean color) {
    super(PieceType.KING, color);
  }

  /**
   * Determines if the king can move to the specified square on the chessboard.
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

    // The King cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }

    int rowDiff = Math.abs(newRow - currentRow);
    int colDiff = Math.abs(newCol - currentCol);

    // Check if king can castle king side
    if (currentRow == newRow && colDiff == 2 && newSquare.getCol() == 6) {
      return kingSideCastle;
    }

    if (currentRow == newRow && colDiff == 2 && newSquare.getCol() == 2) {
      return queenSideCastle;
    }

    // Check for a regular move (moving one square in any direction)
    if (rowDiff <= 1 && colDiff <= 1) {
      Square square = board.getSquare(newRow, newCol);
      // The destination square must be unoccupied or occupied by an opponent's piece
      return !square.isOccupied() || square.getPiece().getColor() != this.getColor();
    }

    // If none of the above conditions are met, the move is not valid for the King
    return false;
  }

  /**
   * Sets the flag indicating whether the king can perform a king-side castle.
   *
   * @param bool the boolean value indicating if the king can perform a king-side castle
   */
  public void setKingSideCastle(boolean bool) {
    kingSideCastle = bool;
  }

  /**
   * Sets the flag indicating whether the king can perform a queen-side castle.
   *
   * @param bool the boolean value indicating if the king can perform a queen-side castle
   */
  public void setQueenSideCastle(boolean bool) {
    queenSideCastle = bool;
  }

  /**
   * Get the image path of the king based on its color.
   *
   * @return the image path of the king
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_king.png";
    } else {
      return "black_king.png";
    }
  }
}
