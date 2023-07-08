package ChessEngine.AI;

import ChessEngine.model.Model;
import ChessEngine.model.Move;

import java.util.List;
import java.util.Map;

public class AI {
  final int pawnValue = 100;
  final int knightValue = 300;
  final int bishopValue = 300;
  final int rookValue = 500;
  final int queenValue = 900;
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
    int whiteEval = countMaterial(true);
    int blackEval = countMaterial(false);

    int evaluation = whiteEval - blackEval;

    int currentTurn = model.getCurrentTurn() ? 1 : -1;

    return evaluation * currentTurn;
  }

  public int countMaterial(boolean color) {
    int material = 0;
    PieceTables pieceTables = new PieceTables();

    if (color) {
      long[] queens =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[1]);
      for (long queen : queens) {
        int position = model.getBitboard().convertBitboardToInt(queen);
        material += queenValue + pieceTables.getQueenSquareValue(true, position);
      }
      long[] rooks =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[2]);
      for (long rook : rooks) {
        int position = model.getBitboard().convertBitboardToInt(rook);
        material += rookValue + pieceTables.getRookSquareValue(true, position);
      }
      long[] bishops =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[3]);
      for (long bishop : bishops) {
        int position = model.getBitboard().convertBitboardToInt(bishop);
        material += bishopValue + pieceTables.getBishopSquareValue(true, position);
      }
      long[] knights =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[4]);
      for (long knight : knights) {
        int position = model.getBitboard().convertBitboardToInt(knight);
        material += knightValue + pieceTables.getKnightSquareValue(true, position);
      }
      long[] pawns =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[5]);
      for (long pawn : pawns) {
        int position = model.getBitboard().convertBitboardToInt(pawn);
        material += pawnValue + pieceTables.getPawnSquareValue(true, position);
      }
      long king = model.getBitboard().pieceBitboards[0];
      int position = model.getBitboard().convertBitboardToInt(king);
      material += pieceTables.getKingSquareValue(true, position);
    } else {
      long[] queens =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[7]);
      for (long queen : queens) {
        int position = model.getBitboard().convertBitboardToInt(queen);
        material += queenValue + pieceTables.getQueenSquareValue(false, position);
      }
      long[] rooks =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[8]);
      for (long rook : rooks) {
        int position = model.getBitboard().convertBitboardToInt(rook);
        material += rookValue + pieceTables.getRookSquareValue(false, position);
      }
      long[] bishops =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[9]);
      for (long bishop : bishops) {
        int position = model.getBitboard().convertBitboardToInt(bishop);
        material += bishopValue + pieceTables.getBishopSquareValue(false, position);
      }
      long[] knights =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[10]);
      for (long knight : knights) {
        int position = model.getBitboard().convertBitboardToInt(knight);
        material += knightValue + pieceTables.getKnightSquareValue(false, position);
      }
      long[] pawns =
          model.getBitboard().getIndividualPieceBitboards(model.getBitboard().pieceBitboards[11]);
      for (long pawn : pawns) {
        int position = model.getBitboard().convertBitboardToInt(pawn);
        material += pawnValue + pieceTables.getPawnSquareValue(false, position);
      }
      long king = model.getBitboard().pieceBitboards[6];
      int position = model.getBitboard().convertBitboardToInt(king);
      material += pieceTables.getKingSquareValue(false, position);
    }
    return material;
  }

  public int search(int depth, int alpha, int beta) {
    Move move = null;

    if (depth == 0) {
      return evaluate();
    }

    if (model.isCheckmate()) {
      return -99999999;
    } else if (model.isDraw()) {
      return 0;
    }

    int bestEvaluation = -999999999;

    for (Map.Entry<Character, List<Move>> entry : model.getLegalMoves().entrySet()) {
      List<Move> pieceMoves = entry.getValue();

      for (Move possibleMove : pieceMoves) {
        searchCount++;
        model.movePiece(possibleMove, true);
        int evaluation = -search(depth - 1, -beta, -alpha);
        model.undoMove();

        if (evaluation > bestEvaluation) {
          bestEvaluation = evaluation;
          move = possibleMove;
        }

        alpha = Math.max(alpha, evaluation);
        if (alpha >= beta) {
          // Beta cutoff
          break;
        }
      }
    }

    bestMove = move;
    return alpha;
  }

  public void fullSearch(int depth) {
    if (depth == 0) {
      leafNodeCount++;
      searchCount++;
      return; // Return value doesn't matter in this context
    }
    for (Map.Entry<Character, List<Move>> entry : model.getLegalMoves().entrySet()) {
      List<Move> pieceMoves = entry.getValue();
      for (Move possibleMove : pieceMoves) {
        model.movePiece(possibleMove, true);
        fullSearch(depth - 1);
        model.undoMove();
      }
    }
  }

  public Move getBestMove() {
    return bestMove;
  }
}
