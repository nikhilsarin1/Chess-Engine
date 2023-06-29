package com.example.chessproject.controller;

import com.example.chessproject.ChessBot.Bot;
import com.example.chessproject.model.ModelImpl;
import com.example.chessproject.model.Square;

public class ControllerImpl implements Controller {
  private final ModelImpl model;
  private Square origin;
  private final Bot bot;

  public ControllerImpl(ModelImpl model) {
    if (model == null) {
      throw new IllegalArgumentException();
    }

    this.model = model;
    this.origin = null;
    this.bot = new Bot(this.model);

  }

  public Square getOrigin() {
    return origin;
  }

  public void resetOrigin() {
    this.origin = null;
  }

  public void clickOrigin(Square square) {
    // Origin square must contain a piece
    if (!model.getBoard().getSquare(square.getRow(), square.getCol()).isOccupied()) {
      throw new IllegalArgumentException();
    }

    // Origin square must be a square containing a piece of the current color's turn
    if (model.getBoard().getSquare(square.getRow(), square.getCol()).getPiece().getColor()
        != model.getCurrentTurn()) {
      throw new IllegalArgumentException();
    }

    this.origin = square;
  }

  public void clickDestination(Square square) {
    // Destination square must be a valid move
    if (!model.isMoveValid(origin, square)) {
      throw new IllegalArgumentException();
    }

    // Execute code for moving the selected piece

    model.movePiece(origin, square, true);
    this.origin = null;
  }

  public void botTurn() {
    if (model.getCurrentTurn() != model.getSelectedPlayer()) {
      long startTime = System.nanoTime(); // Capture the start time
      bot.search(3, -999999999, 999999999);
      long endTime = System.nanoTime(); // Capture the end time
      long elapsedTime = endTime - startTime;
      double elapsedSeconds = (double) elapsedTime / 1_000_000_000;
      System.out.println("Moves Searched: " + bot.searchCount);
      System.out.println("Search Time: " + elapsedSeconds);
      System.out.println("Moves/sec: " + bot.searchCount/elapsedSeconds);

      Square origin = bot.getOrigin();
      Square destination = bot.getDestination();

      System.out.println("Origin Square: " + bot.getOrigin().getRow() + ", " + bot.getOrigin().getCol());
      System.out.println("Destination Square: " + bot.getDestination().getRow() + ", " + bot.getDestination().getCol());

      model.movePiece(origin, destination, true);
      model.searching = false;
      bot.searchCount = 0;

      // Current implementation checking around 2500-3000 nodes per sec
    }
  }
}
