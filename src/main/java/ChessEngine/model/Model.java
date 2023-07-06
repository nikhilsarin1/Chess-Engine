package ChessEngine.model;

import java.util.*;

public class Model {
  private final Bitboard bitboard;
  private final List<ModelObserver> observers;
  private boolean currentTurn;
  private boolean selectedPlayer;
  private int lastMoveOrigin;
  private int lastMoveDestination;
  private Map<Character, List<Move>> legalMoves;
  private char promotedPiece;
  private int moveCount;
  private Map<String, Integer> boardState;
  private Stack<MoveInfo> moveStack;

  public Model() {
    this.bitboard = new Bitboard();
    this.observers = new ArrayList<>();
    this.currentTurn = true;
    this.promotedPiece = ' ';
    this.moveCount = 0;
    this.boardState = new HashMap<>();
    this.moveStack = new Stack<>();
    this.selectedPlayer = true;
    generateLegalMoves();
  }

  public Bitboard getBitboard() {
    return bitboard;
  }

  public boolean getCurrentTurn() {
    return currentTurn;
  }

  public int getLastMoveOrigin() {
    return lastMoveOrigin;
  }

  public int getLastMoveDestination() {
    return lastMoveDestination;
  }

  public boolean getSelectedPlayer() {
    return selectedPlayer;
  }

  public void setSelectedPlayer(boolean color) {
    selectedPlayer = color;
  }

  public Map<Character, List<Move>> getLegalMoves() {
    return this.legalMoves;
  }

  public void setPromotedPiece(char piece) {
    promotedPiece = piece;
  }

  public void changeTurn() {
    currentTurn = !currentTurn;
  }

  public void generateLegalMoves() {
    Map<Character, List<Move>> legalMoves = new HashMap<>();
    Map<Character, List<Move>> possibleMoves = bitboard.getPossibleMoves();

    for (Map.Entry<Character, List<Move>> entry : possibleMoves.entrySet()) {
      List<Move> pieceLegalMoves = new ArrayList<>();
      Character piece = entry.getKey();
      List<Move> pieceMoves = entry.getValue();
      for (Move pieceMove : pieceMoves) {
        if (notInCheck(pieceMove)) {
          pieceLegalMoves.add(pieceMove);
        }
      }
      if (!pieceLegalMoves.isEmpty()) {
        legalMoves.put(piece, pieceLegalMoves);
      }
    }
    this.legalMoves = legalMoves;
  }

  public boolean isMoveValid(int origin, int destination, char piece) {
    if (legalMoves.get(piece) == null) {
      return false;
    }
    List<Move> pieceMoves = legalMoves.get(piece);
    for (Move move : pieceMoves) {
      if (move.getOrigin() == origin && move.getDestination() == destination) {
        return true;
      }
    }
    return false;
  }

  public void movePiece(Move move, boolean isActualMove) {
    MoveInfo moveInfo = new MoveInfo();
    moveInfo.pieceBitboards = Arrays.copyOf(bitboard.pieceBitboards, bitboard.pieceBitboards.length);
    moveInfo.WK = bitboard.whiteKingSide;
    moveInfo.WQ = bitboard.whiteQueenSide;
    moveInfo.BK = bitboard.blackKingSide;
    moveInfo.BQ = bitboard.blackQueenSide;
    moveInfo.currentTurn = currentTurn;
    moveInfo.moveCount = moveCount;
    int origin = move.getOrigin();
    int destination = move.getDestination();
    char piece = move.getPiece();

    long originBitboard = bitboard.convertIntToBitboard(origin);
    long destinationBitboard = bitboard.convertIntToBitboard(destination);
    moveInfo.enPassantSquare = bitboard.getEnPassantSquare();
    bitboard.setEnPassantSquare(0L);
    boolean resetMoveCount = false;

    for (int i = 0; i < bitboard.pieceBitboards.length; i++) {
      if ((bitboard.pieceBitboards[i] & destinationBitboard) != 0) {
        bitboard.pieceBitboards[i] ^= destinationBitboard;
        resetMoveCount = true;
        break; // Exit the loop since only one piece can be captured
      }
    }

    switch (piece) {
      case 'K' -> {
        bitboard.pieceBitboards[0] ^= originBitboard;
        bitboard.pieceBitboards[0] |= destinationBitboard;

          bitboard.whiteKingSide = false;
          bitboard.whiteQueenSide = false;

        if (move.isKingSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(63);
          long addRook = bitboard.convertIntToBitboard(61);
          bitboard.pieceBitboards[2] ^= removeRook;
          bitboard.pieceBitboards[2] |= addRook;
        } else if (move.isQueenSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(56);
          long addRook = bitboard.convertIntToBitboard(59);
          bitboard.pieceBitboards[2] ^= removeRook;
          bitboard.pieceBitboards[2] |= addRook;
        }
      }
      case 'Q' -> {
        bitboard.pieceBitboards[1] ^= originBitboard;
        bitboard.pieceBitboards[1] |= destinationBitboard;
      }
      case 'R' -> {
        bitboard.pieceBitboards[2] ^= originBitboard;
        bitboard.pieceBitboards[2] |= destinationBitboard;
        if (origin == 63) {
          bitboard.whiteKingSide = false;
        } else if (origin == 56) {
          bitboard.whiteQueenSide = false;
        }
      }
      case 'B' -> {
        bitboard.pieceBitboards[3] ^= originBitboard;
        bitboard.pieceBitboards[3] |= destinationBitboard;
      }
      case 'N' -> {
        bitboard.pieceBitboards[4] ^= originBitboard;
        bitboard.pieceBitboards[4] |= destinationBitboard;
      }
      case 'P' -> {
        resetMoveCount = true;
        bitboard.pieceBitboards[5] ^= originBitboard;
        bitboard.pieceBitboards[5] |= destinationBitboard;
        if (Math.abs(destination - origin) == 16) {
          bitboard.setEnPassantSquare(originBitboard << 8);
        }
        if (move.isEnPassant()) {
          bitboard.pieceBitboards[11] ^= destinationBitboard >> 8;
        }
        if (move.getPromotion() != ' ') {
          promotedPiece = move.getPromotion();
          if (isActualMove) {
            notifyPromotion(destination);
          }
          switch (promotedPiece) {
            case 'Q' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[1] |= destinationBitboard;
            }
            case 'R' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[2] |= destinationBitboard;
            }
            case 'B' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[3] |= destinationBitboard;
            }
            case 'N' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[4] |= destinationBitboard;
            }
          }
          promotedPiece = ' ';
        }
      }
      case 'k' -> {
        bitboard.pieceBitboards[6] ^= originBitboard;
        bitboard.pieceBitboards[6] |= destinationBitboard;

          bitboard.blackKingSide = false;
          bitboard.blackQueenSide = false;

        if (move.isKingSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(7);
          long addRook = bitboard.convertIntToBitboard(5);
          bitboard.pieceBitboards[8] ^= removeRook;
          bitboard.pieceBitboards[8] |= addRook;
        } else if (move.isQueenSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(0);
          long addRook = bitboard.convertIntToBitboard(3);
          bitboard.pieceBitboards[8] ^= removeRook;
          bitboard.pieceBitboards[8] |= addRook;
        }
      }
      case 'q' -> {
        bitboard.pieceBitboards[7] ^= originBitboard;
        bitboard.pieceBitboards[7] |= destinationBitboard;
      }
      case 'r' -> {
        bitboard.pieceBitboards[8] ^= originBitboard;
        bitboard.pieceBitboards[8] |= destinationBitboard;
        if (origin == 7) {
          bitboard.blackKingSide = false;
        } else if (origin == 0) {
          bitboard.blackQueenSide = false;
        }
      }
      case 'b' -> {
        bitboard.pieceBitboards[9] ^= originBitboard;
        bitboard.pieceBitboards[9] |= destinationBitboard;
      }
      case 'n' -> {
        bitboard.pieceBitboards[10] ^= originBitboard;
        bitboard.pieceBitboards[10] |= destinationBitboard;
      }
      case 'p' -> {
        resetMoveCount = true;
        bitboard.pieceBitboards[11] ^= originBitboard;
        bitboard.pieceBitboards[11] |= destinationBitboard;
        if (Math.abs(destination - origin) == 16) {
          bitboard.setEnPassantSquare(originBitboard >> 8);
        }
        if (move.isEnPassant()) {
          bitboard.pieceBitboards[5] ^= destinationBitboard << 8;
        }
        if (move.getPromotion() != ' ') {
          promotedPiece = move.getPromotion();
          if (isActualMove) {
            notifyPromotion(destination);
          }
          switch (promotedPiece) {
            case 'q' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[7] |= destinationBitboard;
            }
            case 'r' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[8] |= destinationBitboard;
            }
            case 'b' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[9] |= destinationBitboard;
            }
            case 'n' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[10] |= destinationBitboard;
            }
          }
          promotedPiece = ' ';
        }
      }
    }
    moveStack.push(moveInfo);

    bitboard.updateBitboard();
    if (isActualMove) {
      if (resetMoveCount) {
        moveCount = 0;
      } else {
        moveCount++;
      }
      String str = generateBoardState();
      boardState.put(str, boardState.getOrDefault(str, 0) + 1);
      lastMoveOrigin = origin;
      lastMoveDestination = destination;
      changeTurn();
      bitboard.changeTurn();
      bitboard.updateBitboard();
      bitboard.generatePossibleMoves();
      generateLegalMoves();
      notifyObservers();
    }
  }

  public void undoMove() {
    MoveInfo moveInfo = moveStack.pop();
    bitboard.pieceBitboards = moveInfo.pieceBitboards;
    bitboard.whiteKingSide = moveInfo.WK;
    bitboard.whiteQueenSide = moveInfo.WQ;
    bitboard.blackKingSide = moveInfo.BK;
    bitboard.blackQueenSide = moveInfo.BQ;
    bitboard.setEnPassantSquare(moveInfo.enPassantSquare);
    moveCount = moveInfo.moveCount;
    currentTurn = moveInfo.currentTurn;
    bitboard.currentTurn = currentTurn;
    bitboard.updateBitboard();
    bitboard.generatePossibleMoves();
    generateLegalMoves();
  }

  public boolean isCheck() {
    long king = currentTurn ? bitboard.pieceBitboards[0] : bitboard.pieceBitboards[6];
    return (bitboard.attackMap & king) != 0L;
  }

  public boolean notInCheck(Move pieceMove) {
    long[] savedPieceBitboards =
        Arrays.copyOf(bitboard.pieceBitboards, bitboard.pieceBitboards.length);
    boolean saveWK = bitboard.whiteKingSide;
    boolean saveWQ = bitboard.whiteQueenSide;
    boolean saveBK = bitboard.blackKingSide;
    boolean saveBQ = bitboard.blackQueenSide;
    long saveEnPassantSquare = bitboard.getEnPassantSquare();

    boolean check;

    movePiece(pieceMove, false);
    check = !isCheck();
    bitboard.pieceBitboards = savedPieceBitboards;
    bitboard.whiteKingSide = saveWK;
    bitboard.whiteQueenSide = saveWQ;
    bitboard.blackKingSide = saveBK;
    bitboard.blackQueenSide = saveBQ;
    bitboard.setEnPassantSquare(saveEnPassantSquare);
    bitboard.updateBitboard();
    moveStack.pop();
    return check;
  }

  public boolean isCheckmate() {
    if (!isCheck()) {
      return false;
    }
    return legalMoves.isEmpty();
  }

  public boolean isDraw() {
    return isStalemate()
            || isFiftyMoveDraw()
            || isInsufficientMaterial()
            || isThreeFoldRepetition();
  }

  public boolean isStalemate() {
    if (isCheck()) {
      return false;
    }
    return legalMoves.isEmpty();
  }

  public boolean isFiftyMoveDraw() {
    return moveCount >= 100;
  }

  public boolean isInsufficientMaterial() {
    if (bitboard.pieceBitboards[1] != 0L
        | bitboard.pieceBitboards[2] != 0L
        | bitboard.pieceBitboards[5] != 0L
        | bitboard.pieceBitboards[7] != 0L
        | bitboard.pieceBitboards[8] != 0L
        | bitboard.pieceBitboards[11] != 0L) {
      return false;
    }
    return (Long.bitCount(bitboard.pieceBitboards[3]) + Long.bitCount(bitboard.pieceBitboards[4]) <= 1)
            && (Long.bitCount(bitboard.pieceBitboards[9]) + Long.bitCount(bitboard.pieceBitboards[10]))
            <= 1;
  }

  public boolean isThreeFoldRepetition() {
    for (int count : boardState.values()) {
      if (count >= 3) {
        return true;
      }
    }
    return false;
  }

  public String generateBoardState() {
    char[] board = bitboard.convertBitboardsToCharArray(bitboard.pieceBitboards);
    String str = new String(board);
    return str;
  }

  public void addObserver(ModelObserver observer) {
    observers.add(observer);
  }

  public void notifyObservers() {
    for (ModelObserver observer : observers) {
      observer.updateMove(this);
    }
  }

  public void notifyPromotion(int promotionSquare) {
    // Notify all observers of a new promoted pawn
    for (ModelObserver observer : observers) {
      observer.updatePromotion(promotionSquare);
    }
  }
}
