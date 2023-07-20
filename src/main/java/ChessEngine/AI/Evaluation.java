package ChessEngine.AI;

import ChessEngine.model.Bitboard;
import ChessEngine.model.Model;

public class Evaluation {
  static int[] knightValues = {-20, -16, -12, -8, -4, 0, 4, 8, 12};
  static int[] rookValues = {15, 12, 9, 6, 3, 0, -3, -6, -9};
  private final Model model;
  private final Bitboard bitboard;
  public long check;

  public Evaluation(Model model) {
    this.model = model;
    this.bitboard = model.getBitboard();
    check = 0L;
  }

  public int evaluate(boolean currentTurn) {
    int eval = 0;
    eval += materialScore();
    eval += pieceTableBonuses();
    eval += pawnEvaluation();
    eval += knightEvaluation();
    eval += bishopEvaluation();
    eval += rookEvaluation();
    eval += queenEvaluation();
    eval += kingEvaluation();
    int color = currentTurn ? 1 : -1;
    return eval * color;
  }

  public int materialScore() {
    return model.getBitboard().materialCount;
  }

  public int pieceTableBonuses() {
    return model.getBitboard().squareBonuses;
  }

  public int finalEvaluation() {
    return 0;
  }

  public int openingEvaluation() {
    return 0;
  }

  public int middleGameEvaluation() {
    return 0;
  }

  public int endGameEvaluation() {
    return 0;
  }

  public int pawnEvaluation() {
    if (model.pawnTable.containsKey(model.pawnHashKey)) {
      return model.pawnTable.get(model.pawnHashKey);
    }

    int eval = whitePawnEvaluation() - blackPawnEvaluation();
    model.pawnTable.put(model.pawnHashKey, eval);
    return eval;
  }

  public int knightEvaluation() {
    int whiteEval = knightValues[Long.bitCount(bitboard.pieceBitboards[5])];
    int blackEval = knightValues[Long.bitCount(bitboard.pieceBitboards[11])];

    return whiteEval - blackEval;
  }

  public int bishopEvaluation() {
    int whiteEval = 0;
    int blackEval = 0;

    // Bishop Pair
    if (Long.bitCount(bitboard.pieceBitboards[3]) == 2) {
      whiteEval += 35;
    }
    if (Long.bitCount(bitboard.pieceBitboards[9]) == 2) {
      blackEval -= 35;
    }

    return whiteEval - blackEval;
  }

  public int rookEvaluation() {
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

  public int queenEvaluation() {
    // Nothing right now, might add stuff later
    int whiteEval = 0;
    int blackEval = 0;

    return whiteEval - blackEval;
  }

  public int kingEvaluation() {
    return whiteKingEvaluation() - blackKingEvaluation();
  }

  public int whiteKingEvaluation() {
    int eval = 0;

    // Penalty for losing castling rights without castling
    if (!model.hasWhiteCastled) {
      if (!bitboard.whiteKingSide) {
        eval -= 15;
      }
      if (!bitboard.whiteQueenSide) {
        eval -= 15;
      }
    }
    return eval;
  }

  public int blackKingEvaluation() {
    int eval = 0;

    // Penalty for losing castling rights without castling
    if (!model.hasBlackCastled) {
      if (!bitboard.blackKingSide) {
        eval -= 15;
      }
      if (!bitboard.blackQueenSide) {
        eval -= 15;
      }
    }
    return eval;
  }

  public int whitePawnEvaluation() {
    int eval = 0;
    long whitePawns = bitboard.pieceBitboards[5];
    long blackPawns = bitboard.pieceBitboards[11];

    long passedPawns = 0L;
    int doubledPawns = 0;

    long[] individualPawns = bitboard.getIndividualPieceBitboards(whitePawns);
    for (long pawn : individualPawns) {
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
      long frontSpan = (file | leftFile | rightFile) & upperBits & ~rank;

      // Passed Pawns
      if ((frontSpan & blackPawns) == 0L && ((whitePawns ^ pawn) & file & upperBits) == 0L) {
        passedPawns |= pawn;
        switch (position / 8) {
          case 1 -> eval += 3;
          case 2 -> eval += 7;
          case 3 -> eval += 12;
          case 4 -> eval += 19;
          case 5 -> eval += 27;
          case 6 -> eval += 37;
        }
      }

      // Doubled Pawns
      if (((whitePawns ^ pawn) & file) != 0L) {
        doubledPawns++;
      }

      // Isolated Pawns
      if (((whitePawns & leftFile) | (whitePawns & rightFile)) == 0L) {
        switch (position % 8) {
          case 0, 1, 6, 7 -> eval -= 3;
          case 2, 5 -> eval -= 4;
          case 3, 4 -> eval -= 5;
        }
      }
    }

    long connectedPawns = bitboard.whitePawnCapture(whitePawns, true) & whitePawns;

    // Bonus for protected passed pawn
    eval += 8 * Long.bitCount(connectedPawns & passedPawns);

    eval -= doubledPawns * 4;
    eval += Long.bitCount(connectedPawns) * 5;
    return eval;
  }

  public int blackPawnEvaluation() {
    int eval = 0;
    long whitePawns = bitboard.pieceBitboards[5];
    long blackPawns = bitboard.pieceBitboards[11];

    long passedPawns = 0L;
    int doubledPawns = 0;

    long[] individualPawns = bitboard.getIndividualPieceBitboards(blackPawns);
    for (long pawn : individualPawns) {
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
        passedPawns |= pawn;
        switch (position / 8) {
          case 6 -> eval += 3;
          case 5 -> eval += 7;
          case 4 -> eval += 12;
          case 3 -> eval += 19;
          case 2 -> eval += 27;
          case 1 -> eval += 37;
        }
      }

      // Doubled Pawns
      if (((blackPawns ^ pawn) & file) != 0L) {
        doubledPawns++;
      }

      // Isolated Pawns
      if (((blackPawns & leftFile) | (blackPawns & rightFile)) == 0L) {
        switch (position % 8) {
          case 0, 1, 6, 7 -> eval -= 3;
          case 2, 5 -> eval -= 4;
          case 3, 4 -> eval -= 5;
        }
      }
    }

    long connectedPawns = bitboard.blackPawnCapture(whitePawns, true) & blackPawns;

    // Bonus for protected passed pawn
    eval += 8 * Long.bitCount(connectedPawns & passedPawns);

    eval -= doubledPawns * 4;
    eval += connectedPawns * 5;
    return eval;
  }
}
