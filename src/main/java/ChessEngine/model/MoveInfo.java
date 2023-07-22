package ChessEngine.model;

import java.util.List;
import java.util.Map;

public class MoveInfo {
  public long[] pieceBitboards;
  public char[] charBoard;
  public List<Move> legalMoves;
  public long zobristKey;
  public boolean WK;
  public boolean WQ;
  public boolean BK;
  public boolean BQ;
  public boolean currentTurn;
  public long enPassantSquare;
  public int moveCount;
  public int materialCount;
  public int squareBonuses;
  public Map<Long, Integer> boardState;
  public long pawnHashKey;
  public long kingPawnProximityHashKey;
  public boolean hasWhiteCastled;
  public boolean hasBlackCastled;

  public MoveInfo() {}
}
