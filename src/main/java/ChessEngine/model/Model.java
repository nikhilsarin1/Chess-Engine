package ChessEngine.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {
    private Bitboard bitboard;
    private List<ModelObserver> observers;
    private boolean currentTurn;
    private int lastMoveOrigin;
    private int lastMoveDestination;

    public Model() {
        this.bitboard = new Bitboard();
        this.observers = new ArrayList<>();
        this.currentTurn = true;
    }

    public Bitboard getBitboard() {
        return bitboard;
    }

    public boolean getCurrentTurn() {
        return currentTurn;
    }

    public int getLastMoveOrigin() {
        return lastMoveOrigin;
    }

    public int getLastMoveDestination() {
        return lastMoveDestination;
    }

    public void changeTurn() {
        currentTurn = !currentTurn;
    }

    public boolean isMoveValid(int origin, int destination, char piece) {
        Map<Character, List<Move>> possibleMoves = bitboard.getPossibleMoves();

        List<Move> pieceMoves = possibleMoves.get(piece);
        for (Move move : pieceMoves) {
            if (move.getOrigin() == origin && move.getDestination() == destination) {
                return true;
            }
        }
        return false;
    }

    public void movePiece(int origin, int destination, char piece) {
        lastMoveOrigin = origin;
        lastMoveDestination = destination;

        long originBitboard = bitboard.convertIntToBitboard(origin);
        long destinationBitboard = bitboard.convertIntToBitboard(destination);

        for (int i = 0; i < bitboard.pieceBitboards.length; i++) {
            if ((bitboard.pieceBitboards[i] & destinationBitboard) != 0) {
                bitboard.pieceBitboards[i] ^= destinationBitboard;
                break; // Exit the loop since only one piece can be captured
            }
        }

        switch (piece) {
            case 'K' -> {
                bitboard.pieceBitboards[0] ^= originBitboard;
                bitboard.pieceBitboards[0] |= destinationBitboard;
            }
            case 'Q' -> {
                bitboard.pieceBitboards[1] ^= originBitboard;
                bitboard.pieceBitboards[1] |= destinationBitboard;
            }
            case 'R' -> {
                bitboard.pieceBitboards[2] ^= originBitboard;
                bitboard.pieceBitboards[2] |= destinationBitboard;
            }
            case 'B' -> {
                bitboard.pieceBitboards[3] ^= originBitboard;
                bitboard.pieceBitboards[3] |= destinationBitboard;
            }
            case 'N' -> {
                bitboard.pieceBitboards[4] ^= originBitboard;
                bitboard.pieceBitboards[4] |= destinationBitboard;
            }
            case 'P' -> {
                bitboard.pieceBitboards[5] ^= originBitboard;
                bitboard.pieceBitboards[5] |= destinationBitboard;
            }
            case 'k' -> {
                bitboard.pieceBitboards[6] ^= originBitboard;
                bitboard.pieceBitboards[6] |= destinationBitboard;
            }
            case 'q' -> {
                bitboard.pieceBitboards[7] ^= originBitboard;
                bitboard.pieceBitboards[7] |= destinationBitboard;
            }
            case 'r' -> {
                bitboard.pieceBitboards[8] ^= originBitboard;
                bitboard.pieceBitboards[8] |= destinationBitboard;
            }
            case 'b' -> {
                bitboard.pieceBitboards[9] ^= originBitboard;
                bitboard.pieceBitboards[9] |= destinationBitboard;
            }
            case 'n' -> {
                bitboard.pieceBitboards[10] ^= originBitboard;
                bitboard.pieceBitboards[10] |= destinationBitboard;
            }
            case 'p' -> {
                bitboard.pieceBitboards[11] ^= originBitboard;
                bitboard.pieceBitboards[11] |= destinationBitboard;
            }
        }
        bitboard.updateBitboard();
        changeTurn();
        notifyObservers();
    }

    public void addObserver(ModelObserver observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (ModelObserver observer : observers) {
            observer.updateMove(this);
        }
    }
}
