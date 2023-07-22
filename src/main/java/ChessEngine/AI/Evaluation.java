package ChessEngine.AI;

import ChessEngine.model.Bitboard;
import ChessEngine.model.Model;

public class Evaluation {
  static int[] kingCentralizationTable = {
    -50, -40, -30, -20, -20, -30, -40, -50,
    -40, -30, -20, -10, -10, -20, -30, -40,
    -30, -20, -10, 0, 0, -10, -20, -30,
    -20, -10, 0, 20, 20, 0, -10, -20,
    -20, -10, 0, 20, 20, 0, -10, -20,
    -30, -20, -10, 0, 0, -10, -20, -30,
    -40, -30, -20, -10, -10, -20, -30, -40,
    -50, -40, -30, -20, -20, -30, -40, -50
  };
  static int maxPieces = 32;
  static int endGameThreshold = 15;
  static int[] knightValues = {-20, -16, -12, -8, -4, 0, 4, 8, 12};
  static int[] rookValues = {15, 12, 9, 6, 3, 0, -3, -6, -9};
  private final Model model;
  private final Bitboard bitboard;
  public long check;
  private double openingWeight;
  private double endGameWeight;

  public Evaluation(Model model) {
    this.model = model;
    this.bitboard = model.getBitboard();
    check = 0L;
  }

  public int evaluate(boolean currentTurn) {
    int color = currentTurn ? 1 : -1;
    return (finalEvaluation() + materialScore()) * color;
  }

  public int evaluationDisplay() {
    return (finalEvaluation() + materialScore());
  }

  public int materialScore() {
    return model.getBitboard().materialCount;
  }

  public int pieceTableBonuses() {
    return model.getBitboard().squareBonuses;
  }

  public void adjustWeights() {
    int remainingPieces = Long.bitCount(bitboard.occupied);

    if (remainingPieces > endGameThreshold) {
      double ratio = (double) (remainingPieces - endGameThreshold) / (maxPieces - endGameThreshold);
      openingWeight = 0.5 + (0.5 * ratio);
      endGameWeight = 0.5 - (0.5 * ratio);
    } else {
      double ratio = (double) remainingPieces / endGameThreshold;
      openingWeight = (0.3 * ratio);
      endGameWeight = 1 - (0.3 * ratio);
    }
  }

  public int finalEvaluation() {
    adjustWeights();

    int openingPawnEval = openingPawnEvaluation();
    int endGamePawnEval = endGamePawnEvaluation();

    if (!model.pawnTable.containsKey(model.pawnHashKey)) {
      model.pawnTable.put(model.pawnHashKey, new PawnEntry(openingPawnEval, endGamePawnEval));
    }

    int eval =
        (int)
            ((openingEvaluation() + openingPawnEval + pieceTableBonuses()) * openingWeight
                + (endGameEvaluation() + endGamePawnEval) * endGameWeight);

    eval += mopUpEvaluation(eval);

    return eval;
  }

  public int openingEvaluation() {
    return openingKnightEvaluation()
        + openingBishopEvaluation()
        + openingRookEvaluation()
        + openingQueenEvaluation()
        + openingKingEvaluation();
  }

  public int endGameEvaluation() {
    return endGameKnightEvaluation()
        + endGameBishopEvaluation()
        + endGameRookEvaluation()
        + endGameQueenEvaluation()
        + endGameKingEvaluation();
  }

  public int mopUpEvaluation(int eval) {
    if ((bitboard.pieceBitboards[5] | bitboard.pieceBitboards[11]) != 0L) {
      return 0;
    }

    int whiteEval = 0;
    int blackEval = 0;

    int whiteKingPosition = bitboard.convertBitboardToInt(bitboard.pieceBitboards[0]);
    int blackKingPosition = bitboard.convertBitboardToInt(bitboard.pieceBitboards[6]);
    int kingDistance = manhattanDistance(whiteKingPosition, blackKingPosition);

    if (eval > 0) {
      whiteEval -= (kingCentralizationTable[blackKingPosition]);
      whiteEval -= 10 * kingDistance;

    } else if (eval < 0) {
      blackEval -= (kingCentralizationTable[whiteKingPosition]);
      blackEval -= 10 * kingDistance;
    }

    return whiteEval - blackEval;
  }

  public int openingPawnEvaluation() {
    if (model.pawnTable.containsKey(model.pawnHashKey)) {
      return model.pawnTable.get(model.pawnHashKey).openingEvaluation();
    }

    // White Pawn Evaluation
    int whiteEval = 0;
    long whitePawns = bitboard.pieceBitboards[5];
    long blackPawns = bitboard.pieceBitboards[11];

    long whitePassedPawns = 0L;
    int whiteDoubledPawns = 0;

    long[] individualWhitePawns = bitboard.getIndividualPieceBitboards(whitePawns);
    for (long pawn : individualWhitePawns) {
      int position = Long.numberOfTrailingZeros(pawn);
      long rank = Bitboard.rankMasks[position / 8];
      long file = Bitboard.fileMasks[position % 8];
      long upperBits = ~1L << position;
      long leftFile = 0L;
      long rightFile = 0L;
      if (position % 8 != 0) {
        leftFile = Bitboard.fileMasks[(position % 8) - 1];
      }
      if (position % 8 != 7) {
        rightFile = Bitboard.fileMasks[(position % 8) + 1];
      }
      long frontSpan = (file | leftFile | rightFile) & upperBits & ~rank;

      // Passed Pawns
      if ((frontSpan & blackPawns) == 0L && ((whitePawns ^ pawn) & file & upperBits) == 0L) {
        whitePassedPawns |= pawn;
        switch (position / 8) {
          case 1 -> whiteEval += 3;
          case 2 -> whiteEval += 7;
          case 3 -> whiteEval += 12;
          case 4 -> whiteEval += 19;
          case 5 -> whiteEval += 27;
          case 6 -> whiteEval += 37;
        }
      }

      // Doubled Pawns
      if (((whitePawns ^ pawn) & file) != 0L) {
        whiteDoubledPawns++;
      }

      // Isolated Pawns
      if (((whitePawns & leftFile) | (whitePawns & rightFile)) == 0L) {
        switch (position % 8) {
          case 0, 1, 6, 7 -> whiteEval -= 3;
          case 2, 5 -> whiteEval -= 4;
          case 3, 4 -> whiteEval -= 5;
        }
      }
    }

    long whiteConnectedPawns = bitboard.whitePawnCapture(whitePawns, true) & whitePawns;

    // Bonus for protected passed pawn
    whiteEval += 8 * Long.bitCount(whiteConnectedPawns & whitePassedPawns);

    whiteEval -= whiteDoubledPawns * 4;
    whiteEval += Long.bitCount(whiteConnectedPawns) * 5;

    // Black Pawn Evaluation
    int blackEval = 0;
    long blackPassedPawns = 0L;
    int blackDoubledPawns = 0;

    long[] individualBlackPawns = bitboard.getIndividualPieceBitboards(blackPawns);
    for (long pawn : individualBlackPawns) {
      int position = Long.numberOfTrailingZeros(pawn);
      long rank = Bitboard.rankMasks[position / 8];
      long file = Bitboard.fileMasks[position % 8];
      long upperBits = ~1L << position;
      long lowerBits = ~upperBits & ~pawn;
      long leftFile = 0L;
      long rightFile = 0L;

      if (position % 8 != 0) {
        leftFile = Bitboard.fileMasks[(position % 8) - 1];
      }
      if (position % 8 != 7) {
        rightFile = Bitboard.fileMasks[(position % 8) + 1];
      }
      long frontSpan = (file | leftFile | rightFile) & lowerBits & ~rank;

      // Passed Pawns
      if ((frontSpan & whitePawns) == 0L && ((blackPawns ^ pawn) & file & lowerBits) == 0L) {
        blackPassedPawns |= pawn;
        switch (position / 8) {
          case 6 -> blackEval += 3;
          case 5 -> blackEval += 7;
          case 4 -> blackEval += 12;
          case 3 -> blackEval += 19;
          case 2 -> blackEval += 27;
          case 1 -> blackEval += 37;
        }
      }

      // Doubled Pawns
      if (((blackPawns ^ pawn) & file) != 0L) {
        blackDoubledPawns++;
      }

      // Isolated Pawns
      if (((blackPawns & leftFile) | (blackPawns & rightFile)) == 0L) {
        switch (position % 8) {
          case 0, 1, 6, 7 -> blackEval -= 3;
          case 2, 5 -> blackEval -= 4;
          case 3, 4 -> blackEval -= 5;
        }
      }
    }

    long blackConnectedPawns = bitboard.blackPawnCapture(whitePawns, true) & blackPawns;

    // Bonus for protected passed pawn
    blackEval += 8 * Long.bitCount(blackConnectedPawns & blackPassedPawns);

    blackEval -= blackDoubledPawns * 4;
    blackEval += Long.bitCount(blackConnectedPawns) * 5;

    return whiteEval - blackEval;
  }

  public int openingKnightEvaluation() {
    int whiteEval = knightValues[Long.bitCount(bitboard.pieceBitboards[5])];
    int blackEval = knightValues[Long.bitCount(bitboard.pieceBitboards[11])];

    return whiteEval - blackEval;
  }

  public int openingBishopEvaluation() {
    int whiteEval = 0;
    int blackEval = 0;

    // Bishop Pair
    if (Long.bitCount(bitboard.pieceBitboards[3]) == 2) {
      whiteEval += 50;
    }
    if (Long.bitCount(bitboard.pieceBitboards[9]) == 2) {
      blackEval += 50;
    }

    return whiteEval - blackEval;
  }

  public int openingRookEvaluation() {
    int whiteEval = 0;
    int blackEval = 0;

    long[] whiteRooks = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[2]);

    for (long rook : whiteRooks) {
      int position = Long.numberOfTrailingZeros(rook);
      long file = Bitboard.fileMasks[position % 8];
      long pawns = bitboard.pieceBitboards[5];

      if ((file & pawns) == 0L) {
        whiteEval += 10;
      }
    }

    whiteEval += rookValues[Long.bitCount(bitboard.pieceBitboards[5])];

    long[] blackRooks = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[8]);

    for (long rook : blackRooks) {
      int position = Long.numberOfTrailingZeros(rook);
      long file = Bitboard.fileMasks[position % 8];
      long pawns = bitboard.pieceBitboards[11];

      if ((file & pawns) == 0L) {
        blackEval += 10;
      }
    }

    blackEval += rookValues[Long.bitCount(bitboard.pieceBitboards[11])];

    return whiteEval - blackEval;
  }

  public int openingQueenEvaluation() {
    // No queen evaluation right now
    return 0;
  }

  public int openingKingEvaluation() {
    // White King Evaluation
    int whiteEval = 0;

    // Penalty for losing castling rights without castling
    if (!model.hasWhiteCastled) {
      if (!bitboard.whiteKingSide) {
        whiteEval -= 15;
      }
      if (!bitboard.whiteQueenSide) {
        whiteEval -= 15;
      }
    }

    int whiteKingSafety = Long.bitCount(bitboard.queenMove(bitboard.pieceBitboards[0], true, true));
    whiteEval -= whiteKingSafety * 2;

    // Black King Evaluation
    int blackEval = 0;

    // Penalty for losing castling rights without castling
    if (!model.hasBlackCastled) {
      if (!bitboard.blackKingSide) {
        blackEval -= 15;
      }
      if (!bitboard.blackQueenSide) {
        blackEval -= 15;
      }
    }

    int blackKingSafety =
        Long.bitCount(bitboard.queenMove(bitboard.pieceBitboards[6], false, true));
    blackEval -= blackKingSafety * 2;

    return whiteEval - blackEval;
  }

  public int endGamePawnEvaluation() {
    if (model.pawnTable.containsKey(model.pawnHashKey)) {
      return model.pawnTable.get(model.pawnHashKey).endGameEvaluation();
    }

    long whitePawns = bitboard.pieceBitboards[5];
    long blackPawns = bitboard.pieceBitboards[11];

    // White Pawn Evaluation
    int whiteEval = 0;
    int whiteDoubledPawns = 0;
    long whitePassedPawns = 0L;

    long[] individualWhitePawns = bitboard.getIndividualPieceBitboards(whitePawns);
    for (long pawn : individualWhitePawns) {
      int position = Long.numberOfTrailingZeros(pawn);
      long rank = Bitboard.rankMasks[position / 8];
      long file = Bitboard.fileMasks[position % 8];
      long upperBits = ~1L << position;
      long leftFile = 0L;
      long rightFile = 0L;
      if (position % 8 != 0) {
        leftFile = Bitboard.fileMasks[(position % 8) - 1];
      }
      if (position % 8 != 7) {
        rightFile = Bitboard.fileMasks[(position % 8) + 1];
      }
      long frontSpan = (file | leftFile | rightFile) & upperBits & ~rank;

      // Passed Pawns
      if ((frontSpan & blackPawns) == 0L && ((whitePawns ^ pawn) & file & upperBits) == 0L) {
        whitePassedPawns |= pawn;
        switch (position / 8) {
          case 1 -> whiteEval += 18;
          case 2 -> whiteEval += 22;
          case 3 -> whiteEval += 27;
          case 4 -> whiteEval += 34;
          case 5 -> whiteEval += 42;
          case 6 -> whiteEval += 52;
        }
      }

      // Bonus for having pawns that are next to each other
      if ((leftFile & whitePawns) != 0L) {
        whiteEval += 7;
      }
      if ((rightFile & whitePawns) != 0L) {
        whiteEval += 7;
      }

      // Doubled Pawns
      if (((whitePawns ^ pawn) & file) != 0L) {
        whiteDoubledPawns++;
      }
    }

    long whiteConnectedPawns = bitboard.whitePawnCapture(whitePawns, true) & whitePawns;

    // Bonus for protected passed pawn
    whiteEval += 8 * Long.bitCount(whiteConnectedPawns & whitePassedPawns);

    whiteEval -= whiteDoubledPawns * 4;
    whiteEval += Long.bitCount(whiteConnectedPawns) * 5;

    // Black Pawn Evaluation
    int blackEval = 0;
    long blackPassedPawns = 0L;
    int blackDoubledPawns = 0;

    long[] individualBlackPawns = bitboard.getIndividualPieceBitboards(blackPawns);
    for (long pawn : individualBlackPawns) {
      int position = Long.numberOfTrailingZeros(pawn);
      long rank = Bitboard.rankMasks[position / 8];
      long file = Bitboard.fileMasks[position % 8];
      long upperBits = ~1L << position;
      long lowerBits = ~upperBits & ~pawn;
      long leftFile = 0L;
      long rightFile = 0L;

      if (position % 8 != 0) {
        leftFile = Bitboard.fileMasks[(position % 8) - 1];
      }
      if (position % 8 != 7) {
        rightFile = Bitboard.fileMasks[(position % 8) + 1];
      }
      long frontSpan = (file | leftFile | rightFile) & lowerBits & ~rank;

      // Passed Pawns
      if ((frontSpan & whitePawns) == 0L && ((blackPawns ^ pawn) & file & lowerBits) == 0L) {
        blackPassedPawns |= pawn;
        switch (position / 8) {
          case 6 -> blackEval += 18;
          case 5 -> blackEval += 22;
          case 4 -> blackEval += 27;
          case 3 -> blackEval += 34;
          case 2 -> blackEval += 42;
          case 1 -> blackEval += 52;
        }
      }

      // Doubled Pawns
      if (((blackPawns ^ pawn) & file) != 0L) {
        blackDoubledPawns++;
      }

      if ((leftFile & blackPawns) != 0L) {
        blackEval += 7;
      }
      if ((rightFile & blackPawns) != 0L) {
        blackEval += 7;
      }
    }

    long blackConnectedPawns = bitboard.blackPawnCapture(whitePawns, true) & blackPawns;

    // Bonus for protected passed pawn
    blackEval += 8 * Long.bitCount(blackConnectedPawns & blackPassedPawns);

    blackEval -= blackDoubledPawns * 4;
    blackEval += Long.bitCount(blackConnectedPawns) * 5;

    return whiteEval - blackEval;
  }

  public int endGameKnightEvaluation() {
    int whiteEval = knightValues[Long.bitCount(bitboard.pieceBitboards[5])];
    int blackEval = knightValues[Long.bitCount(bitboard.pieceBitboards[11])];

    long[] whiteKnights = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[4]);
    long[] blackKnights = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[10]);

    int whiteMobility = 0;
    int blackMobility = 0;

    // Increase bonus for more knight mobility in the end game
    for (long knight : whiteKnights) {
      whiteMobility += Long.bitCount(bitboard.knightMove(knight, true, false));

      if (whiteMobility <= 4) {
        whiteEval -= 3 * whiteMobility;
      } else {
        whiteEval += 3 * whiteMobility;
      }
    }

    for (long knight : blackKnights) {
      blackMobility += Long.bitCount(bitboard.knightMove(knight, false, false));

      if (blackMobility <= 4) {
        blackEval -= 3 * blackMobility;
      } else {
        blackEval += 3 * blackMobility;
      }
    }

    return whiteEval - blackEval;
  }

  public int endGameBishopEvaluation() {
    int whiteEval = 0;
    int blackEval = 0;

    // Bishop Pair
    if (Long.bitCount(bitboard.pieceBitboards[3]) == 2) {
      whiteEval += 50;
    }
    if (Long.bitCount(bitboard.pieceBitboards[9]) == 2) {
      blackEval += 50;
    }

    // Bonus for protected bishops in endgame
    if ((bitboard.whitePawnCapture(bitboard.pieceBitboards[5], true) & bitboard.pieceBitboards[3])
        != 0L) {
      whiteEval += 15;
    }
    if ((bitboard.blackPawnCapture(bitboard.pieceBitboards[11], true) & bitboard.pieceBitboards[9])
        != 0L) {
      blackEval += 15;
    }

    return whiteEval - blackEval;
  }

  public int endGameRookEvaluation() {
    int whiteEval = 0;
    int blackEval = 0;

    long[] whiteRooks = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[2]);

    for (long rook : whiteRooks) {
      int position = Long.numberOfTrailingZeros(rook);
      long file = Bitboard.fileMasks[position % 8];
      long rank = Bitboard.fileMasks[position / 8];
      long pawns = bitboard.pieceBitboards[5];

      // Bonus for rook on open file
      if ((file & pawns) == 0L) {
        whiteEval += 10;
      }

      // Bonus for rook on 7th or 8th rank
      if (rank == 6) {
        whiteEval += 50;
      } else if (rank == 7) {
        whiteEval += 35;
      }
    }

    whiteEval += rookValues[Long.bitCount(bitboard.pieceBitboards[5])];

    long[] blackRooks = bitboard.getIndividualPieceBitboards(bitboard.pieceBitboards[8]);

    for (long rook : blackRooks) {
      int position = Long.numberOfTrailingZeros(rook);
      long file = Bitboard.fileMasks[position % 8];
      long rank = Bitboard.fileMasks[position / 8];
      long pawns = bitboard.pieceBitboards[11];

      if ((file & pawns) == 0L) {
        blackEval += 10;
      }

      // Bonus for rook on 1st or 2nd rank
      if (rank == 1) {
        blackEval += 50;
      } else if (rank == 0) {
        blackEval += 35;
      }
    }

    blackEval += rookValues[Long.bitCount(bitboard.pieceBitboards[11])];

    return whiteEval - blackEval;
  }

  public int endGameQueenEvaluation() {
    // No queen evaluation right now
    return 0;
  }

  public int endGameKingEvaluation() {
    if (model.kingPawnProximityTable.containsKey(model.kingPawnProximityHashKey)) {
      return model.kingPawnProximityTable.get(model.kingPawnProximityHashKey);
    }

    long[] pawns =
        bitboard.getIndividualPieceBitboards(
            bitboard.pieceBitboards[5] | bitboard.pieceBitboards[11]);
    int numPawns = Long.bitCount(bitboard.pieceBitboards[5] | bitboard.pieceBitboards[11]);

    int whiteEval = 0;
    int blackEval = 0;

    int whiteKingPosition = bitboard.convertBitboardToInt(bitboard.pieceBitboards[0]);
    int blackKingPosition = bitboard.convertBitboardToInt(bitboard.pieceBitboards[6]);

    // King centralization is favorable in end game
    whiteEval += kingCentralizationTable[whiteKingPosition];
    blackEval += kingCentralizationTable[blackKingPosition];

    int whiteKingDistance = 0;
    int blackKingDistance = 0;

    for (long pawn : pawns) {
      int pawnPosition = bitboard.convertBitboardToInt(pawn);

      whiteKingDistance += manhattanDistance(whiteKingPosition, pawnPosition);
      blackKingDistance += manhattanDistance(blackKingPosition, pawnPosition);
    }

    double whiteAverageDistance = (double) whiteKingDistance / numPawns;
    double blackAverageDistance = (double) blackKingDistance / numPawns;

    // Bonus for king that is closer to pawns in end game
    whiteEval += (int) (80 - 2 * whiteAverageDistance);
    blackEval += (int) (80 - 2 * blackAverageDistance);

    int numPieces = Long.bitCount(bitboard.occupied);

    // Bonus for side with opposition, which increase as piece count decreases
    if (manhattanDistance(whiteKingPosition, blackKingPosition) == 2) {
      if (model.getCurrentTurn()) {
        if (numPieces > 6) {
          blackEval += 50 - (5 * numPieces);
        } else {
          blackEval += 100 - (10 * numPieces);
        }
      } else {
        if (numPieces > 6) {
          whiteEval += 50 - (5 * numPieces);
        } else {
          whiteEval += 100 - (10 * numPieces);
        }
      }
    }

    int eval = whiteEval - blackEval;

    model.kingPawnProximityTable.put(model.kingPawnProximityHashKey, eval);

    return eval;
  }

  public int manhattanDistance(int square1, int square2) {
    // Convert square indices to 2D coordinates (file and rank)
    int file1 = square1 % 8;
    int rank1 = square1 / 8;
    int file2 = square2 % 8;
    int rank2 = square2 / 8;

    // Calculate the Manhattan distance
    return Math.abs(file2 - file1) + Math.abs(rank2 - rank1);
  }
}
