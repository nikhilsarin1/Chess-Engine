package ChessEngine.model;

public class Move {
    private int origin;
    private int destination;
    private char piece;

    public Move(int origin, int destination, char piece) {
        this.origin = origin;
        this.destination = destination;
        this.piece = piece;
    }

    public int getOrigin() {
        return origin;
    }

    public int getDestination() {
        return destination;
    }

    public char getPiece() {return piece;}
}
