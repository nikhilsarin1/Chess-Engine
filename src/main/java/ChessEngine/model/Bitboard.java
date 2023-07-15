package ChessEngine.model;

import ChessEngine.AI.MoveComparator;

import java.util.*;

public class Bitboard {
  static long[] fileMasks = {
    0x101010101010101L, // File a
    0x202020202020202L, // File b
    0x404040404040404L, // File c
    0x808080808080808L, // File d
    0x1010101010101010L, // File e
    0x2020202020202020L, // File f
    0x4040404040404040L, // File g
    0x8080808080808080L // File h
  };
  static long[] rankMasks = {
    0xFFL, // Rank 1
    0xFF00L, // Rank 2
    0xFF0000L, // Rank 3
    0xFF000000L, // Rank 4
    0xFF00000000L, // Rank 5
    0xFF0000000000L, // Rank 6
    0xFF000000000000L, // Rank 7
    0xFF00000000000000L // Rank 8
  };
  static long[] diagonalMasks = {
    0x1L, // a1
    0x102L, // a2 to b1
    0x10204L, // a3 to c1
    0x1020408L, // a4 to d1
    0x102040810L, // a5 to e1
    0x10204081020L, // a6 to f1
    0x1020408102040L, // a7 to g1
    0x102040810204080L, // a8 to h1
    0x204081020408000L, // b8 to h2
    0x408102040800000L, // c8 to h3
    0x810204080000000L, // d8 to h4
    0x1020408000000000L, // e8 to h5
    0x2040800000000000L, // f8 to h6
    0x4080000000000000L, // g8 to h7
    0x8000000000000000L // h8
  };
  static long[] antiDiagonalMasks = {
    0x80L, // h1
    0x8040L, // h2 to g1
    0x804020L, // h3 to f1
    0x80402010L, // h4 to e1
    0x8040201008L, // h5 to d1
    0x804020100804L, // h6 to c1
    0x80402010080402L, // h7 to b1
    0x8040201008040201L, // h8 to a1
    0x4020100804020100L, // g8 to a2
    0x2010080402010000L, // f8 to a3
    0x1008040201000000L, // e8 to a4
    0x804020100000000L, // d8 to a5
    0x402010000000000L, // c8 to a6
    0x201000000000000L, // b8 to a7
    0x100000000000000L // a8
  };

  static long whiteKingSideMask = 0x70L; // white king side castle squares
  static long whiteQueenSideMask = 0x1CL; // white queen side castle squares
  static long blackKingSideMask = 0x7000000000000000L; // black king side castle squares
  static long blackQueenSideMask = 0x1C00000000000000L; // black queen side castle squares
  public long[] pieceBitboards;
  public char[] charBoard;
  public long attackMap;
  public int attackingPieces;
  public long attackRay;
  public long pinnedPieces;
  public Map<Long, Character> pinnedRays;
  public boolean currentTurn;
  public boolean whiteKingSide;
  public boolean whiteQueenSide;
  public boolean blackKingSide;
  public boolean blackQueenSide;
  public List<Move> legalMoves;
  private long occupied;
  private long empty;
  private long whitePieces;
  private long blackPieces;
  private long enPassantSquare;

  public Bitboard(String fen) {
    fenConverter(fen);
    this.enPassantSquare = 0L;
    this.legalMoves = new ArrayList<>();
    updateBitboard();
    generateLegalMoves();
  }

  public void fenConverter(String fen) {
    fen = fen.replace("/", "");
    char[] board = new char[64];
    int index = 0;
    for (int i = 0; i < fen.length(); i++) {
      if (Character.isDigit(fen.charAt(i))) {
        int num = Character.getNumericValue(fen.charAt(i));
        for (int j = 0; j < num; j++) {
          board[index + j] = ' ';
        }
        index += num;
      } else {
        board[index] = fen.charAt(i);
        index++;
      }
      if (index == 64) {
        fen = fen.substring(i + 1);
        break;
      }
    }
    this.charBoard = board;
    this.pieceBitboards = convertCharArrayToBitboards(board);
    this.currentTurn = fen.contains("w");
    this.whiteKingSide = fen.contains("K");
    this.whiteQueenSide = fen.contains("Q");
    this.blackKingSide = fen.contains("k");
    this.blackQueenSide = fen.contains("q");
  }

  public void updateBitboard() {
    this.attackRay = 0xFFFFFFFFFFFFFFFFL;
    this.attackingPieces = 0;
    this.pinnedPieces = 0L;
    setWhitePieces();
    setBlackPieces();
    setOccupied();
    setEmpty();
    setEnemyAttackMap();
  }

  public void changeTurn() {
    currentTurn = !currentTurn;
  }

  public List<Move> getLegalMoves() {
    return legalMoves;
  }

  public void generateLegalMoves() {
    List<Move> legalMoves = new ArrayList<>();
    if (currentTurn) {
      if (attackingPieces == 2) {
        legalMoves.addAll(kingMoves(pieceBitboards[0], true));
      } else {
        legalMoves.addAll(kingMoves(pieceBitboards[0], true));
        legalMoves.addAll(queenMoves(pieceBitboards[1], true));
        legalMoves.addAll(rookMoves(pieceBitboards[2], true));
        legalMoves.addAll(bishopMoves(pieceBitboards[3], true));
        legalMoves.addAll(knightMoves(pieceBitboards[4], true));
        legalMoves.addAll(whitePawnMoves(pieceBitboards[5]));
      }
    } else {
      if (attackingPieces == 2) {
        legalMoves.addAll(kingMoves(pieceBitboards[6], false));
      } else {
        legalMoves.addAll(kingMoves(pieceBitboards[6], false));
        legalMoves.addAll(queenMoves(pieceBitboards[7], false));
        legalMoves.addAll(rookMoves(pieceBitboards[8], false));
        legalMoves.addAll(bishopMoves(pieceBitboards[9], false));
        legalMoves.addAll(knightMoves(pieceBitboards[10], false));
        legalMoves.addAll(blackPawnMoves(pieceBitboards[11]));
      }
    }
    legalMoves.sort(new MoveComparator());
    this.legalMoves = legalMoves;
  }

  public void setEnemyAttackMap() {
    long attackMap = 0L;
    long attackRay = 0xFFFFFFFFFFFFFFFFL;
    int attackingPieces = 0;
    long pinnedPieces = 0L;
    this.pinnedRays = new HashMap<>();

    if (currentTurn) {
      long king = pieceBitboards[0];
      if (pieceBitboards[6] != 0L) {
        attackMap |= kingMove(pieceBitboards[6], false, true);
        if ((king & kingMove(pieceBitboards[6], false, true)) != 0L) {
          attackingPieces |= pieceBitboards[6];
        }
      }
      if (pieceBitboards[7] != 0L) {
        long[] individualQueens = getIndividualPieceBitboards(pieceBitboards[7]);
        for (long queen : individualQueens) {
          attackMap |= queenMove(queen, false, true);
          pinnedPieces |= setPinnedPiece(queen, 'q');
          if ((king & queenMove(queen, false, true)) != 0L) {
            attackRay = getDirectionalMask(queen, king, false);
            attackMap |= getDirectionalMask(queen, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[8] != 0L) {
        long[] individualRooks = getIndividualPieceBitboards(pieceBitboards[8]);
        for (long rook : individualRooks) {
          attackMap |= rookMove(rook, false, true);
          pinnedPieces |= setPinnedPiece(rook, 'r');
          if ((king & rookMove(rook, false, true)) != 0L) {
            attackRay = getDirectionalMask(rook, king, false);
            attackMap |= getDirectionalMask(rook, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[9] != 0L) {
        long[] individualBishops = getIndividualPieceBitboards(pieceBitboards[9]);
        for (long bishop : individualBishops) {
          attackMap |= bishopMove(bishop, false, true);
          pinnedPieces |= setPinnedPiece(bishop, 'b');
          if ((king & bishopMove(bishop, false, true)) != 0L) {
            attackRay = getDirectionalMask(bishop, king, false);
            attackMap |= getDirectionalMask(bishop, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[10] != 0L) {
        long[] individualKnights = getIndividualPieceBitboards(pieceBitboards[10]);
        for (long knight : individualKnights) {
          attackMap |= knightMove(knight, false, true);
          if ((king & knightMove(knight, false, true)) != 0L) {
            attackRay = knight;
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[11] != 0L) {
        attackMap |= blackPawnCapture(pieceBitboards[11], true);
        long[] individualPawns = getIndividualPieceBitboards(pieceBitboards[11]);
        for (long pawn : individualPawns) {
          if ((king & blackPawnCapture(pawn, true)) != 0L) {
            attackRay = pawn;
            attackingPieces++;
          }
        }
      }
    } else {
      long king = pieceBitboards[6];
      if (pieceBitboards[0] != 0L) {
        attackMap |= kingMove(pieceBitboards[0], true, true);
        if ((king & kingMove(pieceBitboards[0], true, true)) != 0L) {
          attackingPieces |= pieceBitboards[0];
        }
      }
      if (pieceBitboards[1] != 0L) {
        long[] individualQueens = getIndividualPieceBitboards(pieceBitboards[1]);
        for (long queen : individualQueens) {
          attackMap |= queenMove(queen, true, true);
          pinnedPieces |= setPinnedPiece(queen, 'Q');
          if ((king & queenMove(queen, true, true)) != 0L) {
            attackRay = getDirectionalMask(queen, king, false);
            attackMap |= getDirectionalMask(queen, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[2] != 0L) {
        long[] individualRooks = getIndividualPieceBitboards(pieceBitboards[2]);
        for (long rook : individualRooks) {
          attackMap |= rookMove(rook, true, true);
          pinnedPieces |= setPinnedPiece(rook, 'R');
          if ((king & rookMove(rook, true, true)) != 0L) {
            attackRay = getDirectionalMask(rook, king, false);
            attackMap |= getDirectionalMask(rook, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[3] != 0L) {
        long[] individualBishops = getIndividualPieceBitboards(pieceBitboards[3]);
        for (long bishop : individualBishops) {
          attackMap |= bishopMove(bishop, true, true);
          pinnedPieces |= setPinnedPiece(bishop, 'B');
          if ((king & bishopMove(bishop, true, true)) != 0L) {
            attackRay = getDirectionalMask(bishop, king, false);
            attackMap |= getDirectionalMask(bishop, king, true);
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[4] != 0L) {
        long[] individualKnights = getIndividualPieceBitboards(pieceBitboards[4]);
        for (long knight : individualKnights) {
          attackMap |= knightMove(knight, true, true);
          if ((king & knightMove(knight, true, true)) != 0L) {
            attackRay = knight;
            attackingPieces++;
          }
        }
      }
      if (pieceBitboards[5] != 0L) {
        attackMap |= whitePawnCapture(pieceBitboards[5], true);
        long[] individualPawns = getIndividualPieceBitboards(pieceBitboards[5]);
        for (long pawn : individualPawns) {
          if ((king & whitePawnCapture(pawn, true)) != 0L) {
            attackRay = pawn;
            attackingPieces++;
          }
        }
      }
    }

    this.pinnedPieces = pinnedPieces;
    this.attackRay = attackRay;
    this.attackingPieces = attackingPieces;
    this.attackMap = attackMap;
  }

  public long setPinnedPiece(long piece, char pieceType) {
    long pinPieces = Character.isUpperCase(pieceType) ? blackPieces : whitePieces;
    long blockingPieces = Character.isUpperCase(pieceType) ? whitePieces : blackPieces;
    pieceType = Character.toLowerCase(pieceType);
    int position = Long.numberOfTrailingZeros(piece);
    long king = currentTurn ? pieceBitboards[0] : pieceBitboards[6];
    long bottomMask = (1L << Long.numberOfTrailingZeros(piece)) - 1;
    long upperMask = ~bottomMask & ~piece;
    long bottomKingMask = (1L << Long.numberOfTrailingZeros(king)) - 1;
    long upperKingMask = ~bottomKingMask & ~king;
    long diagonal = diagonalMasks[(position / 8) + (position % 8)];
    long antiDiagonal = antiDiagonalMasks[(7 + (position / 8) - (position % 8))];

    long north = (0x0101010101010100L) << position;
    long south = (0x0080808080808080L) >> (position ^ 63);
    long east = 2 * ((1L << (position | 7)) - (1L << position));
    long west = (1L << position) - (1L << (position & 56));

    long northEast = (antiDiagonal & upperMask) & ~piece;
    long northWest = (diagonal & upperMask) & ~piece;
    long southEast = (diagonal & bottomMask) & ~piece;
    long southWest = (antiDiagonal & bottomMask) & ~piece;

    if (pieceType == 'r' | pieceType == 'q') {
      if ((north & king) != 0L) {
        north &= bottomKingMask;
        if ((Long.bitCount(north & pinPieces) == 1) & (north & blockingPieces) == 0L) {
          pinnedRays.put(north & pinPieces, 'v');
          return north & pinPieces;
        }
      } else if ((south & king) != 0L) {
        south &= upperKingMask;
        if ((Long.bitCount(south & pinPieces) == 1) & (south & blockingPieces) == 0L) {
          pinnedRays.put(south & pinPieces, 'v');
          return south & pinPieces;
        }
      } else if ((east & king) != 0L) {
        east &= bottomKingMask;
        if ((Long.bitCount(east & pinPieces) == 1) & (east & blockingPieces) == 0L) {
          pinnedRays.put(east & pinPieces, 'h');
          return east & pinPieces;
        }
      } else if ((west & king) != 0L) {
        west &= upperKingMask;
        if ((Long.bitCount(west & pinPieces) == 1) & (west & blockingPieces) == 0L) {
          pinnedRays.put(west & pinPieces, 'h');
          return west & pinPieces;
        }
      }
    }
    if (pieceType == 'b' | pieceType == 'q') {
      if ((northEast & king) != 0L) {
        northEast &= bottomKingMask;
        if ((Long.bitCount(northEast & pinPieces) == 1) & (northEast & blockingPieces) == 0L) {
          pinnedRays.put(northEast & pinPieces, 'a');
          return northEast & pinPieces;
        }
      } else if ((northWest & king) != 0L) {
        northWest &= bottomKingMask;
        if ((Long.bitCount(northWest & pinPieces) == 1) & (northWest & blockingPieces) == 0L) {
          pinnedRays.put(northWest & pinPieces, 'd');
          return northWest & pinPieces;
        }
      } else if ((southEast & king) != 0L) {
        southEast &= upperKingMask;
        if ((Long.bitCount(southEast & pinPieces) == 1) & (southEast & blockingPieces) == 0L) {
          pinnedRays.put(southEast & pinPieces, 'd');
          return southEast & pinPieces;
        }
      } else if (((southWest & king) != 0L)) {
        southWest &= upperKingMask;
        if ((Long.bitCount(southWest & pinPieces) == 1) & (southWest & blockingPieces) == 0L) {
          pinnedRays.put(southWest & pinPieces, 'a');
          return southWest & pinPieces;
        }
      }
    }
    return 0L;
  }

  public long getDirectionalMask(long piece, long king, boolean isAttackMap) {
    int position = Long.numberOfTrailingZeros(piece);
    long bottomMask = (1L << Long.numberOfTrailingZeros(piece)) - 1;
    long upperMask = ~bottomMask & ~piece;
    long diagonal = diagonalMasks[(position / 8) + (position % 8)];
    long antiDiagonal = antiDiagonalMasks[(7 + (position / 8) - (position % 8))];
    long bottomKingMask = (1L << Long.numberOfTrailingZeros(king)) - 1;
    long upperKingMask = ~bottomKingMask & ~king;

    long north = (0x0101010101010100L) << position;
    long south = (0x0080808080808080L) >> (position ^ 63);
    long east = 2 * ((1L << (position | 7)) - (1L << position));
    long west = (1L << position) - (1L << (position & 56));

    long northEast = (antiDiagonal & upperMask) & ~piece;
    long northWest = (diagonal & upperMask) & ~piece;
    long southEast = (diagonal & bottomMask) & ~piece;
    long southWest = (antiDiagonal & bottomMask) & ~piece;

    if ((north & king) != 0L) {
      return isAttackMap ? north : (north | piece) & bottomKingMask & ~king;
    } else if ((south & king) != 0L) {
      return isAttackMap ? south : (south | piece) & upperKingMask & ~king;
    } else if ((east & king) != 0L) {
      return isAttackMap ? east : (east | piece) & bottomKingMask & ~king;
    } else if ((west & king) != 0L) {
      return isAttackMap ? west : (west | piece) & upperKingMask & ~king;
    } else if ((northEast & king) != 0L) {
      return isAttackMap ? northEast : (northEast | piece) & bottomKingMask & ~king;
    } else if ((northWest & king) != 0L) {
      return isAttackMap ? northWest : (northWest | piece) & bottomKingMask & ~king;
    } else if ((southEast & king) != 0L) {
      return isAttackMap ? southEast : (southEast | piece) & upperKingMask & ~king;
    } else if ((southWest & king) != 0L) {
      return isAttackMap ? southWest : (southWest | piece) & upperKingMask & ~king;
    } else return 0L;
  }

  public long convertIntToBitboard(int position) {
    return 1L << ((7 - (position / 8)) * 8 + (position % 8));
  }

  public boolean isOccupiedSquare(int square) {
    long bitPosition = convertIntToBitboard(square);
    return (bitPosition & occupied) != 0L;
  }

  public char[] convertBitboardsToCharArray(long[] pieceBitboards) {
    char[] board = new char[64];
    for (int i = 0; i < 64; i++) {
      board[i] = ' ';
    }

    for (int i = 0; i < 64; i++) {
      int index = (7 - (i / 8)) * 8 + (i % 8);
      if ((pieceBitboards[0] >> i & 1) == 1) {
        board[index] = 'K';
      }
      if ((pieceBitboards[1] >> i & 1) == 1) {
        board[index] = 'Q';
      }
      if ((pieceBitboards[2] >> i & 1) == 1) {
        board[index] = 'R';
      }
      if ((pieceBitboards[3] >> i & 1) == 1) {
        board[index] = 'B';
      }
      if ((pieceBitboards[4] >> i & 1) == 1) {
        board[index] = 'N';
      }
      if ((pieceBitboards[5] >> i & 1) == 1) {
        board[index] = 'P';
      }
      if ((pieceBitboards[6] >> i & 1) == 1) {
        board[index] = 'k';
      }
      if ((pieceBitboards[7] >> i & 1) == 1) {
        board[index] = 'q';
      }
      if ((pieceBitboards[8] >> i & 1) == 1) {
        board[index] = 'r';
      }
      if ((pieceBitboards[9] >> i & 1) == 1) {
        board[index] = 'b';
      }
      if ((pieceBitboards[10] >> i & 1) == 1) {
        board[index] = 'n';
      }
      if ((pieceBitboards[11] >> i & 1) == 1) {
        board[index] = 'p';
      }
    }

    return board;
  }

  public long[] convertCharArrayToBitboards(char[] board) {
    long[] pieceBitboards = new long[12];

    for (int i = 0; i < 64; i++) {
      long bitPosition = 1L << ((7 - (i / 8)) * 8 + (i % 8));
      switch (board[(i / 8) * 8 + (i % 8)]) {
        case 'K' -> pieceBitboards[0] |= bitPosition; // King for white
        case 'Q' -> pieceBitboards[1] |= bitPosition; // Queen for white
        case 'R' -> pieceBitboards[2] |= bitPosition; // Rook for white
        case 'B' -> pieceBitboards[3] |= bitPosition; // Bishop for white
        case 'N' -> pieceBitboards[4] |= bitPosition; // Knight for white
        case 'P' -> pieceBitboards[5] |= bitPosition; // Pawn for white
        case 'k' -> pieceBitboards[6] |= bitPosition; // King for black
        case 'q' -> pieceBitboards[7] |= bitPosition; // Queen for black
        case 'r' -> pieceBitboards[8] |= bitPosition; // Rook for black
        case 'b' -> pieceBitboards[9] |= bitPosition; // Bishop for black
        case 'n' -> pieceBitboards[10] |= bitPosition; // Knight for black
        case 'p' -> pieceBitboards[11] |= bitPosition; // Pawn for black
      }
    }

    return pieceBitboards;
  }

  public int convertBitboardToInt(long bitboard) {
    int bitIndex = Long.numberOfTrailingZeros(bitboard);
    return (7 - (bitIndex / 8)) * 8 + (bitIndex % 8);
  }

  public int[] convertBitboardToArrayOfIndexes(long bitboard) {
    int[] indexes = new int[Long.bitCount(bitboard)];
    for (int i = 0; i < indexes.length; i++) {
      int index = convertBitboardToInt(bitboard);
      indexes[i] = index;
      bitboard = bitboard & (bitboard - 1);
    }
    return indexes;
  }

  public long[] getIndividualPieceBitboards(long bitboard) {
    int count = Long.bitCount(bitboard);
    long[] piecePositions = new long[count];

    for (int i = 0; i < count; i++) {
      long twosComplement = ~bitboard + 1L;
      piecePositions[i] = bitboard & twosComplement;
      bitboard = bitboard & (bitboard - 1);
    }
    return piecePositions;
  }

  public void setOccupied() {
    this.occupied = whitePieces | blackPieces;
  }

  public void setEmpty() {
    this.empty = ~occupied;
  }

  public void setWhitePieces() {
    this.whitePieces =
        pieceBitboards[0]
            | pieceBitboards[1]
            | pieceBitboards[2]
            | pieceBitboards[3]
            | pieceBitboards[4]
            | pieceBitboards[5];
  }

  public void setBlackPieces() {
    this.blackPieces =
        pieceBitboards[6]
            | pieceBitboards[7]
            | pieceBitboards[8]
            | pieceBitboards[9]
            | pieceBitboards[10]
            | pieceBitboards[11];
  }

  public long getEnPassantSquare() {
    return enPassantSquare;
  }

  public void setEnPassantSquare(long square) {
    this.enPassantSquare = square;
  }

  public List<Move> whitePawnMoves(long whitePawns) {
    List<Move> moveList = new ArrayList<>();
    long[] individualPawns = getIndividualPieceBitboards(whitePawns);

    for (long pawn : individualPawns) {
      int origin = convertBitboardToInt(pawn);
      long moveDestinationsBitboard = whitePawnMove(pawn);
      int[] moveDestinations = convertBitboardToArrayOfIndexes(moveDestinationsBitboard);
      for (int destination : moveDestinations) {
        long destinationBitboard = convertIntToBitboard(destination);
        if ((destinationBitboard & rankMasks[7]) != 0L) {
          Move promotionQueen = new Move(origin, destination, 'P', ' ', false, false, false, 'Q');
          Move promotionRook = new Move(origin, destination, 'P', ' ', false, false, false, 'R');
          Move promotionBishop = new Move(origin, destination, 'P', ' ', false, false, false, 'B');
          Move promotionKnight = new Move(origin, destination, 'P', ' ', false, false, false, 'N');
          moveList.add(promotionQueen);
          moveList.add(promotionRook);
          moveList.add(promotionBishop);
          moveList.add(promotionKnight);
        } else {
          Move move = new Move(origin, destination, 'P', ' ');
          moveList.add(move);
        }
      }
      long captureDestinationsBitboard = whitePawnCapture(pawn, false);
      int[] captureDestinations = convertBitboardToArrayOfIndexes(captureDestinationsBitboard);
      for (int destination : captureDestinations) {
        char capturedPiece = charBoard[destination];
        long destinationBitboard = convertIntToBitboard(destination);
        if ((destinationBitboard & rankMasks[7]) != 0L) {
          Move promotionQueen =
              new Move(origin, destination, 'P', capturedPiece, false, false, false, 'Q');
          Move promotionRook =
              new Move(origin, destination, 'P', capturedPiece, false, false, false, 'R');
          Move promotionBishop =
              new Move(origin, destination, 'P', capturedPiece, false, false, false, 'B');
          Move promotionKnight =
              new Move(origin, destination, 'P', capturedPiece, false, false, false, 'N');
          moveList.add(promotionQueen);
          moveList.add(promotionRook);
          moveList.add(promotionBishop);
          moveList.add(promotionKnight);
        } else if (destinationBitboard == enPassantSquare) {
          long[] savePieceBitboards = Arrays.copyOf(pieceBitboards, pieceBitboards.length);
          long saveAttackMap = attackMap;
          long saveAttackRay = attackRay;
          int saveAttackingPieces = attackingPieces;
          long savePinnedPieces = pinnedPieces;
          Map<Long, Character> savePinnedRays = new HashMap<>(pinnedRays);
          boolean saveCurrentTurn = currentTurn;
          long saveOccupied = occupied;
          long saveEmpty = empty;
          long saveWhitePieces = whitePieces;
          long saveBlackPieces = blackPieces;
          long saveEnPassantSquare = enPassantSquare;
          long originBitboard = convertIntToBitboard(origin);
          pieceBitboards[5] ^= originBitboard;
          pieceBitboards[5] |= destinationBitboard;
          pieceBitboards[11] ^= destinationBitboard >> 8;
          updateBitboard();
          if ((attackMap & pieceBitboards[0]) == 0L) {
            Move move = new Move(origin, destination, 'P', 'p', false, false, true, ' ');
            moveList.add(move);
          }
          pieceBitboards = savePieceBitboards;
          attackMap = saveAttackMap;
          attackRay = saveAttackRay;
          attackingPieces = saveAttackingPieces;
          pinnedPieces = savePinnedPieces;
          pinnedRays = savePinnedRays;
          currentTurn = saveCurrentTurn;
          occupied = saveOccupied;
          empty = saveEmpty;
          whitePieces = saveWhitePieces;
          blackPieces = saveBlackPieces;
          enPassantSquare = saveEnPassantSquare;
        } else {
          Move move = new Move(origin, destination, 'P', capturedPiece);
          moveList.add(move);
        }
      }
    }
    return moveList;
  }

  public List<Move> blackPawnMoves(long blackPawns) {
    List<Move> moveList = new ArrayList<>();
    long[] individualPawns = getIndividualPieceBitboards(blackPawns);

    for (long pawn : individualPawns) {
      int origin = convertBitboardToInt(pawn);
      long moveDestinationsBitboard = blackPawnMove(pawn);
      int[] moveDestinations = convertBitboardToArrayOfIndexes(moveDestinationsBitboard);
      for (int destination : moveDestinations) {
        long destinationBitboard = convertIntToBitboard(destination);
        if ((destinationBitboard & rankMasks[0]) != 0L) {
          Move promotionQueen = new Move(origin, destination, 'p', ' ', false, false, false, 'q');
          Move promotionRook = new Move(origin, destination, 'p', ' ', false, false, false, 'r');
          Move promotionBishop = new Move(origin, destination, 'p', ' ', false, false, false, 'b');
          Move promotionKnight = new Move(origin, destination, 'p', ' ', false, false, false, 'n');
          moveList.add(promotionQueen);
          moveList.add(promotionRook);
          moveList.add(promotionBishop);
          moveList.add(promotionKnight);

        } else {
          Move move = new Move(origin, destination, 'p', ' ');
          moveList.add(move);
        }
      }
      long captureDestinationsBitboard = blackPawnCapture(pawn, false);
      int[] captureDestinations = convertBitboardToArrayOfIndexes(captureDestinationsBitboard);
      for (int destination : captureDestinations) {
        char capturedPiece = charBoard[destination];
        long destinationBitboard = convertIntToBitboard(destination);
        if ((destinationBitboard & rankMasks[0]) != 0L) {
          Move promotionQueen =
              new Move(origin, destination, 'p', capturedPiece, false, false, false, 'q');
          Move promotionRook =
              new Move(origin, destination, 'p', capturedPiece, false, false, false, 'r');
          Move promotionBishop =
              new Move(origin, destination, 'p', capturedPiece, false, false, false, 'b');
          Move promotionKnight =
              new Move(origin, destination, 'p', capturedPiece, false, false, false, 'n');
          moveList.add(promotionQueen);
          moveList.add(promotionRook);
          moveList.add(promotionBishop);
          moveList.add(promotionKnight);
        } else if (destinationBitboard == enPassantSquare) {
          long[] savePieceBitboards = Arrays.copyOf(pieceBitboards, pieceBitboards.length);
          long saveAttackMap = attackMap;
          long saveAttackRay = attackRay;
          int saveAttackingPieces = attackingPieces;
          long savePinnedPieces = pinnedPieces;
          Map<Long, Character> savePinnedRays = new HashMap<>(pinnedRays);
          boolean saveCurrentTurn = currentTurn;
          long saveOccupied = occupied;
          long saveEmpty = empty;
          long saveWhitePieces = whitePieces;
          long saveBlackPieces = blackPieces;
          long saveEnPassantSquare = enPassantSquare;
          long originBitboard = convertIntToBitboard(origin);
          pieceBitboards[11] ^= originBitboard;
          pieceBitboards[11] |= destinationBitboard;
          pieceBitboards[5] ^= destinationBitboard << 8;
          updateBitboard();
          if ((attackMap & pieceBitboards[6]) == 0L) {
            Move move = new Move(origin, destination, 'p', 'P', false, false, true, ' ');
            moveList.add(move);
          }
          pieceBitboards = savePieceBitboards;
          attackMap = saveAttackMap;
          attackRay = saveAttackRay;
          attackingPieces = saveAttackingPieces;
          pinnedPieces = savePinnedPieces;
          pinnedRays = savePinnedRays;
          currentTurn = saveCurrentTurn;
          occupied = saveOccupied;
          empty = saveEmpty;
          whitePieces = saveWhitePieces;
          blackPieces = saveBlackPieces;
          enPassantSquare = saveEnPassantSquare;
        } else {
          Move move = new Move(origin, destination, 'p', capturedPiece);
          moveList.add(move);
        }
      }
    }
    return moveList;
  }

  public long whitePawnCapture(long whitePawn, boolean isAttackMap) {
    if (isAttackMap) {
      long leftCaptures = ((whitePawn & ~fileMasks[0]) << 7);
      long rightCaptures = ((whitePawn & ~fileMasks[7]) << 9);
      return leftCaptures | rightCaptures;
    }

    long leftCaptures = ((whitePawn & ~fileMasks[0]) << 7) & (blackPieces | enPassantSquare);
    long rightCaptures = ((whitePawn & ~fileMasks[7]) << 9) & (blackPieces | enPassantSquare);

    if ((whitePawn & pinnedPieces) != 0L) {
      switch (pinnedRays.get(whitePawn)) {
        case 'h', 'v' -> {
          return 0L;
        }
        case 'd' -> {
          return leftCaptures & attackRay;
        }
        case 'a' -> {
          return rightCaptures & attackRay;
        }
      }
    }
    return (leftCaptures | rightCaptures) & (attackRay | enPassantSquare);
  }

  public long blackPawnCapture(long blackPawn, boolean isAttackMap) {
    if (isAttackMap) {
      long leftCaptures = ((blackPawn & ~fileMasks[7]) >> 7);
      long rightCaptures = ((blackPawn & ~fileMasks[0]) >> 9);
      return leftCaptures | rightCaptures;
    }
    long leftCaptures = (blackPawn & ~fileMasks[7]) >> 7 & (whitePieces | enPassantSquare);
    long rightCaptures = (blackPawn & ~fileMasks[0]) >> 9 & (whitePieces | enPassantSquare);

    if (((blackPawn & pinnedPieces) != 0L)) {
      switch (pinnedRays.get(blackPawn)) {
        case 'h', 'v' -> {
          return 0L;
        }
        case 'd' -> {
          return leftCaptures & attackRay;
        }
        case 'a' -> {
          return rightCaptures & attackRay;
        }
      }
    }

    return (leftCaptures | rightCaptures) & (attackRay | enPassantSquare);
  }

  public long whitePawnMove(long whitePawn) {
    long forwardOne = ((whitePawn & ~rankMasks[7]) << 8) & ~occupied;
    long forwardTwo = ((whitePawn & rankMasks[1]) << 16) & (empty << 8) & ~occupied;

    if ((whitePawn & pinnedPieces) != 0L) {
      switch (pinnedRays.get(whitePawn)) {
        case 'h', 'd', 'a' -> {
          return 0L;
        }
        case 'v' -> {
          return (forwardOne | forwardTwo) & attackRay;
        }
      }
    }
    return (forwardOne | forwardTwo) & attackRay;
  }

  public long blackPawnMove(long blackPawn) {
    long forwardOne = ((blackPawn & ~rankMasks[0]) >> 8) & ~occupied;
    long forwardTwo = ((blackPawn & rankMasks[6]) >> 16) & (empty >> 8) & ~occupied;

    if ((blackPawn & pinnedPieces) != 0L) {
      switch (pinnedRays.get(blackPawn)) {
        case 'h', 'd', 'a' -> {
          return 0L;
        }
        case 'v' -> {
          return (forwardOne | forwardTwo) & attackRay;
        }
      }
    }
    return (forwardOne | forwardTwo) & attackRay;
  }

  public List<Move> rookMoves(long rooks, boolean color) {
    char piece = color ? 'R' : 'r';
    List<Move> moveList = new ArrayList<>();
    long[] individualRooks = getIndividualPieceBitboards(rooks);
    for (long rook : individualRooks) {
      int origin = convertBitboardToInt(rook);
      long destinationsBitboard = rookMove(rook, currentTurn, false);
      int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
      for (int destination : destinations) {
        char capturedPiece = charBoard[destination];
        Move move = new Move(origin, destination, piece, capturedPiece);
        moveList.add(move);
      }
    }
    return moveList;
  }

  public long rookMove(long rook, boolean color, boolean isAttackMap) {
    long currentPieces = color ? whitePieces : blackPieces;
    int position = Long.numberOfTrailingZeros(rook);
    long rank = rankMasks[position / 8];
    long file = fileMasks[position % 8];
    long horizontal =
        (occupied - 2 * rook) ^ Long.reverse(Long.reverse(occupied) - 2 * Long.reverse(rook));
    long vertical =
        (occupied & file) - (2 * rook)
            ^ Long.reverse(Long.reverse(occupied & file) - (2 * Long.reverse(rook)));

    if (((rook & pinnedPieces) != 0L) && !isAttackMap) {
      switch (pinnedRays.get(rook)) {
        case 'h' -> {
          return (horizontal & rank) & ~currentPieces & attackRay;
        }
        case 'v' -> {
          return (vertical & file) & ~currentPieces & attackRay;
        }
        case 'd', 'a' -> {
          return 0L;
        }
      }
    }

    long attacks = ((horizontal & rank) | (vertical & file)) & attackRay;

    if (isAttackMap) {
      return attacks;
    }

    return attacks & ~currentPieces;
  }

  public List<Move> bishopMoves(long bishops, boolean color) {
    char piece = color ? 'B' : 'b';
    List<Move> moveList = new ArrayList<>();
    long[] individualBishops = getIndividualPieceBitboards(bishops);
    for (long bishop : individualBishops) {
      int origin = convertBitboardToInt(bishop);
      long destinationsBitboard = bishopMove(bishop, currentTurn, false);
      int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
      for (int destination : destinations) {
        char capturedPiece = charBoard[destination];
        Move move = new Move(origin, destination, piece, capturedPiece);
        moveList.add(move);
      }
    }
    return moveList;
  }

  public long bishopMove(long bishop, boolean color, boolean isAttackMap) {
    long currentPieces = color ? whitePieces : blackPieces;
    int position = Long.numberOfTrailingZeros(bishop);
    long diagonal = diagonalMasks[(position / 8) + (position % 8)];
    long antiDiagonal = antiDiagonalMasks[(7 + (position / 8) - (position % 8))];
    long diagonalMove =
        ((occupied & diagonal) - (2 * bishop))
            ^ Long.reverse(Long.reverse(occupied & diagonal) - (2 * Long.reverse(bishop)));
    long antiDiagonalMove =
        ((occupied & antiDiagonal) - (2 * bishop))
            ^ Long.reverse(Long.reverse(occupied & antiDiagonal) - (2 * Long.reverse(bishop)));

    if (((bishop & pinnedPieces) != 0L) && !isAttackMap) {
      switch (pinnedRays.get(bishop)) {
        case 'h', 'v' -> {
          return 0L;
        }
        case 'd' -> {
          return (diagonalMove & diagonal) & ~currentPieces & attackRay;
        }
        case 'a' -> {
          return (antiDiagonalMove & antiDiagonal) & ~currentPieces & attackRay;
        }
      }
    }

    long attacks = ((diagonalMove & diagonal) | (antiDiagonalMove & antiDiagonal)) & attackRay;

    if (isAttackMap) {
      return attacks;
    }

    return attacks & ~currentPieces;
  }

  public List<Move> queenMoves(long queens, boolean color) {
    char piece = color ? 'Q' : 'q';
    List<Move> moveList = new ArrayList<>();
    long[] individualQueens = getIndividualPieceBitboards(queens);
    for (long queen : individualQueens) {
      int origin = convertBitboardToInt(queen);
      long destinationsBitboard = queenMove(queen, currentTurn, false);
      int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
      for (int destination : destinations) {
        char capturedPiece = charBoard[destination];
        Move move = new Move(origin, destination, piece, capturedPiece);
        moveList.add(move);
      }
    }
    return moveList;
  }

  public long queenMove(long queen, boolean color, boolean isAttackMap) {
    long currentPieces = color ? whitePieces : blackPieces;
    int position = Long.numberOfTrailingZeros(queen);
    long rank = rankMasks[position / 8];
    long file = fileMasks[position % 8];
    long diagonal = diagonalMasks[(position / 8) + (position % 8)];
    long antiDiagonal = antiDiagonalMasks[(7 + (position / 8) - (position % 8))];

    long horizontal =
        (occupied - 2 * queen) ^ Long.reverse(Long.reverse(occupied) - 2 * Long.reverse(queen));
    long vertical =
        (occupied & file) - (2 * queen)
            ^ Long.reverse(Long.reverse(occupied & file) - (2 * Long.reverse(queen)));
    long diagonalMove =
        ((occupied & diagonal) - (2 * queen))
            ^ Long.reverse(Long.reverse(occupied & diagonal) - (2 * Long.reverse(queen)));
    long antiDiagonalMove =
        ((occupied & antiDiagonal) - (2 * queen))
            ^ Long.reverse(Long.reverse(occupied & antiDiagonal) - (2 * Long.reverse(queen)));

    if (((queen & pinnedPieces) != 0L) && !isAttackMap) {
      switch (pinnedRays.get(queen)) {
        case 'h' -> {
          return (horizontal & rank) & ~currentPieces & attackRay;
        }
        case 'v' -> {
          return (vertical & file) & ~currentPieces & attackRay;
        }
        case 'd' -> {
          return (diagonalMove & diagonal) & ~currentPieces & attackRay;
        }
        case 'a' -> {
          return (antiDiagonalMove & antiDiagonal) & ~currentPieces & attackRay;
        }
      }
    }

    long attacks =
        (horizontal & rank)
            | (vertical & file)
            | (diagonalMove & diagonal)
            | (antiDiagonalMove & antiDiagonal);

    if (isAttackMap) {
      return attacks & attackRay;
    }

    return attacks & ~currentPieces & attackRay;
  }

  public List<Move> knightMoves(long knights, boolean color) {
    char piece = color ? 'N' : 'n';
    List<Move> moveList = new ArrayList<>();
    long[] individualKnights = getIndividualPieceBitboards(knights);
    for (long knight : individualKnights) {
      int origin = convertBitboardToInt(knight);
      long destinationsBitboard = knightMove(knight, color, false);
      int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
      for (int destination : destinations) {
        char capturedPiece = charBoard[destination];
        Move move = new Move(origin, destination, piece, capturedPiece);
        moveList.add(move);
      }
    }
    return moveList;
  }

  public long knightMove(long knight, boolean color, boolean isAttackMap) {
    if (((knight & pinnedPieces) != 0L) && !isAttackMap) {
      return 0L;
    }

    long currentPieces = color ? whitePieces : blackPieces;
    long nne = (knight << 17) & ~(fileMasks[0]);
    long nee = (knight << 10) & ~(fileMasks[0] | fileMasks[1]);
    long see = (knight >>> 6) & ~(fileMasks[0] | fileMasks[1]);
    long sse = (knight >>> 15) & ~(fileMasks[0]);
    long nnw = (knight << 15) & ~(fileMasks[7]);
    long nww = (knight << 6) & ~(fileMasks[6] | fileMasks[7]);
    long sww = (knight >>> 10) & ~(fileMasks[6] | fileMasks[7]);
    long ssw = (knight >>> 17) & ~(fileMasks[7]);

    long attacks = (nne | nee | see | sse | nnw | nww | sww | ssw) & attackRay;

    if (isAttackMap) {
      return attacks;
    }

    return attacks & ~currentPieces;
  }

  public List<Move> kingMoves(long king, boolean color) {
    char piece = color ? 'K' : 'k';
    List<Move> moveList = new ArrayList<>();
    int origin = convertBitboardToInt(king);
    long destinationsBitboard = kingMove(king, color, false);
    int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
    for (int destination : destinations) {
      char capturedPiece = charBoard[destination];
      Move move;
      boolean castle = Math.abs(destination - origin) == 2;
      if (castle) {
        if (destination > origin) {
          move = new Move(origin, destination, piece, ' ', true, false, false, ' ');
        } else {
          move = new Move(origin, destination, piece, ' ', false, true, false, ' ');
        }
      } else {
        move = new Move(origin, destination, piece, capturedPiece);
      }
      moveList.add(move);
    }
    List<Move> castleMoves = castleMoves(king, color);
    moveList.addAll(castleMoves);
    return moveList;
  }

  public long kingMove(long king, boolean color, boolean isAttackMap) {
    long currentPieces = color ? whitePieces : blackPieces;

    long n = (king & ~rankMasks[7]) << 8;
    long s = (king & ~rankMasks[0]) >>> 8;
    long e = (king & ~fileMasks[7]) << 1;
    long w = (king & ~fileMasks[0]) >>> 1;
    long ne = (king & ~rankMasks[7] & ~fileMasks[7]) << 9;
    long se = (king & ~rankMasks[0] & ~fileMasks[7]) >>> 7;
    long nw = (king & ~rankMasks[7] & ~fileMasks[0]) << 7;
    long sw = (king & ~rankMasks[0] & ~fileMasks[0]) >>> 9;

    long attacks = (n | s | e | w | ne | se | nw | sw);

    if (isAttackMap) {
      return attacks;
    }

    return attacks & ~currentPieces & ~attackMap;
  }

  public List<Move> castleMoves(long king, boolean color) {
    char piece = color ? 'K' : 'k';
    List<Move> moveList = new ArrayList<>();
    int origin = convertBitboardToInt(king);
    long destinationsBitboard = castleMove(king, color);
    if (destinationsBitboard == 0L) {
      return moveList;
    }
    int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
    for (int destination : destinations) {
      Move move;
      if (destination > origin) {
        move = new Move(origin, destination, piece, ' ', true, false, false, ' ');
      } else {
        move = new Move(origin, destination, piece, ' ', false, true, false, ' ');
      }
      moveList.add(move);
    }
    return moveList;
  }

  public long castleMove(long king, boolean color) {
    long kingSideCastle = 0L;
    long queenSideCastle = 0L;

    if (color) {
      if (whiteKingSide) {
        if (((attackMap & whiteKingSideMask) == 0L)
            & (((king << 1) & occupied) == 0L)
            & (((king << 2) & occupied) == 0L)) {
          kingSideCastle = king << 2;
        }
      }
      if (whiteQueenSide) {
        if (((attackMap & whiteQueenSideMask) == 0L)
            & (((king >> 1) & occupied) == 0L)
            & (((king >> 2) & occupied) == 0L)
            & (((king >> 3) & occupied) == 0L)) {
          queenSideCastle = king >> 2;
        }
      }
    } else {
      if (blackKingSide) {
        if (((attackMap & blackKingSideMask) == 0L)
            & (((king << 1) & occupied) == 0L)
            & (((king << 2) & occupied) == 0L)) {
          kingSideCastle = king << 2;
        }
      }
      if (blackQueenSide) {
        if (((attackMap & blackQueenSideMask) == 0L)
            & (((king >> 1) & occupied) == 0L)
            & (((king >> 2) & occupied) == 0L)
            & (((king >> 3) & occupied) == 0L)) {
          queenSideCastle = king >> 2;
        }
      }
    }
    return kingSideCastle | queenSideCastle;
  }
}
