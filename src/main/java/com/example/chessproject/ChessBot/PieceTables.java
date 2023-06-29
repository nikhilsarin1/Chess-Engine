package com.example.chessproject.ChessBot;

public class PieceTables {
    public int getPawnSquareValue(boolean color, int row, int col) {
        int[] pawnTable = (color) ? PAWN_TABLE[0] : PAWN_TABLE[1];
        int index = row * 8 + col;
        return pawnTable[index];
    }

    public int getKnightSquareValue(boolean color, int row, int col) {
        int[] knightTable = (color) ? KNIGHT_TABLE[0] : KNIGHT_TABLE[1];
        int index = row * 8 + col;
        return knightTable[index];
    }

    public int getBishopSquareValue(boolean color, int row, int col) {
        int[] bishopTable = (color) ? BISHOP_TABLE[0] : BISHOP_TABLE[1];
        int index = row * 8 + col;
        return bishopTable[index];
    }

    public int getRookSquareValue(boolean color, int row, int col) {
        int[] rookTable = (color) ? ROOK_TABLE[0] : ROOK_TABLE[1];
        int index = row * 8 + col;
        return rookTable[index];
    }

    public int getQueenSquareValue(boolean color, int row, int col) {
        int[] queenTable = (color) ? QUEEN_TABLE[0] : QUEEN_TABLE[1];
        int index = row * 8 + col;
        return queenTable[index];
    }

    public int getKingSquareValue(boolean color, int row, int col) {
        int[] kingTable = (color) ? KING_TABLE[0] : KING_TABLE[1];
        int index = row * 8 + col;
        return kingTable[index];
    }

    // Piece-square tables for each piece type and color

    public static final int[][] PAWN_TABLE = {
            {   // White pawns
                    0,   0,   0,   0,   0,   0,   0,   0,
                    50,  50,  50,  50,  50,  50,  50,  50,
                    10,  10,  20,  30,  30,  20,  10,  10,
                    5,   5,   10,  25,  25,  10,  5,   5,
                    0,   0,   0,   20,  20,  0,   0,   0,
                    5,  -5,  -10,  0,   0,  -10, -5,   5,
                    5,  10,  10, -20, -20,  10,  10,  5,
                    0,   0,   0,   0,   0,   0,   0,   0
            },
            {   // Black pawns (flipped horizontally)
                    0,   0,   0,   0,   0,   0,   0,   0,
                    5,   10,  10, -20, -20,  10,  10,   5,
                    5,   -5, -10,  0,   0,  -10,  -5,   5,
                    0,   0,   0,   20,  20,  0,   0,    0,
                    5,   5,   10,  25,  25,  10,  5,    5,
                    10,  10,  20,  30,  30,  20,  10,   10,
                    50,  50,  50,  50,  50,  50,  50,   50,
                    0,   0,   0,   0,   0,   0,   0,    0
            }
    };

    public static final int[][] KNIGHT_TABLE = {
            {   // White knights
                    -50, -40, -30, -30, -30, -30, -40, -50,
                    -40, -20,   0,   0,   0,   0, -20, -40,
                    -30,   0,  10,  15,  15,  10,   0, -30,
                    -30,   5,  15,  20,  20,  15,   5, -30,
                    -30,   0,  15,  20,  20,  15,   0, -30,
                    -30,   5,  10,  15,  15,  10,   5, -30,
                    -40, -20,   0,   5,   5,   0, -20, -40,
                    -50, -40, -30, -30, -30, -30, -40, -50
            },
            {   // Black knights (flipped horizontally)
                    -50, -40, -30, -30, -30, -30, -40, -50,
                    -40, -20,   0,   5,   5,   0, -20, -40,
                    -30,   5,  10,  15,  15,  10,   5, -30,
                    -30,   0,  15,  20,  20,  15,   0, -30,
                    -30,   5,  15,  20,  20,  15,   5, -30,
                    -30,   0,  10,  15,  15,  10,   0, -30,
                    -40, -20,   0,   0,   0,   0, -20, -40,
                    -50, -40, -30, -30, -30, -30, -40, -50
            }
    };

    public static final int[][] BISHOP_TABLE = {
            {   // White bishops
                    -20, -10, -10, -10, -10, -10, -10, -20,
                    -10,   0,   0,   0,   0,   0,   0, -10,
                    -10,   0,   5,  10,  10,   5,   0, -10,
                    -10,   5,   5,  10,  10,   5,   5, -10,
                    -10,   0,  10,  10,  10,  10,   0, -10,
                    -10,  10,  10,  10,  10,  10,  10, -10,
                    -10,   5,   0,   0,   0,   0,   5, -10,
                    -20, -10, -10, -10, -10, -10, -10, -20
            },
            {   // Black bishops (flipped horizontally)
                    -20, -10, -10, -10, -10, -10, -10, -20,
                    -10,   5,   0,   0,   0,   0,   5, -10,
                    -10,  10,  10,  10,  10,  10,  10, -10,
                    -10,   0,  10,  10,  10,  10,   0, -10,
                    -10,   5,   5,  10,  10,   5,   5, -10,
                    -10,   0,   5,  10,  10,   5,   0, -10,
                    -10,   0,   0,   0,   0,   0,   0, -10,
                    -20, -10, -10, -10, -10, -10, -10, -20
            }
    };

    public static final int[][] ROOK_TABLE = {
            {   // White rooks
                    0,   0,   0,   0,   0,   0,   0,   0,
                    5,  10,  10,  10,  10,  10,  10,   5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    0,   0,   0,   5,   5,   0,   0,   0
            },
            {   // Black rooks (flipped horizontally)
                    0,   0,   0,   5,   5,   0,   0,   0,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    -5,   0,   0,   0,   0,   0,   0,  -5,
                    5,  10,  10,  10,  10,  10,  10,   5,
                    0,   0,   0,   0,   0,   0,   0,   0
            }
    };

    public static final int[][] QUEEN_TABLE = {
            {   // White queen
                    -20, -10, -10,  -5,  -5, -10, -10, -20,
                    -10,   0,   0,   0,   0,   0,   0, -10,
                    -10,   0,   5,   5,   5,   5,   0, -10,
                    -5,   0,   5,   5,   5,   5,   0,  -5,
                    0,   0,   5,   5,   5,   5,   0,  -5,
                    -10,   5,   5,   5,   5,   5,   0, -10,
                    -10,   0,   5,   0,   0,   0,   0, -10,
                    -20, -10, -10,  -5,  -5, -10, -10, -20
            },
            {   // Black queen (flipped horizontally)
                    -20, -10, -10,  -5,  -5, -10, -10, -20,
                    -10,   0,   5,   0,   0,   0,   0, -10,
                    -10,   5,   5,   5,   5,   5,   0, -10,
                    -5,   0,   5,   5,   5,   5,   0,  -5,
                    0,   0,   5,   5,   5,   5,   0,  -5,
                    -10,   0,   5,   5,   5,   5,   0, -10,
                    -10,   0,   0,   0,   0,   0,   0, -10,
                    -20, -10, -10,  -5,  -5, -10, -10, -20
            }
    };

    public static final int[][] KING_TABLE = {
            {  // White king
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -20, -30, -30, -40, -40, -30, -30, -20,
                    -10, -20, -20, -20, -20, -20, -20, -10,
                    20,  20,   0,   0,   0,   0,  20,  20,
                    20,  30,  10,   0,   0,  10,  30,  20
            },
            {  // Black king
                    20,  30,  10,   0,   0,  10,  30,  20,
                    20,  20,   0,   0,   0,   0,  20,  20,
                    -10, -20, -20, -20, -20, -20, -20, -10,
                    -20, -30, -30, -40, -40, -30, -30, -20,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30,
                    -30, -40, -40, -50, -50, -40, -40, -30
            }
    };
}
