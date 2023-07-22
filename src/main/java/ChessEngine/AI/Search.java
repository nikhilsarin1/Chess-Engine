package ChessEngine.AI;

import ChessEngine.model.Model;
import ChessEngine.model.Move;

public class Search {
  static int mateScore = 1000000;
  public final Evaluation evaluation;
  private final Model model;
  public int searchCount;
  public int leafNodeCount;
  private Move bestMove;

  public Search(Model model) {
    this.model = model;
    this.evaluation = new Evaluation(model);
    searchCount = 0;
    leafNodeCount = 0;
    bestMove = null;
  }

  public int search(int depth, int ply, int alpha, int beta) {
    int alphaOriginal = alpha;

    long zobristKey = model.getZobristKey();

    TranspositionEntry entry = model.transpositionTable.getPosition(zobristKey);

    if (entry != null && entry.ply() >= ply) {
      if (entry.flag() == TranspositionEntry.Flag.EXACT) {
        bestMove = entry.bestMove();
        return entry.score();
      } else if (entry.flag() == TranspositionEntry.Flag.LOWER_BOUND) {
        alpha = Math.max(alpha, entry.score());
      } else if (entry.flag() == TranspositionEntry.Flag.UPPER_BOUND) {
        beta = Math.min(beta, entry.score());
      }
      if (alpha >= beta) {
        return entry.score();
      }
    }

    if (model.isCheckmate()) {
      return -mateScore + ply;
    } else if (model.isDraw()) {
      return 0;
    }

    if (depth == 0) {
      return quiescenceSearch(alpha, beta);
    }

    int evaluation = -999999999;
    Move bestMoveAtCurrentDepth = null;

    for (Move move : model.getBitboard().getLegalMoves()) {
      searchCount++;
      model.movePiece(move, false, depth);
      int score = -search(depth - 1, ply + 1, -beta, -alpha);
      model.undoMove();

      if (score > evaluation) {
        evaluation = score;
        bestMoveAtCurrentDepth = move; // Update the best move at this depth
      }

      alpha = Math.max(alpha, evaluation);

      if (alpha >= beta) {
        model.getBitboard().recordKillerMove(move, depth);
        break;
      }
    }

    TranspositionEntry.Flag flag;

    if (evaluation <= alphaOriginal) {
      flag = TranspositionEntry.Flag.UPPER_BOUND;
    } else if (evaluation >= beta) {
      flag = TranspositionEntry.Flag.LOWER_BOUND;
    } else {
      flag = TranspositionEntry.Flag.EXACT;
    }

    if (evaluation != 0) {
      model.transpositionTable.storePosition(
          zobristKey, ply, evaluation, flag, bestMoveAtCurrentDepth);
    }

    bestMove = bestMoveAtCurrentDepth;
    return evaluation;
  }

  private int quiescenceSearch(int alpha, int beta) {
    int eval = evaluation.evaluate(model.getCurrentTurn());

    if (eval >= beta) {
      return beta;
    }

    if (eval > alpha) {
      alpha = eval;
    }

    if (model.isCheck()) {
      for (Move move : model.getBitboard().getLegalMoves()) {
        searchCount++;
        model.movePiece(move, false, -1);
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
      for (Move move : model.getBitboard().getLegalMoves()) {
        if (move.getCapturedPiece() == ' ') {
          return alpha;
        }
        searchCount++;
        model.movePiece(move, false, -1);
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
      model.movePiece(possibleMove, false, -1);
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

  public void resetBestMove() {
    bestMove = null;
  }
}
