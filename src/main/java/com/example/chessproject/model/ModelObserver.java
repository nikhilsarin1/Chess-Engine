package com.example.chessproject.model;

/**
 * The ModelObserver interface defines the contract for objects that observe the Chess game model.
 * Implementing classes must provide implementations for the updateMove and updatePromotion methods.
 */
public interface ModelObserver {

  /**
   * Notifies the observer that a move has been made in the Chess game model.
   *
   * @param model the Chess game model that initiated the move
   */
  void updateMove(ModelImpl model);

  /**
   * Notifies the observer that a promotion has occurred in the Chess game model.
   *
   * @param promotionSquare the Square on the Chessboard where the promotion occurred
   */
  void updatePromotion(Square promotionSquare);
}
