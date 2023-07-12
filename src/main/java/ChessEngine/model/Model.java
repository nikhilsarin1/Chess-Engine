package ChessEngine.model;

import java.util.*;

public class Model {
  private final Bitboard bitboard;
  private final List<ModelObserver> observers;
  private final Stack<MoveInfo> moveStack;
  public Map<String, Integer> boardState;
  public boolean searching;
  private boolean currentTurn;
  private boolean selectedPlayer;
  private int lastMoveOrigin;
  private int lastMoveDestination;
  private char promotedPiece;
  private int moveCount;

  public Model(String fen) {
    this.bitboard = new Bitboard(fen);
    this.observers = new ArrayList<>();
    this.currentTurn = bitboard.currentTurn;
    this.promotedPiece = ' ';
    this.moveCount = 0;
    this.boardState = new HashMap<>();
    this.moveStack = new Stack<>();
    this.selectedPlayer = true;
    this.searching = false;
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

  public void setPromotedPiece(char piece) {
    promotedPiece = piece;
  }

  public void changeTurn() {
    currentTurn = !currentTurn;
  }

  public boolean isMoveValid(int origin, int destination) {
    for (Move move : this.getBitboard().getLegalMoves()) {
      if (move.getOrigin() == origin && move.getDestination() == destination) {
        return true;
      }
    }
    return false;
  }

  public void movePiece(Move move, boolean isActualMove) {
    MoveInfo moveInfo = new MoveInfo();
    moveInfo.pieceBitboards =
        Arrays.copyOf(bitboard.pieceBitboards, bitboard.pieceBitboards.length);
    moveInfo.charBoard = Arrays.copyOf(getBitboard().charBoard, getBitboard().charBoard.length);
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

    bitboard.charBoard[origin] = ' ';
    bitboard.charBoard[destination] = piece;

    if (move.getCapturedPiece() != ' ' && !move.isEnPassant()) {
      resetMoveCount = true;
      switch (move.getCapturedPiece()) {
        case 'K' -> bitboard.pieceBitboards[0] ^= destinationBitboard;
        case 'Q' -> bitboard.pieceBitboards[1] ^= destinationBitboard;
        case 'R' -> bitboard.pieceBitboards[2] ^= destinationBitboard;
        case 'B' -> bitboard.pieceBitboards[3] ^= destinationBitboard;
        case 'N' -> bitboard.pieceBitboards[4] ^= destinationBitboard;
        case 'P' -> bitboard.pieceBitboards[5] ^= destinationBitboard;
        case 'k' -> bitboard.pieceBitboards[6] ^= destinationBitboard;
        case 'q' -> bitboard.pieceBitboards[7] ^= destinationBitboard;
        case 'r' -> bitboard.pieceBitboards[8] ^= destinationBitboard;
        case 'b' -> bitboard.pieceBitboards[9] ^= destinationBitboard;
        case 'n' -> bitboard.pieceBitboards[10] ^= destinationBitboard;
        case 'p' -> bitboard.pieceBitboards[11] ^= destinationBitboard;
      }
    }

    switch (destination) {
      case 63 -> bitboard.whiteKingSide = false;
      case 56 -> bitboard.whiteQueenSide = false;
      case 7 -> bitboard.blackKingSide = false;
      case 0 -> bitboard.blackQueenSide = false;
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
          bitboard.charBoard[63] = ' ';
          bitboard.charBoard[61] = 'R';
        } else if (move.isQueenSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(56);
          long addRook = bitboard.convertIntToBitboard(59);
          bitboard.pieceBitboards[2] ^= removeRook;
          bitboard.pieceBitboards[2] |= addRook;
          bitboard.charBoard[56] = ' ';
          bitboard.charBoard[59] = 'R';
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
          bitboard.charBoard[destination + 8] = ' ';
        }
        if (move.getPromotion() != ' ') {
          promotedPiece = move.getPromotion();
          if (isActualMove && !searching) {
            notifyPromotion(destination);
          }
          switch (promotedPiece) {
            case 'Q' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[1] |= destinationBitboard;
              bitboard.charBoard[destination] = 'Q';
            }
            case 'R' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[2] |= destinationBitboard;
              bitboard.charBoard[destination] = 'R';
            }
            case 'B' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[3] |= destinationBitboard;
              bitboard.charBoard[destination] = 'B';
            }
            case 'N' -> {
              bitboard.pieceBitboards[5] ^= destinationBitboard;
              bitboard.pieceBitboards[4] |= destinationBitboard;
              bitboard.charBoard[destination] = 'N';
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
          bitboard.charBoard[7] = ' ';
          bitboard.charBoard[5] = 'r';
        } else if (move.isQueenSideCastle()) {
          long removeRook = bitboard.convertIntToBitboard(0);
          long addRook = bitboard.convertIntToBitboard(3);
          bitboard.pieceBitboards[8] ^= removeRook;
          bitboard.pieceBitboards[8] |= addRook;
          bitboard.charBoard[0] = ' ';
          bitboard.charBoard[3] = 'r';
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
          bitboard.charBoard[destination - 8] = ' ';
        }
        if (move.getPromotion() != ' ') {
          promotedPiece = move.getPromotion();
          if (isActualMove & !searching) {
            notifyPromotion(destination);
          }
          switch (promotedPiece) {
            case 'q' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[7] |= destinationBitboard;
              bitboard.charBoard[destination] = 'q';
            }
            case 'r' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[8] |= destinationBitboard;
              bitboard.charBoard[destination] = 'r';
            }
            case 'b' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[9] |= destinationBitboard;
              bitboard.charBoard[destination] = 'b';
            }
            case 'n' -> {
              bitboard.pieceBitboards[11] ^= destinationBitboard;
              bitboard.pieceBitboards[10] |= destinationBitboard;
              bitboard.charBoard[destination] = 'n';
            }
          }
          promotedPiece = ' ';
        }
      }
    }

    if (isActualMove) {
      moveStack.push(moveInfo);
      if (resetMoveCount) {
        moveCount = 0;
      } else {
        moveCount++;
      }
      lastMoveOrigin = origin;
      lastMoveDestination = destination;
      changeTurn();
      bitboard.changeTurn();
      bitboard.updateBitboard();
      bitboard.generateLegalMoves();
    }
  }

  public void undoMove() {
    MoveInfo moveInfo = moveStack.pop();
    bitboard.pieceBitboards = moveInfo.pieceBitboards;
    bitboard.charBoard = moveInfo.charBoard;
    bitboard.whiteKingSide = moveInfo.WK;
    bitboard.whiteQueenSide = moveInfo.WQ;
    bitboard.blackKingSide = moveInfo.BK;
    bitboard.blackQueenSide = moveInfo.BQ;
    bitboard.setEnPassantSquare(moveInfo.enPassantSquare);
    moveCount = moveInfo.moveCount;
    currentTurn = moveInfo.currentTurn;
    bitboard.currentTurn = currentTurn;
    bitboard.updateBitboard();
    bitboard.generateLegalMoves();
  }

  public boolean isCheck() {
    long king = currentTurn ? bitboard.pieceBitboards[0] : bitboard.pieceBitboards[6];
    return (bitboard.attackMap & king) != 0L;
  }

  public boolean isCheckmate() {
    if (!isCheck()) {
      return false;
    }
    return this.getBitboard().getLegalMoves().isEmpty();
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
    return this.getBitboard().getLegalMoves().isEmpty();
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
    return (Long.bitCount(bitboard.pieceBitboards[3]) + Long.bitCount(bitboard.pieceBitboards[4])
            <= 1)
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

  public String getMoveNotation(int origin, int destination) {
    StringBuilder move = new StringBuilder();

    switch (origin % 8) {
      case 0 -> move.append("a");
      case 1 -> move.append("b");
      case 2 -> move.append("c");
      case 3 -> move.append("d");
      case 4 -> move.append("e");
      case 5 -> move.append("f");
      case 6 -> move.append("g");
      case 7 -> move.append("h");
    }
    switch (origin / 8) {
      case 0 -> move.append("8");
      case 1 -> move.append("7");
      case 2 -> move.append("6");
      case 3 -> move.append("5");
      case 4 -> move.append("4");
      case 5 -> move.append("3");
      case 6 -> move.append("2");
      case 7 -> move.append("1");
    }
    switch (destination % 8) {
      case 0 -> move.append("a");
      case 1 -> move.append("b");
      case 2 -> move.append("c");
      case 3 -> move.append("d");
      case 4 -> move.append("e");
      case 5 -> move.append("f");
      case 6 -> move.append("g");
      case 7 -> move.append("h");
    }
    switch (destination / 8) {
      case 0 -> move.append("8");
      case 1 -> move.append("7");
      case 2 -> move.append("6");
      case 3 -> move.append("5");
      case 4 -> move.append("4");
      case 5 -> move.append("3");
      case 6 -> move.append("2");
      case 7 -> move.append("1");
    }
    return move.toString();
  }

  public String generateBoardState() {
    char[] board = bitboard.convertBitboardsToCharArray(bitboard.pieceBitboards);
    return new String(board);
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
