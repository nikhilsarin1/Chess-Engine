package ChessEngine.model;

import ChessEngine.AI.TranspositionTable;
import ChessEngine.AI.Zobrist;

import java.util.*;

public class Model {
  private final Bitboard bitboard;
  private final List<ModelObserver> observers;
  private final Stack<MoveInfo> moveStack;
  public Map<Long, Integer> boardState;
  public boolean searching;
  public TranspositionTable transpositionTable;
  public HashMap<Long, Integer> pawnTable;
  public long pawnHashKey;
  public boolean hasWhiteCastled;
  public boolean hasBlackCastled;
  private boolean currentTurn;
  private boolean selectedPlayer;
  private int lastMoveOrigin;
  private int lastMoveDestination;
  private char promotedPiece;
  private int moveCount;
  private long zobristKey;

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
    Zobrist.getInstance(bitboard);
    this.zobristKey = Zobrist.getZobristKey();
    this.pawnHashKey = Zobrist.getPawnHashKey();
    this.transpositionTable = new TranspositionTable();
    this.pawnTable = new HashMap<>();
    this.hasWhiteCastled = false;
    this.hasBlackCastled = false;
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

  public long getZobristKey() {
    return zobristKey;
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

  public void movePiece(Move move, boolean isActualMove, int depth) {
    MoveInfo moveInfo = new MoveInfo();
    moveInfo.pieceBitboards =
        Arrays.copyOf(bitboard.pieceBitboards, bitboard.pieceBitboards.length);
    moveInfo.charBoard = Arrays.copyOf(getBitboard().charBoard, getBitboard().charBoard.length);
    moveInfo.legalMoves = new ArrayList<>(bitboard.getLegalMoves());
    moveInfo.zobristKey = zobristKey;
    moveInfo.WK = bitboard.whiteKingSide;
    moveInfo.WQ = bitboard.whiteQueenSide;
    moveInfo.BK = bitboard.blackKingSide;
    moveInfo.BQ = bitboard.blackQueenSide;
    moveInfo.currentTurn = currentTurn;
    moveInfo.moveCount = moveCount;
    moveInfo.materialCount = bitboard.materialCount;
    moveInfo.squareBonuses = bitboard.squareBonuses;
    moveInfo.boardState = new HashMap<>(boardState);
    moveInfo.pawnHashKey = pawnHashKey;
    moveInfo.hasWhiteCastled = hasWhiteCastled;
    moveInfo.hasBlackCastled = hasBlackCastled;
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
          hasWhiteCastled = true;
          long removeRook = bitboard.convertIntToBitboard(63);
          long addRook = bitboard.convertIntToBitboard(61);
          bitboard.pieceBitboards[2] ^= removeRook;
          bitboard.pieceBitboards[2] |= addRook;
          bitboard.charBoard[63] = ' ';
          bitboard.charBoard[61] = 'R';
        } else if (move.isQueenSideCastle()) {
          hasWhiteCastled = true;
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
          hasBlackCastled = true;
          long removeRook = bitboard.convertIntToBitboard(7);
          long addRook = bitboard.convertIntToBitboard(5);
          bitboard.pieceBitboards[8] ^= removeRook;
          bitboard.pieceBitboards[8] |= addRook;
          bitboard.charBoard[7] = ' ';
          bitboard.charBoard[5] = 'r';
        } else if (move.isQueenSideCastle()) {
          hasBlackCastled = true;
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

    updateZobristKey(move, moveInfo);
    bitboard.updateMaterialCount(move);
    bitboard.updateSquareBonuses(move);
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
    bitboard.generateLegalMoves(depth);
    boardState.put(zobristKey, boardState.getOrDefault(zobristKey, 0) + 1);

    if (isActualMove) {
      notifyObservers();
      //      transpositionTable.clear();
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
    bitboard.legalMoves = moveInfo.legalMoves;
    zobristKey = moveInfo.zobristKey;
    bitboard.materialCount = moveInfo.materialCount;
    bitboard.squareBonuses = moveInfo.squareBonuses;
    boardState = moveInfo.boardState;
    pawnHashKey = moveInfo.pawnHashKey;
    hasWhiteCastled = moveInfo.hasWhiteCastled;
    hasBlackCastled = moveInfo.hasBlackCastled;
  }

  public void updateZobristKey(Move move, MoveInfo moveInfo) {
    int origin = move.getOrigin();
    int destination = move.getDestination();
    char piece = move.getPiece();
    char capturedPiece = move.getCapturedPiece();

    switch (piece) {
      case 'K' -> {
        zobristKey ^= Zobrist.board[0][origin];
        zobristKey ^= Zobrist.board[0][destination];
        if (move.isKingSideCastle()) {
          zobristKey ^= Zobrist.board[2][63];
          zobristKey ^= Zobrist.board[2][61];
        } else if (move.isQueenSideCastle()) {
          zobristKey ^= Zobrist.board[2][56];
          zobristKey ^= Zobrist.board[2][59];
        }
      }
      case 'Q' -> {
        zobristKey ^= Zobrist.board[1][origin];
        zobristKey ^= Zobrist.board[1][destination];
      }
      case 'R' -> {
        zobristKey ^= Zobrist.board[2][origin];
        zobristKey ^= Zobrist.board[2][destination];
      }
      case 'B' -> {
        zobristKey ^= Zobrist.board[3][origin];
        zobristKey ^= Zobrist.board[3][destination];
      }
      case 'N' -> {
        zobristKey ^= Zobrist.board[4][origin];
        zobristKey ^= Zobrist.board[4][destination];
      }
      case 'P' -> {
        zobristKey ^= Zobrist.board[5][origin];
        pawnHashKey ^= Zobrist.board[5][origin];
        if (move.getPromotion() != ' ') {
          switch (move.getPromotion()) {
            case 'Q' -> zobristKey ^= Zobrist.board[1][destination];
            case 'R' -> zobristKey ^= Zobrist.board[2][destination];
            case 'B' -> zobristKey ^= Zobrist.board[3][destination];
            case 'N' -> zobristKey ^= Zobrist.board[4][destination];
          }
        } else {
          zobristKey ^= Zobrist.board[5][destination];
          pawnHashKey ^= Zobrist.board[5][destination];
        }
      }
      case 'k' -> {
        zobristKey ^= Zobrist.board[6][origin];
        zobristKey ^= Zobrist.board[6][destination];
        if (move.isKingSideCastle()) {
          zobristKey ^= Zobrist.board[8][7];
          zobristKey ^= Zobrist.board[8][5];
        } else if (move.isQueenSideCastle()) {
          zobristKey ^= Zobrist.board[8][0];
          zobristKey ^= Zobrist.board[8][3];
        }
      }
      case 'q' -> {
        zobristKey ^= Zobrist.board[7][origin];
        zobristKey ^= Zobrist.board[7][destination];
      }
      case 'r' -> {
        zobristKey ^= Zobrist.board[8][origin];
        zobristKey ^= Zobrist.board[8][destination];
      }
      case 'b' -> {
        zobristKey ^= Zobrist.board[9][origin];
        zobristKey ^= Zobrist.board[9][destination];
      }
      case 'n' -> {
        zobristKey ^= Zobrist.board[10][origin];
        zobristKey ^= Zobrist.board[10][destination];
      }
      case 'p' -> {
        zobristKey ^= Zobrist.board[11][origin];
        pawnHashKey ^= Zobrist.board[11][origin];
        if (move.getPromotion() != ' ') {
          switch (move.getPromotion()) {
            case 'q' -> zobristKey ^= Zobrist.board[7][destination];
            case 'r' -> zobristKey ^= Zobrist.board[8][destination];
            case 'b' -> zobristKey ^= Zobrist.board[9][destination];
            case 'n' -> zobristKey ^= Zobrist.board[10][destination];
          }
        } else {
          zobristKey ^= Zobrist.board[11][destination];
          pawnHashKey ^= Zobrist.board[11][destination];
        }
      }
    }

    if (!move.isEnPassant()) {
      switch (capturedPiece) {
        case 'K' -> zobristKey ^= Zobrist.board[0][destination];
        case 'Q' -> zobristKey ^= Zobrist.board[1][destination];
        case 'R' -> zobristKey ^= Zobrist.board[2][destination];
        case 'B' -> zobristKey ^= Zobrist.board[3][destination];
        case 'N' -> zobristKey ^= Zobrist.board[4][destination];
        case 'P' -> {
          zobristKey ^= Zobrist.board[5][destination];
          pawnHashKey ^= Zobrist.board[5][destination];
        }
        case 'k' -> zobristKey ^= Zobrist.board[6][destination];
        case 'q' -> zobristKey ^= Zobrist.board[7][destination];
        case 'r' -> zobristKey ^= Zobrist.board[8][destination];
        case 'b' -> zobristKey ^= Zobrist.board[9][destination];
        case 'n' -> zobristKey ^= Zobrist.board[10][destination];
        case 'p' -> {
          pawnHashKey ^= Zobrist.board[11][destination];
          zobristKey ^= Zobrist.board[11][destination];
        }
      }
    } else {
      switch (capturedPiece) {
        case 'P' -> {
          zobristKey ^= Zobrist.board[5][destination - 8];
          pawnHashKey ^= Zobrist.board[5][destination - 8];
        }
        case 'p' -> {
          zobristKey ^= Zobrist.board[11][destination + 8];
          pawnHashKey ^= Zobrist.board[11][destination + 8];
        }
      }
    }

    if (bitboard.getEnPassantSquare() != moveInfo.enPassantSquare) {
      if (bitboard.getEnPassantSquare() != 0) {
        int position = bitboard.convertBitboardToInt(bitboard.getEnPassantSquare());
        zobristKey ^= Zobrist.enPassant[position % 8];
      }
      if (moveInfo.enPassantSquare != 0) {
        int position = bitboard.convertBitboardToInt(moveInfo.enPassantSquare);
        zobristKey ^= Zobrist.enPassant[position % 8];
      }
    }

    if (bitboard.whiteKingSide != moveInfo.WK) {
      zobristKey ^= Zobrist.castle[0];
    }
    if (bitboard.whiteQueenSide != moveInfo.WQ) {
      zobristKey ^= Zobrist.castle[1];
    }
    if (bitboard.blackKingSide != moveInfo.BK) {
      zobristKey ^= Zobrist.castle[2];
    }
    if (bitboard.blackQueenSide != moveInfo.BQ) {
      zobristKey ^= Zobrist.castle[3];
    }

    zobristKey ^= Zobrist.turn;
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
    if (!boardState.containsKey(zobristKey)) {
      return false;
    }
    return boardState.get(zobristKey) >= 3;
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
