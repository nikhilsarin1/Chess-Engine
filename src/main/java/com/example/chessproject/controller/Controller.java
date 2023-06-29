package com.example.chessproject.controller;

import com.example.chessproject.model.Square;

public interface Controller {
  /**
   * Handles the logic when a player clicks on the origin square.
   *
   * @param square The selected origin square.
   */
  void clickOrigin(Square square);

  /**
   * Handles the logic when a player clicks on the destination square.
   *
   * @param square The selected destination square.
   */
  void clickDestination(Square square);
}
