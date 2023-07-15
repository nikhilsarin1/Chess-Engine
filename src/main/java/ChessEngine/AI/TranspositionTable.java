package ChessEngine.AI;

import ChessEngine.model.Move;

import java.util.HashMap;
import java.util.Map;

public class TranspositionTable {
  private final Map<Long, TranspositionEntry> table;

  public TranspositionTable() {
    this.table = new HashMap<>();
  }

  public void storePosition(
      long zobristKey, int depth, int score, TranspositionEntry.Flag flag, Move bestMove) {
    TranspositionEntry entry = new TranspositionEntry(depth, score, flag, bestMove);
    table.put(zobristKey, entry);
  }

  public TranspositionEntry getPosition(long zobristKey) {
    return table.get(zobristKey);
  }
}
