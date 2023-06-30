package com.example.chessproject.model;

/** Represents a chessboard consisting of squares. The chessboard is an 8x8 grid of squares. */
public class ChessBoard {
  private final Square[] board;

  /** Constructs a new 8x8 standard ChessBoard */
  public ChessBoard() {
    this.board = new Square[64];
    for (int i = 0; i<64; i++) {
      board[i] = new Square(i/8, i % 8);
    }

  }

  /**
   * Retrieves the square at the specified row and column.
   *
   * @param row the row index of the square
   * @param col the column index of the square
   * @return the square at the specified position
   * @throws IllegalArgumentException if the row or column index is out of bounds
   */
  public Square getSquare(int row, int col) {
    if (row > 7 || col > 7) {
      throw new IllegalArgumentException();
    }
    return board[row*8+col];
  }
}
