package ChessEngine.controller;

import ChessEngine.model.Model;

public class Controller {
    private Model model;
    private int origin;
    private char originPiece;
    public Controller(Model model) {
        this.model = model;
        this.origin = -1;
    }

    public int getOrigin() {
        return origin;
    }

    public void resetOrigin() {
        origin = -1;
    }

    public void clickOrigin(int square, char piece) {
        if (!model.getBitboard().isOccupiedSquare(square)) {
            throw new IllegalArgumentException();
        }
        this.origin = square;
        this.originPiece = piece;
    }

    public void clickDestination(int square) {
        if (!model.isMoveValid(origin, square, originPiece)) {
            throw new IllegalArgumentException();
        }
        model.movePiece(origin, square, originPiece);
        this.origin = -1;
    }
}
