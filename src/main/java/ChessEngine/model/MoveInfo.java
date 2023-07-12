package ChessEngine.model;

public class MoveInfo {
  public long[] pieceBitboards;
  public char[] charBoard;
  public boolean WK;
  public boolean WQ;
  public boolean BK;
  public boolean BQ;
  public boolean currentTurn;
  public long enPassantSquare;
  public int moveCount;

  public MoveInfo() {}
}
