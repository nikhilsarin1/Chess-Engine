package ChessEngine.controller;

import ChessEngine.AI.Search;
import ChessEngine.model.Model;
import ChessEngine.model.Move;

import java.util.List;

public class Controller {
  private final Model model;
  private final Search bot;
  public int maxDepth;
  private int origin;

  public Controller(Model model) {
    this.model = model;
    this.origin = -1;
    this.maxDepth = 6;
    this.bot = new Search(model);
  }

  public int getOrigin() {
    return origin;
  }

  public void resetOrigin() {
    origin = -1;
  }

  public void clickOrigin(int square) {
    if (!model.getBitboard().isOccupiedSquare(square)) {
      throw new IllegalArgumentException();
    }
    this.origin = square;
  }

  public void clickDestination(int square) {
    if (!model.isMoveValid(origin, square)) {
      throw new IllegalArgumentException();
    }
    List<Move> moves = model.getBitboard().getLegalMoves();
    Move selectedMove = null;
    for (Move move : moves) {
      if (move.getOrigin() == origin && move.getDestination() == square) {
        selectedMove = move;
        break;
      }
    }
    assert selectedMove != null;
    model.movePiece(selectedMove, true, -1);
    this.origin = -1;
  }

  public void botTurn() {
    maxDepth = 6;
    long startTime = System.nanoTime(); // Capture the start time
    model.searching = true;
    bot.search(maxDepth, 0, -999999999, 999999999);
    long endTime = System.nanoTime(); // Capture the end time
    long elapsedTime = endTime - startTime;
    double elapsedSeconds = (double) elapsedTime / 1_000_000_000;
    System.out.println("Moves Searched: " + bot.searchCount);
    System.out.println("Search Time: " + elapsedSeconds);
    System.out.println("Moves/sec: " + bot.searchCount / elapsedSeconds);
    Move move = bot.getBestMove();
    model.movePiece(move, true, -1);
    model.searching = false;
    bot.searchCount = 0;
    bot.resetBestMove();
  }
}
