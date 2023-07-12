package ChessEngine.model;

public class Move {
  private final int origin;
  private final int destination;
  private final char piece;
  private final char capturedPiece;
  private boolean kingSideCastle;
  private boolean queenSideCastle;
  private boolean enPassant;
  private char promotion;

  public Move(int origin, int destination, char piece, char capturedPiece) {
    this.origin = origin;
    this.destination = destination;
    this.piece = piece;
    this.capturedPiece = capturedPiece;
    this.kingSideCastle = false;
    this.queenSideCastle = false;
    this.enPassant = false;
    this.promotion = ' ';
  }

  public Move(
      int origin,
      int destination,
      char piece,
      char capturedPiece,
      boolean kingSideCastle,
      boolean queenSideCastle,
      boolean enPassant,
      char promotion) {
    this(origin, destination, piece, capturedPiece);
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

  public char getCapturedPiece() {
    return capturedPiece;
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
