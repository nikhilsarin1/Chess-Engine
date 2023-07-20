package ChessEngine.AI;

import ChessEngine.model.Move;

public record TranspositionEntry(
    int ply, int score, ChessEngine.AI.TranspositionEntry.Flag flag, Move bestMove) {

  public enum Flag {
    EXACT,
    UPPER_BOUND,
    LOWER_BOUND
  }
}
