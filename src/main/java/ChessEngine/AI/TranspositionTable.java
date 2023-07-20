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
      // Consider looking at replacing entries that are not exact
      long zobristKey, int ply, int score, TranspositionEntry.Flag flag, Move bestMove) {
    TranspositionEntry newEntry = new TranspositionEntry(ply, score, flag, bestMove);
    if (table.containsKey(zobristKey)) {
      TranspositionEntry oldEntry = table.get(zobristKey);
      if (newEntry.ply() >= oldEntry.ply()) {
        table.put(zobristKey, newEntry);
      }
    } else {
      table.put(zobristKey, newEntry);
    }
  }

  public TranspositionEntry getPosition(long zobristKey) {
    return table.getOrDefault(zobristKey, null);
  }
}
