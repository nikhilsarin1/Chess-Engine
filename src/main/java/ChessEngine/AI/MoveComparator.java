package ChessEngine.AI;

import ChessEngine.model.Move;

import java.util.Comparator;

public class MoveComparator implements Comparator<Move> {
    public int compare(Move move1, Move move2) {
        int captureValue1 = getCaptureValue(move1);
        int captureValue2 = getCaptureValue(move2);

        return Integer.compare(captureValue2, captureValue1);
    }

    private int getCaptureValue(Move move) {
        char piece = Character.toLowerCase(move.getPiece());
        char capturedPiece = Character.toLowerCase(move.getCapturedPiece());
        return switch (capturedPiece) {
            case 'q' -> switch (piece) {
                case 'p' -> 30;
                case 'n' -> 29;
                case 'b' -> 28;
                case 'r' -> 27;
                case 'q' -> 26;
                case 'k' -> 25;
                default -> 0;
            };
            case 'r' -> switch (piece) {
                case 'p' -> 24;
                case 'n' -> 23;
                case 'b' -> 22;
                case 'r' -> 21;
                case 'q' -> 20;
                case 'k' -> 19;
                default -> 0;
            };
            case 'b' -> switch (piece) {
                case 'p' -> 18;
                case 'n' -> 17;
                case 'b' -> 16;
                case 'r' -> 15;
                case 'q' -> 14;
                case 'k' -> 13;
                default -> 0;
            };
            case 'n' -> switch (piece) {
                case 'p' -> 12;
                case 'n' -> 11;
                case 'b' -> 10;
                case 'r' -> 9;
                case 'q' -> 8;
                case 'k' -> 7;
                default -> 0;
            };
            case 'p' -> switch (piece) {
                case 'p' -> 6;
                case 'n' -> 5;
                case 'b' -> 4;
                case 'r' -> 3;
                case 'q' -> 2;
                case 'k' -> 1;
                default -> 0;
            };
            default -> 0;
        };
    }
}
