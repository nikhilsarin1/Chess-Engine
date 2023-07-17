package ChessEngine.AI;

import ChessEngine.model.Model;
import ChessEngine.model.Move;

public class AI {
  private static final int[] boardEdge = {
    5, 4, 3, 2, 2, 3, 4, 5,
    4, 3, 2, 1, 1, 2, 3, 4,
    3, 2, 1, 0, 0, 1, 2, 3,
    2, 1, 0, -1, -1, 0, 1, 2,
    2, 1, 0, -1, -1, 0, 1, 2,
    3, 2, 1, 0, 0, 1, 2, 3,
    4, 3, 2, 1, 1, 2, 3, 4,
    5, 4, 3, 2, 2, 3, 4, 5
  };
  static int mateScore = 1000000;
  private final Model model;
  public int searchCount;
  public int leafNodeCount;
  private Move bestMove;

  public AI(Model model) {
    this.model = model;
    searchCount = 0;
    leafNodeCount = 0;
  }

  public int evaluate() {
    int evaluation = model.getBitboard().materialCount + model.getBitboard().squareBonuses;
    int currentTurn = model.getCurrentTurn() ? 1 : -1;

    long myKing =
        model.getCurrentTurn()
            ? model.getBitboard().pieceBitboards[6]
            : model.getBitboard().pieceBitboards[0];
    int myKingPosition = model.getBitboard().convertBitboardToInt(myKing);

    long enemyKing =
        model.getCurrentTurn()
            ? model.getBitboard().pieceBitboards[0]
            : model.getBitboard().pieceBitboards[6];
    int enemyKingPosition = model.getBitboard().convertBitboardToInt(enemyKing);

    // not sure why enemy king and my king need to be switched for evaluation to work correctly

    evaluation += boardEdge[enemyKingPosition] * endGameWeight();
    evaluation += distanceToEnemyKingWeight(myKingPosition, enemyKingPosition) * endGameWeight();

    return evaluation * currentTurn;
  }

  public int distanceToEnemyKingWeight(int myKingPosition, int enemyKingPosition) {
    // Calculate the distance between the two kings
    int distance =
        Math.abs(myKingPosition % 8 - enemyKingPosition % 8)
            + Math.abs(myKingPosition / 8 - enemyKingPosition / 8);

    // Assign weights based on the distance
    if (distance <= 2) {
      return 10;
    } else if (distance <= 4) {
      return 5;
    } else {
      return 0;
    }
  }

  public int endGameWeight() {
    int weight = 0;
    int weightIncrement = 4;
    int pieceCount = Long.bitCount(model.getBitboard().occupied);

    if (pieceCount <= 16) {
      weight = weightIncrement * (16 - pieceCount);
    }
    return weight;
  }

  public int search(int depth, int alpha, int beta) {
    long zobristKey = model.getZobristKey();

    TranspositionEntry entry = model.getTranspositionTable().getPosition(zobristKey);

    if (depth != 6) {
      if (model.isDraw() || model.boardState.get(zobristKey) > 1) {
        return 0;
      }
      alpha = Math.max(alpha, -mateScore - depth);
      beta = Math.min(beta, mateScore + depth);

      if (alpha >= beta) {
        return alpha;
      }
    }

    if (entry != null && entry.depth() >= depth) {
      if (entry.flag() == TranspositionEntry.Flag.EXACT) {
        bestMove = entry.bestMove();
        return entry.score();
      } else if (entry.flag() == TranspositionEntry.Flag.LOWER_BOUND && entry.score() >= beta) {
        return entry.score();
      } else if (entry.flag() == TranspositionEntry.Flag.UPPER_BOUND && entry.score() < alpha) {
        return entry.score();
      }
    }

    if (depth == 0) {
      return quiescenceSearch(alpha, beta);
    }

    int bestEvaluation = -99999999;
    Move bestMoveAtCurrentDepth = null;
    TranspositionEntry.Flag flag = TranspositionEntry.Flag.UPPER_BOUND;

    for (Move possibleMove : model.getBitboard().getLegalMoves()) {
      searchCount++;
      model.movePiece(possibleMove, false);
      int evaluation = -search(depth - 1, -beta, -alpha);
      model.undoMove();

      if (evaluation >= beta) {
        model.transpositionTable.storePosition(
            zobristKey, depth, beta, TranspositionEntry.Flag.LOWER_BOUND, possibleMove);
        return beta;
      }

      if (evaluation > bestEvaluation) {
        flag = TranspositionEntry.Flag.EXACT;
        bestEvaluation = evaluation;
        bestMoveAtCurrentDepth = possibleMove;
        alpha = Math.max(alpha, evaluation);
      }
    }

    model.transpositionTable.storePosition(
        zobristKey, depth, bestEvaluation, flag, bestMoveAtCurrentDepth);

    bestMove = bestMoveAtCurrentDepth;
    return bestEvaluation;
  }

  private int quiescenceSearch(int alpha, int beta) {
    int evaluation = evaluate();

    if (evaluation >= beta) {
      return beta;
    }

    if (evaluation > alpha) {
      alpha = evaluation;
    }

    if (model.isCheck()) {
      for (Move move : model.getBitboard().getLegalMoves()) {
        searchCount++;
        model.movePiece(move, false);
        int score = -quiescenceSearch(-beta, -alpha);
        model.undoMove();

        if (score >= beta) {
          return beta;
        }

        if (score > alpha) {
          alpha = score;
        }
      }
    } else {
      for (Move move : model.getBitboard().legalCaptureMoves) {
        searchCount++;
        model.movePiece(move, false);
        int score = -quiescenceSearch(-beta, -alpha);
        model.undoMove();

        if (score >= beta) {
          return beta;
        }

        if (score > alpha) {
          alpha = score;
        }
      }
    }

    return alpha;
  }

  public void fullSearch(int depth) {
    if (depth == 0) {
      leafNodeCount++;
      searchCount++;
      return; // Return value doesn't matter in this context
    }
    for (Move possibleMove : model.getBitboard().getLegalMoves()) {
      model.movePiece(possibleMove, false);
      fullSearch(depth - 1);
      model.undoMove();

      if (depth == -1) { // Set to desired depth to get move breakdowns
        System.out.println(
            model.getMoveNotation(possibleMove.getOrigin(), possibleMove.getDestination())
                + ": "
                + leafNodeCount);
        leafNodeCount = 0;
      }
    }
  }

  public Move getBestMove() {
    return bestMove;
  }
}
