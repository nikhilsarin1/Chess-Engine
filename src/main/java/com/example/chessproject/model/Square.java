package com.example.chessproject.model;

/** Represents a square on a chessboard. */
public class Square {
  private final int row; // The row of the square
  private final int col; // The column of the square
  private Piece piece; // The piece occupying the square (null if empty)

  /**
   * Constructs a square at the specified row and column.
   *
   * @param row the row of the square
   * @param col the column of the square
   * @throws IllegalArgumentException if the row or column is out of bounds
   */
  public Square(int row, int col) {
    if (row > 7 || col > 7) {
      throw new IllegalArgumentException("Invalid row or column for a square.");
    }

    this.row = row;
    this.col = col;
    this.piece = null; // Square is initially empty
  }

  /**
   * Returns the row of the square.
   *
   * @return the row of the square
   */
  public int getRow() {
    return row;
  }

  /**
   * Returns the column of the square.
   *
   * @return the column of the square
   */
  public int getCol() {
    return col;
  }

  /**
   * Returns the piece occupying the square.
   *
   * @return the piece occupying the square (null if empty)
   */
  public Piece getPiece() {
    return piece;
  }

  /**
   * Sets the piece occupying the square.
   *
   * @param piece the piece to be set on the square
   */
  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  /**
   * Checks if the square is occupied by a piece.
   *
   * @return true if the square is occupied, false otherwise
   */
  public boolean isOccupied() {
    return piece != null;
  }
}
