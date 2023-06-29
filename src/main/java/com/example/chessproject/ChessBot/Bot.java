package com.example.chessproject.ChessBot;

import com.example.chessproject.model.ModelImpl;
import com.example.chessproject.model.Piece;
import com.example.chessproject.model.Square;

import java.util.*;

public class Bot {
  private final ModelImpl model;
  private Square origin;
  private Square destination;
  final int pawnValue = 100;
  final int knightValue = 300;
  final int bishopValue = 300;
  final int rookValue = 500;
  final int queenValue = 900;
  public int searchCount;

  public Bot(ModelImpl model) {
    this.model = model;
    searchCount = 0;
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
    for (Piece piece : model.getPieces()) {
      if (piece.getColor() == color) {
        switch (piece.getType()) {
          case PAWN -> material += pawnValue + pieceTables.getPawnSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
          case KNIGHT -> material += knightValue + pieceTables.getKnightSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
          case BISHOP -> material += bishopValue + pieceTables.getBishopSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
          case ROOK -> material += rookValue + pieceTables.getRookSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
          case QUEEN -> material += queenValue + pieceTables.getQueenSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
          case KING -> material += pieceTables.getKingSquareValue(color, piece.getSquare().getRow(), piece.getSquare().getCol());
        }
      }
    }
    return material;
  }

  public int search(int depth, int alpha, int beta) {
    model.searching = true;
    Square bestOrigin = null;
    Square bestDestination = null;

    if (depth == 0) {
      return evaluate();
    }

    if (model.isCheckmate()) {
      return -99999999;
    } else if (model.isDraw()) {
      return 0;
    }

    int bestEvaluation = -999999999;

    for (Map.Entry<Piece, List<Square>> entry : model.getOrderedLegalMoves().entrySet()) {
      Piece piece = entry.getKey();
      List<Square> pieceMoves = entry.getValue();

      for (Square possibleMove : pieceMoves) {
        searchCount++;
        model.movePiece(piece.getSquare(), possibleMove, false);
        int evaluation = -search(depth - 1, -beta, -alpha);
        model.undoMove(false);


        if (evaluation > bestEvaluation) {
          bestEvaluation = evaluation;
          bestOrigin = piece.getSquare();
          bestDestination = possibleMove;
        }

        alpha = Math.max(alpha, evaluation);
        if (alpha >= beta) {
          // Beta cutoff
          break;
        }
      }
    }

    origin = bestOrigin;
    destination = bestDestination;
    return alpha;
  }

  public Square getOrigin() {
    return origin;
  }

  public Square getDestination() {
    return destination;
  }
}
