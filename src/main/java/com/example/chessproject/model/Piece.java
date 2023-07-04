package com.example.chessproject.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Piece class represents a generic Chess piece. It is an abstract class that provides common
 * properties and behaviors for all Chess pieces. Subclasses must implement the isPossibleMove and
 * getImagePath methods.
 */
public abstract class Piece {
  private long whiteBitBoard;
  private long blackBitBoard;
  private final boolean color;
  private final PieceType type;
  private Square square;
  private boolean hasMoved;

  /**
   * Constructs a new Piece with the specified type and color.
   *
   * @param type the type of the Chess piece
   * @param color the color of the Chess piece
   */
  public Piece(PieceType type, boolean color) {
    this.type = type;
    this.color = color;
    this.square = null;
    this.hasMoved = false;
    this.whiteBitBoard = 0L;
    this.blackBitBoard = 0L;
  }

  /**
   * Retrieves the type of the Chess piece.
   *
   * @return the type of the Chess piece
   */
  public PieceType getType() {
    return type;
  }

  /**
   * Retrieves the color of the Chess piece.
   *
   * @return the color of the Chess piece
   */
  public boolean getColor() {
    return color;
  }

  /**
   * Retrieves the Square on the Chessboard where the piece is located.
   *
   * @return the Square where the piece is located
   */
  public Square getSquare() {
    return square;
  }

  /**
   * Sets the Square on the Chessboard where the piece is located.
   *
   * @param newSquare the new Square where the piece is located
   */
  public void setSquare(Square newSquare) {
    this.square = newSquare;
  }

  /**
   * Checks if the piece has moved.
   *
   * @return true if the piece has moved, false otherwise
   */
  public boolean hasMoved() {
    return hasMoved;
  }

  /** Sets the flag indicating that the piece has moved. */
  public void setHasMoved(boolean bool) {
    this.hasMoved = bool;
  }

  /**
   * Retrieves a list of possible moves for the piece on the Chessboard.
   *
   * @param board the Chessboard representing the game state
   * @return a list of possible moves for the piece
   */
  public List<Square> getPossibleMoves(ChessBoard board) {
    List<Square> possibleMoves = new ArrayList<>();

    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        Square square = board.getSquare(row, col);
        if (this.isPossibleMove(square, board)) {
          possibleMoves.add(square);
        }
      }
    }
    return possibleMoves;
  }

  /**
   * Checks if the piece can make a possible move to the specified Square on the Chessboard.
   *
   * @param newSquare the Square to move the piece to
   * @param board the Chessboard representing the game state
   * @return true if the move is possible, false otherwise
   */
  public abstract boolean isPossibleMove(Square newSquare, ChessBoard board);

  /**
   * Retrieves the file path of the image representing the Chess piece.
   *
   * @return the file path of the image
   */
  public abstract String getImagePath();
}
