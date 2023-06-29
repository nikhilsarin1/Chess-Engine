package com.example.chessproject.model;

import java.util.List;
import java.util.Map;

public class MoveInfo {
    public Square origin;
    public Square destination;
    public Piece capturedPiece;
    public Piece promotedPawn;
    public Square enPassantTarget;
    public int moveCount;
    public Map<Piece, List<Square>> possibleMoves;
    public Map<Piece, List<Square>> legalMoves;
    public Map<Piece, List<Square>> orderedLegalMoves;
    public List<Pawn> enPassantPawns;
    public boolean wasPromotion;
    public boolean wasCastle;
    public boolean wasEnPassant;
    public boolean lastPieceHasNotMoved;
    
    public MoveInfo() {
    }
}
