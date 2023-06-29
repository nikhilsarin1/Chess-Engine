package com.example.chessproject.model;

import java.util.ArrayList;
import java.util.List;

/** Represents a chessboard consisting of squares. The chessboard is an 8x8 grid of squares. */
public class ChessBoard {
  private final List<List<Square>> board;

  /** Constructs a new 8x8 standard ChessBoard */
  public ChessBoard() {
    board = new ArrayList<>(8);
    for (int row = 0; row < 8; row++) {
      List<Square> rowList = new ArrayList<>(8);
      for (int col = 0; col < 8; col++) {
        rowList.add(new Square(row, col));
      }
      board.add(rowList);
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
    return board.get(row).get(col);
  }
}
