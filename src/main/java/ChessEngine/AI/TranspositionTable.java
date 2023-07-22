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
      long zobristKey, int ply, int score, TranspositionEntry.Flag flag, Move bestMove) {
    TranspositionEntry newEntry = new TranspositionEntry(ply, score, flag, bestMove);

    // Always replace scheme seems to work better than depth based one
    table.put(zobristKey, newEntry);
  }

  public TranspositionEntry getPosition(long zobristKey) {
    return table.getOrDefault(zobristKey, null);
  }

  public void clear() {
    table.clear();
  }
}
