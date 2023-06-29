package com.example.chessproject.model;

public interface Model {
  /**
   * Checks if a move from the current square to the new square is valid.
   *
   * @param currentSquare the square containing the piece to move
   * @param newSquare the destination square for the piece
   * @return true if the move is valid, false otherwise
   */
  boolean isMoveValid(Square currentSquare, Square newSquare);

  /**
   * Moves a piece from the current square to the new square.
   *
   * @param currentSquare the square containing the piece to move
   * @param newSquare the destination square for the piece
   */
  void movePiece(Square currentSquare, Square newSquare, boolean isActualMove);

  /**
   * Checks if the current player is in checkmate.
   *
   * @return true if the current player is in checkmate, false otherwise
   */
  boolean isCheckmate();

  /**
   * Checks if the game is a draw.
   *
   * @return true if the game is a draw, false otherwise
   */
  boolean isDraw();
}
