package com.example.chessproject.model;

import java.util.*;

public class ModelImpl implements Model {
  private final ChessBoard board;
  private final List<ModelObserver> observers;
  private final Map<String, Integer> positionCountMap;
  private final List<Piece> pieces;
  private boolean currentTurn;
  private int moveCount;
  private PieceType promotedPieceType;
  private Square enPassantTarget;
  private Square lastMoveOrigin;
  private Square lastMoveDestination;
  private Map<Piece, List<Square>> possibleMoves;
  private Map<Piece, List<Square>> legalMoves;
  private Map<Piece, List<Square>> orderedLegalMoves;
  private boolean selectedPlayer;
  private final Stack<MoveInfo> moveStack;
  public boolean searching;

  public ModelImpl() {
    this.board = new ChessBoard();
    this.currentTurn = true;
    this.observers = new ArrayList<>();
    this.moveCount = 0;
    this.positionCountMap = new HashMap<>();
    this.promotedPieceType = null;
    this.lastMoveOrigin = null;
    this.lastMoveDestination = null;
    this.pieces = new ArrayList<>();
    this.possibleMoves = null;
    this.legalMoves = null;
    this.orderedLegalMoves = null;
    this.selectedPlayer = true;
    moveStack = new Stack<>();
  }

  public void changeTurn() {
    this.currentTurn = !currentTurn;
  }
  public Square getLastMoveOrigin() {
    return lastMoveOrigin;
  }

  public void setLastMoveOrigin(Square square) {
    lastMoveOrigin = square;
  }

  public Square getLastMoveDestination() {
    return lastMoveDestination;
  }

  public void setLastMoveDestination(Square square) {
    lastMoveDestination = square;
  }

  public ChessBoard getBoard() {
    return this.board;
  }

  public boolean getCurrentTurn() {
    return currentTurn;
  }

  public List<Piece> getPieces() { return pieces;}

  public void setPromotedPieceType(PieceType type) {
    this.promotedPieceType = type;
  }

  public boolean getSelectedPlayer() {return selectedPlayer;}

  public void setSelectedPlayer(boolean bool) {selectedPlayer = bool;}

  public Map<Piece, List<Square>> getLegalMoves() {
    return this.legalMoves;
  }

  public Map<Piece, List<Square>> getOrderedLegalMoves() {
    return this.orderedLegalMoves;
  }

  public void generatePossibleMoves() {
    // Create the map to store piece and possible moves
    Map<Piece, List<Square>> possibleMoves = new HashMap<>();

    // Iterate through every piece and generate a list of possible squares it can move to
    for (Piece piece : pieces) {
      if (piece.getColor() == currentTurn) {
        List<Square> pieceMoves = piece.getPossibleMoves(board);
        possibleMoves.put(piece, pieceMoves);
      }
    }

    this.possibleMoves = possibleMoves;
  }

  public void generateLegalMoves() {
    // Create the map to store piece and legal moves
    Map<Piece, List<Square>> legalMoves = new HashMap<>();

    // Determine which possible moves for every piece are legal moves
    for (Map.Entry<Piece, List<Square>> entry : possibleMoves.entrySet()) {
      List<Square> pieceLegalMoves = new ArrayList<>();
      Piece piece = entry.getKey();
      List<Square> pieceMoves = entry.getValue();

      for (Square possibleMove : pieceMoves) {
        if (notInCheck(piece.getSquare(), possibleMove)) {
          pieceLegalMoves.add(possibleMove);
        }
      }
      if (!pieceLegalMoves.isEmpty()) {
        legalMoves.put(piece, pieceLegalMoves);
      }
    }

    this.legalMoves = legalMoves;
  }

  public void generateOrderedLegalMoves() {
    // Create the map to store piece and ordered legal moves
    Map<Piece, List<Square>> orderedLegalMoves = new HashMap<>();

    // Iterate over each piece and its legal moves
    for (Map.Entry<Piece, List<Square>> entry : legalMoves.entrySet()) {
      Piece piece = entry.getKey();
      List<Square> pieceMoves = entry.getValue();

      List<Square> orderedMoves = new ArrayList<>();

      // First, add capturing moves
      for (Square possibleMove : pieceMoves) {
        if (possibleMove.isOccupied()) {
          orderedMoves.add(possibleMove);
        }
      }

      // Next, add non-capturing moves
      for (Square possibleMove : pieceMoves) {
        if (!possibleMove.isOccupied()) {
          orderedMoves.add(possibleMove);
        }
      }

      if (!orderedMoves.isEmpty()) {
        orderedLegalMoves.put(piece, orderedMoves);
      }
    }

    this.orderedLegalMoves = orderedLegalMoves;
  }

  public List<Square> opponentPossibleMoves(List<Piece> pieces) {
    // Generate a list of all the squares the opponent is attacking
    List<Square> opponentPossibleMoves = new ArrayList<>();
    for (Piece piece : pieces) {
      if (piece.getColor() != currentTurn) {
        List<Square> pieceMoves = piece.getPossibleMoves(board);
        opponentPossibleMoves.addAll(pieceMoves);
      }
    }

    return opponentPossibleMoves;
  }

  public boolean isMoveValid(Square currentSquare, Square newSquare) {
    // Get the piece from the current square
    Piece piece = currentSquare.getPiece();

    // Check if move is in legal moves
    List<Square> pieceLegalMoves = this.legalMoves.get(piece);

    // Check if piece has no legal moves and therefore the move is not valid
    if (pieceLegalMoves == null) {
      return false;
    }

    for (Square square : pieceLegalMoves) {
      if (square == newSquare) {
        return true;
      }
    }

    return false;
  }

  public void movePiece(Square currentSquare, Square newSquare, boolean isActualMove) {

    // Set the last move origin and destination squares
    setLastMoveOrigin(currentSquare);
    setLastMoveDestination(newSquare);

    // Calculate the column and row differences between the squares
    int colDiff = newSquare.getCol() - currentSquare.getCol();
    int rowDiff = newSquare.getRow() - currentSquare.getRow();

    // Get the piece from the current square
    Piece piece = currentSquare.getPiece();

    MoveInfo moveInfo = new MoveInfo();
    moveInfo.origin = currentSquare;
    moveInfo.destination = newSquare;
    moveInfo.possibleMoves = possibleMoves;
    moveInfo.legalMoves = legalMoves;
    moveInfo.orderedLegalMoves = orderedLegalMoves;
    moveInfo.lastPieceHasNotMoved = !piece.hasMoved();
    moveInfo.enPassantTarget = enPassantTarget;
    enPassantTarget = null;

            // If the moved piece is not a pawn and a piece isn't captured, update the move count for 50
    // move rule check, otherwise reset it to 0
    moveInfo.moveCount = moveCount;
    if (newSquare.isOccupied() || piece.getType() == PieceType.PAWN) {
      moveCount = 0;
    } else {
      moveCount++;
    }

    // Check if the piece is a king and if it is performing a castling move
    if (piece.getType() == PieceType.KING)
         {
      if (colDiff == 2) {
        castleKing(currentSquare, true);
        moveInfo.wasCastle = true;
      } else if (colDiff == -2) {
        castleKing(currentSquare, false);
        moveInfo.wasCastle = true;
      } else {
        moveInfo.wasCastle = false;
      }
    } else {
      moveInfo.wasCastle = false;
    }

    // Remove piece from piece list if it is captured
    if (newSquare.isOccupied()) {
      moveInfo.capturedPiece = newSquare.getPiece();
      pieces.remove(newSquare.getPiece());
      board.getSquare(newSquare.getRow(), newSquare.getCol()).setPiece(null);
    } else {
      moveInfo.capturedPiece = null;
    }

    // Check if pawn is performing enPassant and capture pawn if it is
    if (piece instanceof Pawn pawn) {
      if (newSquare == pawn.getEnPassantTarget()) {
        board.getSquare(currentSquare.getRow(), newSquare.getCol()).getPiece().setSquare(null);
        moveInfo.capturedPiece = board.getSquare(currentSquare.getRow(), newSquare.getCol()).getPiece();
        pieces.remove(board.getSquare(currentSquare.getRow(), newSquare.getCol()).getPiece());
        board.getSquare(currentSquare.getRow(), newSquare.getCol()).setPiece(null);
        moveInfo.wasEnPassant = true;
      } else {
        moveInfo.wasEnPassant = false;
      }
    } else {
      moveInfo.wasEnPassant = false;
    }

    moveInfo.enPassantPawns = new ArrayList<>();

    for (Piece piece1 : pieces) {
      if (piece1 instanceof Pawn pawn) {
        if (pawn.getEnPassantTarget() != null) {
          moveInfo.enPassantPawns.add(pawn);
        }
      }
    }

    // Reset enPassant target for every pawn after a move
    resetEnPassantTarget();

    // Set the piece's hasMoved flag, update its square, and update the board state
    piece.setHasMoved(true);
    piece.setSquare(newSquare);


    // Update the piece on the board
    board.getSquare(currentSquare.getRow(), currentSquare.getCol()).setPiece(null);
    board.getSquare(newSquare.getRow(), newSquare.getCol()).setPiece(piece);

    // Check if the piece is a pawn and if it reached the promotion rank and check for possible en
    // passant
    if (piece.getType() == PieceType.PAWN) {
      if (newSquare.getRow() == 0) {
        moveInfo.promotedPawn = newSquare.getPiece();
        notifyPromotion(newSquare);
        promotePawn(newSquare);
        moveInfo.wasPromotion = true;
      } else if (newSquare.getRow() == 7) {
        moveInfo.promotedPawn = newSquare.getPiece();
        notifyPromotion(newSquare);
        promotePawn(newSquare);
        moveInfo.wasPromotion = true;
      } else if (Math.abs(rowDiff) == 2) {
        checkEnPassantSquare(piece);
        moveInfo.wasPromotion = false;
        if (currentTurn) {
          enPassantTarget = board.getSquare(newSquare.getRow() + 1, newSquare.getCol());
        } else {
          enPassantTarget = board.getSquare(newSquare.getRow() - 1, newSquare.getCol());
        }
      } else {
        moveInfo.wasPromotion = false;
      }
    } else {
      moveInfo.wasPromotion = false;
    }


    // Change the turn, update castling rights, generate the new possible moves and legal moves, and notify all observers
    moveStack.push(moveInfo);
    changeTurn();
    setIllegalCastle();
    generatePossibleMoves();
    generateLegalMoves();
    generateOrderedLegalMoves();

    if (isActualMove) {
      // If board is a new position, add new key, otherwise increment count of the key
      String str = generateBoardState();
      positionCountMap.put(str, positionCountMap.getOrDefault(str, 0) + 1);
      notifyObservers();
    }
  }

  public void undoMove(boolean bool) {
    MoveInfo moveInfo = moveStack.pop();
    moveCount = moveInfo.moveCount;
    Piece piece;

    if (moveInfo.enPassantTarget != null) {
      enPassantTarget = moveInfo.enPassantTarget;
    }

    if (!moveInfo.enPassantPawns.isEmpty()) {
      for (Pawn pawn : moveInfo.enPassantPawns) {
        pawn.setEnPassantTarget(moveInfo.enPassantTarget);
      }
    }

    if (moveInfo.wasPromotion) {
      piece = moveInfo.promotedPawn;
      pieces.add(piece);
      pieces.remove(moveInfo.destination.getPiece());
    } else {
      piece = moveInfo.destination.getPiece();
    }

    piece.setSquare(moveInfo.origin);
    if (moveInfo.lastPieceHasNotMoved) {
      piece.setHasMoved(false);
    }

    board.getSquare(moveInfo.origin.getRow(), moveInfo.origin.getCol()).setPiece(piece);
    board.getSquare(moveInfo.destination.getRow(), moveInfo.destination.getCol()).setPiece(null);

    if (moveInfo.capturedPiece != null && !moveInfo.wasEnPassant) {
      board
          .getSquare(moveInfo.destination.getRow(), moveInfo.destination.getCol())
          .setPiece(moveInfo.capturedPiece);
      moveInfo.capturedPiece.setSquare(moveInfo.destination);
      pieces.add(moveInfo.capturedPiece);
    }

    if (moveInfo.wasCastle) {
      if (moveInfo.destination.getCol() == 2) {
        Piece rook = board.getSquare(piece.getSquare().getRow(), 3).getPiece();
        rook.setSquare(board.getSquare(piece.getSquare().getRow(), 0));
        board.getSquare(piece.getSquare().getRow(), 3).setPiece(null);
        board.getSquare(piece.getSquare().getRow(), 0).setPiece(rook);
        rook.setHasMoved(false);
      }
      if (moveInfo.destination.getCol() == 6) {
        Piece rook = board.getSquare(piece.getSquare().getRow(), 5).getPiece();
        rook.setSquare(board.getSquare(piece.getSquare().getRow(), 7));
        board.getSquare(piece.getSquare().getRow(), 5).setPiece(null);
        board.getSquare(piece.getSquare().getRow(), 7).setPiece(rook);
        rook.setHasMoved(false);
      }
    }

    if (moveInfo.wasEnPassant) {
      board
          .getSquare(moveInfo.origin.getRow(), moveInfo.destination.getCol())
          .setPiece(moveInfo.capturedPiece);
      moveInfo.capturedPiece.setSquare(board.getSquare(moveInfo.origin.getRow(), moveInfo.destination.getCol()));
      pieces.add(moveInfo.capturedPiece);
    }



    changeTurn();
    this.possibleMoves = moveInfo.possibleMoves;
    this.legalMoves = moveInfo.legalMoves;
    this.orderedLegalMoves = moveInfo.orderedLegalMoves;
    setIllegalCastle();
    if (bool) {
      notifyObservers();
    }
  }

  private void resetEnPassantTarget() {
    // Resets any pawn's enPassantTarget
    for (Piece piece : pieces) {
      if (piece instanceof Pawn pawn) {
          pawn.setEnPassantTarget(null);
      }
    }
  }

  private boolean isCheck(boolean color, List<Square> possibleMoves) {
    // Find the square where the king of the specified color is located
    Square kingSquare = findKing(color).getSquare();

    // Iterate over all the opponent's pieces and identify their possible moves

      for (Square possibleMove : possibleMoves) {
        if (possibleMove == kingSquare) {
          return true;
        }
      }



    // No check found
    return false;
  }

  public boolean isCheckmate() {
    // Check if the current player is not in check, which mean's it's not checkmate
    if (!isCheck(currentTurn, opponentPossibleMoves(pieces))) {
      return false;
    }

    // No legal moves means the king is in checkmate
    return this.getLegalMoves().isEmpty();
  }

  public boolean isDraw() {
    // Check if game is drawn from all possible drawing conditions
    return isStalemate()
        || isFiftyMoveDraw()
        || isInsufficientMaterial()
        || isThreeFoldRepetition();
  }

  public boolean isStalemate() {
    // Check if the current player is in check, which mean's it's not stalemate
    if (isCheck(currentTurn, opponentPossibleMoves(pieces))) {
      return false;
    }

    // No legal moves means the king is in stalemate
    return this.getLegalMoves().isEmpty();
  }

  public boolean isFiftyMoveDraw() {
    // Game is drawn if each player makes 50 moves without moving a pawn or capturing a piece
    return moveCount >= 100;
  }

  public boolean isInsufficientMaterial() {
    // Create separate lists to store white and black pieces
    List<Piece> whitePieces = new ArrayList<>();
    List<Piece> blackPieces = new ArrayList<>();

    // Iterate through all the pieces
    for (Piece piece : pieces) {
      // Check if the piece type is a pawn, rook, or queen
      // If so, it is considered sufficient material for checkmate
      if (piece.getType() == PieceType.PAWN
          || piece.getType() == PieceType.ROOK
          || piece.getType() == PieceType.QUEEN) {
        return false;
      }

      // Separate the pieces into white and black based on their color
      if (piece.getColor()) {
        whitePieces.add(piece);
      } else {
        blackPieces.add(piece);
      }
    }

    // Check if the number of white and black pieces is less than or equal to 2
    // If so, it is considered insufficient material for checkmate
    return whitePieces.size() <= 2 && blackPieces.size() <= 2;
  }

  public boolean isThreeFoldRepetition() {
    // Iterate over the counts of positions stored in the positionCountMap
    for (int count : positionCountMap.values()) {
      // If the same board state is reached 3 times, it indicates threefold repetition
      if (count >= 3) {
        return true;
      }
    }
    // No threefold repetition found
    return false;
  }

  private boolean notInCheck(Square currentSquare, Square newSquare) {
    boolean check;

    // Create a copy of the pieces list
    List<Piece> piecesCopy = new ArrayList<>(pieces);

    // Get the piece from the current square
    Piece piece = currentSquare.getPiece();

    // Set the new square as the piece's current square
    piece.setSquare(newSquare);

    // Temporarily remove the piece from the current square
    board.getSquare(currentSquare.getRow(), currentSquare.getCol()).setPiece(null);

    // Save the piece that was originally on the new square
    Piece save_piece = board.getSquare(newSquare.getRow(), newSquare.getCol()).getPiece();

    // Remove piece in original square from the copied piece list
    piecesCopy.remove(save_piece);

    // Move the piece to the new square
    board.getSquare(newSquare.getRow(), newSquare.getCol()).setPiece(piece);

    // Check if the current player is in check
    check = isCheck(currentTurn, opponentPossibleMoves(piecesCopy));

    // Restore the piece's original square
    piece.setSquare(currentSquare);

    // Restore the piece on the current square
    board.getSquare(currentSquare.getRow(), currentSquare.getCol()).setPiece(piece);

    // Restore the original piece on the new square
    board.getSquare(newSquare.getRow(), newSquare.getCol()).setPiece(save_piece);

    // Return true if the current player is not in check after the move, false otherwise
    return !check;
  }

  private void setIllegalCastle() {
    // Locate the king of the current player's move
    King king = findKing(currentTurn);

    // Set default castle rights to true
    king.setKingSideCastle(true);
    king.setQueenSideCastle(true);

    // King cannot castle if it has already moved
    if (king.hasMoved()) {
      king.setKingSideCastle(false);
      king.setQueenSideCastle(false);
      return;
    }

    // Check if the queen side has a rook that hasn't moved and castling isn't blocked
    if (board.getSquare(king.getSquare().getRow(), 0).isOccupied()) {
      Piece cornerPiece = board.getSquare(king.getSquare().getRow(), 0).getPiece();
      if (cornerPiece.hasMoved() || cornerPiece.getType() != PieceType.ROOK) {
        king.setQueenSideCastle(false);
      } else if (!cornerPiece.isPossibleMove(board.getSquare(king.getSquare().getRow(), 3), board)) {
        king.setQueenSideCastle(false);
      }
    } else {
      king.setQueenSideCastle(false);
    }

    // Check if the king side has a rook that hasn't moved and castling isn't blocked
    if (board.getSquare(king.getSquare().getRow(), 7).isOccupied()) {
      Piece cornerPiece = board.getSquare(king.getSquare().getRow(), 7).getPiece();
      if (cornerPiece.hasMoved() || cornerPiece.getType() != PieceType.ROOK) {
        king.setKingSideCastle(false);
      } else if (!cornerPiece.isPossibleMove(board.getSquare(king.getSquare().getRow(), 5), board)) {
        king.setKingSideCastle(false);
      }
    } else {
      king.setKingSideCastle(false);
    }



      for (Square possibleMove : opponentPossibleMoves(pieces)) {
        //King cannot castle if it is in check
        if (possibleMove == king.getSquare()) {
          king.setKingSideCastle(false);
          king.setQueenSideCastle(false);
        }

        // Check if king side castle is blocked by an opposing piece
        if (possibleMove == board.getSquare(king.getSquare().getRow(), 5)) {
          king.setKingSideCastle(false);
        }

        // Check if queen side castle is blocked by an opposing piece
        if (possibleMove == board.getSquare(king.getSquare().getRow(), 4)
                || possibleMove == board.getSquare(king.getSquare().getRow(), 3)) {
          king.setQueenSideCastle(false);
        }
      }

  }

  private void castleKing(Square currentSquare, boolean direction) {
    if (direction) {
      // Get the king-side rook and update the hasMoved flag
      Piece piece = board.getSquare(currentSquare.getRow(), 7).getPiece();
      piece.setHasMoved(true);

      // Move rook over 2 squares to complete king-side castle
      piece.setSquare(board.getSquare(currentSquare.getRow(), 5));
      board.getSquare(currentSquare.getRow(), 7).setPiece(null);
      board.getSquare(currentSquare.getRow(), 5).setPiece(piece);
    } else {
      // Get the queen-side rook and update the hasMoved flag
      Piece piece = board.getSquare(currentSquare.getRow(), 0).getPiece();
      piece.setHasMoved(true);

      // Move rook over 3 squares to complete queen-side castle
      piece.setSquare(board.getSquare(currentSquare.getRow(), 3));
      board.getSquare(currentSquare.getRow(), 0).setPiece(null);
      board.getSquare(currentSquare.getRow(), 3).setPiece(piece);
    }
  }

  private King findKing(boolean color) {
    // Iterate through piece list and locate the king of the specified color
    for (Piece piece : pieces) {
      if (piece instanceof King && piece.getColor() == color) {
        return (King) piece;
      }
    }

    // Throw illegal argument if king isn't on board, should not be possible
    throw new IllegalArgumentException();
  }

  private void checkEnPassantSquare(Piece piece) {
    // Check if there is a left square to the current square
    if (piece.getSquare().getCol() != 0) {
      // Get the left square
      Square leftSquare =
          board.getSquare(piece.getSquare().getRow(), piece.getSquare().getCol() - 1);
      if (leftSquare.isOccupied()) {
        // Check if the left square contains an opponent pawn
        if (leftSquare.getPiece() instanceof Pawn pawn
            && leftSquare.getPiece().getColor() != piece.getColor()) {
          // Set the en passant target square based on the current turn
          Square enPassantTarget;
          if (currentTurn) {
            enPassantTarget = board.getSquare(piece.getSquare().getRow() + 1, piece.getSquare().getCol());
          } else {
            enPassantTarget = board.getSquare(piece.getSquare().getRow() - 1, piece.getSquare().getCol());
          }
          pawn.setEnPassantTarget(enPassantTarget);
        }
      }
    }

    // Check if there is a right square to the current square
    if (piece.getSquare().getCol() != 7) {
      // Get the right square
      Square rightSquare =
          board.getSquare(piece.getSquare().getRow(), piece.getSquare().getCol() + 1);
      if (rightSquare.isOccupied()) {
        // Check if the right square contains an opponent pawn
        if (rightSquare.getPiece() instanceof Pawn pawn
            && rightSquare.getPiece().getColor() != piece.getColor()) {
          // Set the en passant target square based on the current turn
          Square enPassantTarget;
          if (currentTurn) {
            enPassantTarget = board.getSquare(piece.getSquare().getRow() + 1, piece.getSquare().getCol());
          } else {
            enPassantTarget = board.getSquare(piece.getSquare().getRow() - 1, piece.getSquare().getCol());
          }
          pawn.setEnPassantTarget(enPassantTarget);
        }
      }
    }
  }

  private void promotePawn(Square square) {
    // Check if there is a promoted piece type specified
    if (promotedPieceType != null) {
      // Remove pawn from piece list
      pieces.remove(square.getPiece());

      // Based on the promoted piece type, create a new piece of that type
      // and set its square to the given square
      if (promotedPieceType == PieceType.QUEEN) {
        Queen queen = new Queen(currentTurn);
        queen.setSquare(square);
        board.getSquare(square.getRow(), square.getCol()).setPiece(queen);
        pieces.add(queen);
      } else if (promotedPieceType == PieceType.ROOK) {
        Rook rook = new Rook(currentTurn);
        rook.setSquare(square);
        pieces.add(rook);
        board.getSquare(square.getRow(), square.getCol()).setPiece(rook);
      } else if (promotedPieceType == PieceType.BISHOP) {
        Bishop bishop = new Bishop(currentTurn);
        bishop.setSquare(square);
        board.getSquare(square.getRow(), square.getCol()).setPiece(bishop);
        pieces.add(bishop);
      } else if (promotedPieceType == PieceType.KNIGHT) {
        Knight knight = new Knight(currentTurn);
        knight.setSquare(square);
        board.getSquare(square.getRow(), square.getCol()).setPiece(knight);
        pieces.add(knight);
      }
    }
    // Reset the promotedPieceType to null to indicate no pending promotion
    this.promotedPieceType = null;
  }

  public String generateBoardState() {
    // Create a StringBuilder to build the board state string
    StringBuilder str = new StringBuilder();

    // Iterate through each square on the board
    for (int row = 0; row < 8; row++) {
      for (int col = 0; col < 8; col++) {
        Square square = board.getSquare(row, col);

        // Check if the square is not occupied by a piece
        if (!square.isOccupied()) {
          // Append 'x' to represent an empty square
          str.append("x");
        } else {
          Piece piece = square.getPiece();

          // Determine the type and color of the piece and append the corresponding symbol
          switch (piece.getType()) {
            case PAWN -> str.append(piece.getColor() ? "P" : "p");
            case KNIGHT -> str.append(piece.getColor() ? "N" : "n");
            case BISHOP -> str.append(piece.getColor() ? "B" : "b");
            case ROOK -> str.append(piece.getColor() ? "R" : "r");
            case QUEEN -> str.append(piece.getColor() ? "Q" : "q");
            case KING -> str.append(piece.getColor() ? "K" : "k");
          }
        }
      }
    }

    // Return the generated board state string
    return str.toString();
  }

  public void startingBoard() {
    // Initialize the starting board of chess
    Rook white_rook_1 = new Rook(true);
    Rook white_rook_2 = new Rook(true);
    Knight white_knight_1 = new Knight(true);
    Knight white_knight_2 = new Knight(true);
    Bishop white_bishop_1 = new Bishop(true);
    Bishop white_bishop_2 = new Bishop(true);
    Queen white_queen_1 = new Queen(true);
    King white_king_1 = new King(true);
    Pawn white_pawn_1 = new Pawn(true);
    Pawn white_pawn_2 = new Pawn(true);
    Pawn white_pawn_3 = new Pawn(true);
    Pawn white_pawn_4 = new Pawn(true);
    Pawn white_pawn_5 = new Pawn(true);
    Pawn white_pawn_6 = new Pawn(true);
    Pawn white_pawn_7 = new Pawn(true);
    Pawn white_pawn_8 = new Pawn(true);

    Rook black_rook_1 = new Rook(false);
    Rook black_rook_2 = new Rook(false);
    Knight black_knight_1 = new Knight(false);
    Knight black_knight_2 = new Knight(false);
    Bishop black_bishop_1 = new Bishop(false);
    Bishop black_bishop_2 = new Bishop(false);
    Queen black_queen_1 = new Queen(false);
    King black_king_1 = new King(false);
    Pawn black_pawn_1 = new Pawn(false);
    Pawn black_pawn_2 = new Pawn(false);
    Pawn black_pawn_3 = new Pawn(false);
    Pawn black_pawn_4 = new Pawn(false);
    Pawn black_pawn_5 = new Pawn(false);
    Pawn black_pawn_6 = new Pawn(false);
    Pawn black_pawn_7 = new Pawn(false);
    Pawn black_pawn_8 = new Pawn(false);

    board.getSquare(7, 0).setPiece(white_rook_1);
    white_rook_1.setSquare(board.getSquare(7, 0));
    board.getSquare(7, 1).setPiece(white_knight_1);
    white_knight_1.setSquare(board.getSquare(7, 1));
    board.getSquare(7, 2).setPiece(white_bishop_1);
    white_bishop_1.setSquare(board.getSquare(7, 2));
    board.getSquare(7, 3).setPiece(white_queen_1);
    white_queen_1.setSquare(board.getSquare(7, 3));
    board.getSquare(7, 4).setPiece(white_king_1);
    white_king_1.setSquare(board.getSquare(7, 4));
    board.getSquare(7, 5).setPiece(white_bishop_2);
    white_bishop_2.setSquare(board.getSquare(7, 5));
    board.getSquare(7, 6).setPiece(white_knight_2);
    white_knight_2.setSquare(board.getSquare(7, 6));
    board.getSquare(7, 7).setPiece(white_rook_2);
    white_rook_2.setSquare(board.getSquare(7, 7));

    board.getSquare(6, 0).setPiece(white_pawn_1);
    white_pawn_1.setSquare(board.getSquare(6, 0));
    board.getSquare(6, 1).setPiece(white_pawn_2);
    white_pawn_2.setSquare(board.getSquare(6, 1));
    board.getSquare(6, 2).setPiece(white_pawn_3);
    white_pawn_3.setSquare(board.getSquare(6, 2));
    board.getSquare(6, 3).setPiece(white_pawn_4);
    white_pawn_4.setSquare(board.getSquare(6, 3));
    board.getSquare(6, 4).setPiece(white_pawn_5);
    white_pawn_5.setSquare(board.getSquare(6, 4));
    board.getSquare(6, 5).setPiece(white_pawn_6);
    white_pawn_6.setSquare(board.getSquare(6, 5));
    board.getSquare(6, 6).setPiece(white_pawn_7);
    white_pawn_7.setSquare(board.getSquare(6, 6));
    board.getSquare(6, 7).setPiece(white_pawn_8);
    white_pawn_8.setSquare(board.getSquare(6, 7));

    board.getSquare(0, 0).setPiece(black_rook_1);
    black_rook_1.setSquare(board.getSquare(0, 0));
    board.getSquare(0, 1).setPiece(black_knight_1);
    black_knight_1.setSquare(board.getSquare(0, 1));
    board.getSquare(0, 2).setPiece(black_bishop_1);
    black_bishop_1.setSquare(board.getSquare(0, 2));
    board.getSquare(0, 3).setPiece(black_queen_1);
    black_queen_1.setSquare(board.getSquare(0, 3));
    board.getSquare(0, 4).setPiece(black_king_1);
    black_king_1.setSquare(board.getSquare(0, 4));
    board.getSquare(0, 5).setPiece(black_bishop_2);
    black_bishop_2.setSquare(board.getSquare(0, 5));
    board.getSquare(0, 6).setPiece(black_knight_2);
    black_knight_2.setSquare(board.getSquare(0, 6));
    board.getSquare(0, 7).setPiece(black_rook_2);
    black_rook_2.setSquare(board.getSquare(0, 7));

    board.getSquare(1, 0).setPiece(black_pawn_1);
    black_pawn_1.setSquare(board.getSquare(1, 0));
    board.getSquare(1, 1).setPiece(black_pawn_2);
    black_pawn_2.setSquare(board.getSquare(1, 1));
    board.getSquare(1, 2).setPiece(black_pawn_3);
    black_pawn_3.setSquare(board.getSquare(1, 2));
    board.getSquare(1, 3).setPiece(black_pawn_4);
    black_pawn_4.setSquare(board.getSquare(1, 3));
    board.getSquare(1, 4).setPiece(black_pawn_5);
    black_pawn_5.setSquare(board.getSquare(1, 4));
    board.getSquare(1, 5).setPiece(black_pawn_6);
    black_pawn_6.setSquare(board.getSquare(1, 5));
    board.getSquare(1, 6).setPiece(black_pawn_7);
    black_pawn_7.setSquare(board.getSquare(1, 6));
    board.getSquare(1, 7).setPiece(black_pawn_8);
    black_pawn_8.setSquare(board.getSquare(1, 7));

    pieces.add(white_rook_1);
    pieces.add(white_rook_2);
    pieces.add(white_knight_1);
    pieces.add(white_knight_2);
    pieces.add(white_bishop_1);
    pieces.add(white_bishop_2);
    pieces.add(white_queen_1);
    pieces.add(white_king_1);
    pieces.add(white_pawn_1);
    pieces.add(white_pawn_2);
    pieces.add(white_pawn_3);
    pieces.add(white_pawn_4);
    pieces.add(white_pawn_5);
    pieces.add(white_pawn_6);
    pieces.add(white_pawn_7);
    pieces.add(white_pawn_8);
    pieces.add(black_rook_1);
    pieces.add(black_rook_2);
    pieces.add(black_knight_1);
    pieces.add(black_knight_2);
    pieces.add(black_bishop_1);
    pieces.add(black_bishop_2);
    pieces.add(black_queen_1);
    pieces.add(black_king_1);
    pieces.add(black_pawn_1);
    pieces.add(black_pawn_2);
    pieces.add(black_pawn_3);
    pieces.add(black_pawn_4);
    pieces.add(black_pawn_5);
    pieces.add(black_pawn_6);
    pieces.add(black_pawn_7);
    pieces.add(black_pawn_8);

    generatePossibleMoves();
    generateLegalMoves();
    getOrderedLegalMoves();
  }

  public void addObserver(ModelObserver observer) {
    // Add model observers
    observers.add(observer);
  }

  private void notifyObservers() {
    // Notify all observers of the current model
    for (ModelObserver observer : observers) {
      observer.updateMove(this);
    }
  }

  private void notifyPromotion(Square promotionSquare) {
    // Notify all observers of a new promoted pawn
    for (ModelObserver observer : observers) {
      observer.updatePromotion(promotionSquare);
    }
  }
}
