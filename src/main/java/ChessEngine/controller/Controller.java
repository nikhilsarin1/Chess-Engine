package ChessEngine.controller;

import ChessEngine.AI.AI;
import ChessEngine.model.Model;
import ChessEngine.model.Move;

import java.util.List;

public class Controller {
  private final Model model;
  private final AI bot;
  private int origin;
  private char originPiece;

  public Controller(Model model) {
    this.model = model;
    this.origin = -1;
    this.bot = new AI(model);
  }

  public int getOrigin() {
    return origin;
  }

  public void resetOrigin() {
    origin = -1;
  }

  public void clickOrigin(int square, char piece) {
    if (!model.getBitboard().isOccupiedSquare(square)) {
      throw new IllegalArgumentException();
    }
    this.origin = square;
    this.originPiece = piece;
  }

  public void clickDestination(int square) {
    if (!model.isMoveValid(origin, square, originPiece)) {
      throw new IllegalArgumentException();
    }
    List<Move> moves = model.getLegalMoves().get(originPiece);
    Move selectedMove = null;
    for (Move move : moves) {
      if (move.getOrigin() == origin && move.getDestination() == square) {
        selectedMove = move;
        break;
      }
    }
    assert selectedMove != null;
    model.movePiece(selectedMove, true);
    String str = model.generateBoardState();
    model.boardState.put(str, model.boardState.getOrDefault(str, 0) + 1);
    model.notifyObservers();
    this.origin = -1;
  }

  public void botTurn() {
    long startTime = System.nanoTime(); // Capture the start time
    model.searching = true;
    bot.search(4, -999999999, 999999999);
    long endTime = System.nanoTime(); // Capture the end time
    long elapsedTime = endTime - startTime;
    double elapsedSeconds = (double) elapsedTime / 1_000_000_000;
    System.out.println("Moves Searched: " + bot.searchCount);
    System.out.println("Search Time: " + elapsedSeconds);
    System.out.println("Moves/sec: " + bot.searchCount / elapsedSeconds);
    Move move = bot.getBestMove();
    model.movePiece(move, true);
    model.searching = false;
    String str = model.generateBoardState();
    model.boardState.put(str, model.boardState.getOrDefault(str, 0) + 1);
    model.notifyObservers();
    bot.searchCount = 0;
  }
}
