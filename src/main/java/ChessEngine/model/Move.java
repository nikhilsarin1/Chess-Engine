package ChessEngine.model;

public class Move {
  private final int origin;
  private final int destination;
  private final char piece;
  private boolean kingSideCastle;
  private boolean queenSideCastle;
  private boolean enPassant;
  private char promotion;

  public Move(int origin, int destination, char piece) {
    this.origin = origin;
    this.destination = destination;
    this.piece = piece;
    this.kingSideCastle = false;
    this.queenSideCastle = false;
    this.enPassant = false;
    this.promotion = ' ';
  }

  public Move(
      int origin,
      int destination,
      char piece,
      boolean kingSideCastle,
      boolean queenSideCastle,
      boolean enPassant,
      char promotion) {
    this(origin, destination, piece);
    this.kingSideCastle = kingSideCastle;
    this.queenSideCastle = queenSideCastle;
    this.enPassant = enPassant;
    this.promotion = promotion;
  }

  public int getOrigin() {
    return origin;
  }

  public int getDestination() {
    return destination;
  }

  public char getPiece() {
    return piece;
  }

  public boolean isKingSideCastle() {
    return kingSideCastle;
  }

  public boolean isQueenSideCastle() {
    return queenSideCastle;
  }

  public boolean isEnPassant() {
    return enPassant;
  }

  public char getPromotion() {
    return promotion;
  }
}
