package ChessEngine.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private char[] chessBoard;
    public long[] pieceBitboards;
    private long occupied;
    private long empty;
    private long whitePieces;
    private long blackPieces;
    private boolean currentTurn;
    private Map<Character, List<Move>> possibleMoves;

    public Bitboard() {
        this.chessBoard = startingBoard();
        this.pieceBitboards = convertCharArrayToBitboards(chessBoard);
        this.currentTurn = true;
        this.possibleMoves = new HashMap<>();
        setWhitePieces();
        setBlackPieces();
        setOccupied();
        setEmpty();
        generatePossibleMoves();
    }

    public char[] startingBoard() {
        return new char[] {
                'r', 'n', 'b', 'q', 'k', 'b', 'n', 'r',
                'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ',
                'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P',
                'R', 'N', 'B', 'Q', 'K', 'B', 'N', 'R'
        };
    }

    public void updateBitboard() {
        setWhitePieces();
        setBlackPieces();
        setOccupied();
        setEmpty();
        currentTurn = !currentTurn;
        generatePossibleMoves();
    }

    public Map<Character, List<Move>> getPossibleMoves() {
        return possibleMoves;
    }

    public void generatePossibleMoves() {
        Map<Character, List<Move>> moves = new HashMap<>();
        if (currentTurn) {
            moves.put('K', kingMoves(pieceBitboards[0]));
            moves.put('Q', queenMoves(pieceBitboards[1]));
            moves.put('R', rookMoves(pieceBitboards[2]));
            moves.put('B', bishopMoves(pieceBitboards[3]));
            moves.put('N', knightMoves(pieceBitboards[4]));
            moves.put('P', whitePawnMoves(pieceBitboards[5]));
        } else {
            moves.put('k', kingMoves(pieceBitboards[6]));
            moves.put('q', queenMoves(pieceBitboards[7]));
            moves.put('r', rookMoves(pieceBitboards[8]));
            moves.put('b', bishopMoves(pieceBitboards[9]));
            moves.put('n', knightMoves(pieceBitboards[10]));
            moves.put('p', blackPawnMoves(pieceBitboards[11]));
        }
        possibleMoves = moves;
    }

    public long convertIntToBitboard(int position) {
        return 1L << ((7 - (position / 8)) * 8 + (position % 8));
    }

    public boolean isOccupiedSquare(int square) {
        long bitPosition = convertIntToBitboard(square);
        return (bitPosition & occupied) != 0L;
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
        this.whitePieces = pieceBitboards[0]
                | pieceBitboards[1]
                | pieceBitboards[2]
                | pieceBitboards[3]
                | pieceBitboards[4]
                | pieceBitboards[5];
    }

    public void setBlackPieces() {
        this.blackPieces = pieceBitboards[6]
                | pieceBitboards[7]
                | pieceBitboards[8]
                | pieceBitboards[9]
                | pieceBitboards[10]
                | pieceBitboards[11];
    }

    public List<Move> whitePawnMoves(long whitePawns) {
        List<Move> moveList = new ArrayList<>();
        long[] individualPawns = getIndividualPieceBitboards(whitePawns);

        for (long pawn : individualPawns) {
            int origin = convertBitboardToInt(pawn);
            long moveDestinationsBitboard = whitePawnMove(pawn);
            int[] moveDestinations = convertBitboardToArrayOfIndexes(moveDestinationsBitboard);
            for (int destination : moveDestinations) {
                Move move = new Move(origin, destination, 'P');
                moveList.add(move);
            }
            long captureDestinationsBitboard = whitePawnCapture(pawn);
            int[] captureDestinations = convertBitboardToArrayOfIndexes(captureDestinationsBitboard);
            for (int destination : captureDestinations) {
                Move move = new Move(origin, destination, 'P');
                moveList.add(move);
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
                Move move = new Move(origin, destination, 'p');
                moveList.add(move);
            }
            long captureDestinationsBitboard = blackPawnCapture(pawn);
            int[] captureDestinations = convertBitboardToArrayOfIndexes(captureDestinationsBitboard);
            for (int destination : captureDestinations) {
                Move move = new Move(origin, destination, 'p');
                moveList.add(move);
            }
        }
        return moveList;
    }

    public long whitePawnCapture(long whitePawn) {
        long leftCaptures = ((whitePawn & ~fileMasks[0]) << 7) & blackPieces;
        long rightCaptures = ((whitePawn & ~fileMasks[7]) << 9) & blackPieces;
        return leftCaptures | rightCaptures;
    }

    public long blackPawnCapture(long blackPawn) {
        long leftCaptures = (blackPawn & ~fileMasks[7]) >> 7 & whitePieces;
        long rightCaptures = (blackPawn & ~fileMasks[0]) >> 9 & whitePieces;
        return leftCaptures | rightCaptures;
    }

    public long whitePawnMove(long whitePawn) {
        long forwardOne = ((whitePawn & ~rankMasks[7]) << 8) & ~occupied;
        long forwardTwo = ((whitePawn & rankMasks[1]) << 16) & (empty << 8) & ~occupied;
        return forwardOne | forwardTwo;
    }

    public long blackPawnMove(long blackPawn) {
        long forwardOne = ((blackPawn & ~rankMasks[0]) >> 8) & ~occupied;
        long forwardTwo = ((blackPawn & rankMasks[6]) >> 16) & (empty >> 8) & ~occupied;
        return forwardOne | forwardTwo;
    }

    public List<Move> rookMoves(long rooks) {
        char color = currentTurn ? 'R' : 'r';
        List<Move> moveList = new ArrayList<>();
        long[] individualRooks = getIndividualPieceBitboards(rooks);
        for (long rook : individualRooks) {
            int origin = convertBitboardToInt(rook);
            long destinationsBitboard = rookMove(rook);
            int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
            for (int destination : destinations) {
                Move move = new Move(origin, destination, color);
                moveList.add(move);
            }
        }
        return moveList;
    }

    public long rookMove(long rook) {
        long currentPieces = currentTurn ? whitePieces : blackPieces;
        int position = Long.numberOfTrailingZeros(rook);
        long rank = rankMasks[position / 8];
        long file = fileMasks[position % 8];
        long horizontal =
                (occupied - 2 * rook) ^ Long.reverse(Long.reverse(occupied) - 2 * Long.reverse(rook));
        long vertical =
                (occupied & file) - (2 * rook)
                        ^ Long.reverse(Long.reverse(occupied & file) - (2 * Long.reverse(rook)));

        return ((horizontal & rank) | (vertical & file)) & ~currentPieces;
        // whitePieces will have to be changed to current turn's pieces;
    }

    public List<Move> bishopMoves(long bishops) {
        char color = currentTurn ? 'B' : 'b';
        List<Move> moveList = new ArrayList<>();
        long[] individualBishops = getIndividualPieceBitboards(bishops);
        for (long bishop : individualBishops) {
            int origin = convertBitboardToInt(bishop);
            long destinationsBitboard = bishopMove(bishop);
            int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
            for (int destination : destinations) {
                Move move = new Move(origin, destination, color);
                moveList.add(move);
            }
        }
        return moveList;
    }

    public long bishopMove(long bishop) {
        long currentPieces = currentTurn ? whitePieces : blackPieces;
        int position = Long.numberOfTrailingZeros(bishop);
        long diagonal = diagonalMasks[(position / 8) + (position % 8)];
        long antiDiagonal = antiDiagonalMasks[(7 + (position / 8) - (position % 8))];
        long diagonalMove =
                ((occupied & diagonal) - (2 * bishop))
                        ^ Long.reverse(Long.reverse(occupied & diagonal) - (2 * Long.reverse(bishop)));
        long antiDiagonalMove =
                ((occupied & antiDiagonal) - (2 * bishop))
                        ^ Long.reverse(Long.reverse(occupied & antiDiagonal) - (2 * Long.reverse(bishop)));

        return ((diagonalMove & diagonal) | (antiDiagonalMove & antiDiagonal))
                & ~currentPieces;
        // whitePieces needs to be replaced with current turn's pieces
    }

    public List<Move> queenMoves(long queens) {
        char color = currentTurn ? 'Q' : 'q';
        List<Move> moveList = new ArrayList<>();
        long[] individualQueens = getIndividualPieceBitboards(queens);
        for (long queen : individualQueens) {
            int origin = convertBitboardToInt(queen);
            long destinationsBitboard = queenMove(queen);
            int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
            for (int destination : destinations) {
                Move move = new Move(origin, destination, color);
                moveList.add(move);
            }
        }
        return moveList;
    }

    public long queenMove(long queen) {
        long currentPieces = currentTurn ? whitePieces : blackPieces;
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

        return ((horizontal & rank)
                | (vertical & file)
                | (diagonalMove & diagonal)
                | (antiDiagonalMove & antiDiagonal))
                & ~currentPieces;
        // whitePieces will need to be changed to current turn's pieces
    }

    public List<Move> knightMoves(long knights) {
        char color = currentTurn ? 'N' : 'n';
        List<Move> moveList = new ArrayList<>();
        long[] individualKnights = getIndividualPieceBitboards(knights);
        for (long knight : individualKnights) {
            int origin = convertBitboardToInt(knight);
            long destinationsBitboard = knightMove(knight);
            int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
            for (int destination : destinations) {
                Move move = new Move(origin, destination, color);
                moveList.add(move);
            }
        }
        return moveList;
    }

    public long knightMove(long knight) {
        long currentPieces = currentTurn ? whitePieces : blackPieces;
        long nne = (knight << 17) & ~(fileMasks[0]);
        long nee = (knight << 10) & ~(fileMasks[0] | fileMasks[1]);
        long see = (knight >> 6) & ~(fileMasks[0] | fileMasks[1]);
        long sse = (knight >> 15) & ~(fileMasks[0]);
        long nnw = (knight << 15) & ~(fileMasks[7]);
        long nww = (knight << 6) & ~(fileMasks[6] | fileMasks[7]);
        long sww = (knight >> 10) & ~(fileMasks[6] | fileMasks[7]);
        long ssw = (knight >> 17) & ~(fileMasks[7]);

        return (nne | nee | see | sse | nnw | nww | sww | ssw) & ~currentPieces;
        // whitePieces would have to be updated to current turn's pieces
    }

    public List<Move> kingMoves(long king) {
        long currentPieces = currentTurn ? whitePieces : blackPieces;
        char color = currentTurn ? 'K' : 'k';
        List<Move> moveList = new ArrayList<>();

        long n = (king & ~rankMasks[7]) << 8;
        long s = (king & ~rankMasks[0]) >> 8;
        long e = (king & ~fileMasks[7]) << 1;
        long w = (king & ~fileMasks[0]) >> 1;
        long ne = (king & ~rankMasks[7] & ~fileMasks[7]) << 9;
        long se = (king & ~rankMasks[0] & ~fileMasks[7]) >> 7;
        long nw = (king & ~rankMasks[7] & ~fileMasks[0]) << 7;
        long sw = (king & ~rankMasks[0] & ~fileMasks[0]) >> 9;
        long destinationsBitboard = (n | s | e | w | ne | se | nw | sw) & ~currentPieces;

        int origin = convertBitboardToInt(king);
        int[] destinations = convertBitboardToArrayOfIndexes(destinationsBitboard);
        for (int destination : destinations) {
            Move move = new Move(origin, destination, color);
            moveList.add(move);
        }
        return moveList;
    }
}
