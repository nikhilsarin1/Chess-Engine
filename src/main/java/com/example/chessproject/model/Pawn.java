package com.example.chessproject.model;

/** Represents a Pawn chess piece. */
public class Pawn extends Piece {
  private Square enPassantTarget;

  /**
   * Constructor for the Pawn class.
   *
   * @param color the color of the pawn piece
   */
  public Pawn(boolean color) {
    super(PieceType.PAWN, color);
  }

  /**
   * Determines if the pawn can move to the specified square on the chessboard.
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

    // Pawn cannot stay in the same position
    if (currentRow == newRow && currentCol == newCol) {
      return false;
    }


    // En passant move: if the target square matches the en passant target square, the move is valid
    if (enPassantTarget != null && enPassantTarget == newSquare) {
      return true;
    }

    // Capture move: if the target square is a capture square, occupied by an opponent's piece, the
    // move is valid
    if (isCaptureSquare(newSquare)
        && newSquare.isOccupied()
        && newSquare.getPiece().getColor() != this.getColor()) {
      return true;
    }

    // Regular pawn move
    if (this.getColor()) { // White pawn
      if (currentCol != newCol) {
        return false; // Pawns can only move forward in a straight line, not horizontally
      }

      // Moving one square forward
      if (currentRow - 1 == newRow && !board.getSquare(newRow, newCol).isOccupied()) {
        return true;
      }

      // Moving two squares forward from the starting position (row 6)
      if (currentRow == 6) {
        return currentRow - 2 == newRow
            && !board.getSquare(newRow, newCol).isOccupied()
            && !board.getSquare(currentRow - 1, newCol).isOccupied();
      }
    } else { // Black pawn
      if (currentCol != newCol) {
        return false; // Pawns can only move forward in a straight line, not horizontally
      }

      // Moving one square forward
      if (currentRow + 1 == newRow && !board.getSquare(newRow, newCol).isOccupied()) {
        return true;
      }

      // Moving two squares forward from the starting position (row 1)
      if (currentRow == 1) {
        return currentRow + 2 == newRow
            && !board.getSquare(newRow, newCol).isOccupied()
            && !board.getSquare(currentRow + 1, newCol).isOccupied();
      }
    }

    // If none of the above conditions are met, the move is not possible for the Pawn
    return false;
  }

  /**
   * Determines if the given square is a valid capture square for the Pawn.
   *
   * @param newSquare the square to check
   * @return true if the square is a capture square, false otherwise
   */
  public boolean isCaptureSquare(Square newSquare) {
    int currentRow = this.getSquare().getRow();
    int currentCol = this.getSquare().getCol();
    int newRow = newSquare.getRow();
    int newCol = newSquare.getCol();
    int colDiff = Math.abs(currentCol - newCol);

    // Check if the piece is a white pawn
    if (this.getColor()) {
      if (colDiff != 1) {
        return false; // Capturing pawns can only move one column left or right
      }
      return currentRow - newRow == 1; // Capturing pawns can only move one row forward
    } else { // The piece is a black pawn
      if (colDiff != 1) {
        return false; // Capturing pawns can only move one column left or right
      }
      return newRow - currentRow == 1; // Capturing pawns can only move one row forward
    }
  }

  /**
   * Returns the en passant target square.
   *
   * @return the en passant target square
   */
  public Square getEnPassantTarget() {
    return this.enPassantTarget;
  }

  /**
   * Sets the en passant target square.
   *
   * @param square the en passant target square to set
   */
  public void setEnPassantTarget(Square square) {
    this.enPassantTarget = square;
  }

  /**
   * Get the image path of the pawn based on its color.
   *
   * @return the image path of the pawn
   */
  @Override
  public String getImagePath() {
    if (this.getColor()) {
      return "white_pawn.png";
    } else {
      return "black_pawn.png";
    }
  }
}
