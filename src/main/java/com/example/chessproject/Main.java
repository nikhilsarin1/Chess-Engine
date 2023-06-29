package com.example.chessproject;

import com.example.chessproject.view.ChessGUI;
import javafx.application.Application;

public class Main {

  public static void main(String[] args) {
    Application.launch(ChessGUI.class);
  }
}

// Optimization goals

// Use 12 bitboards to track positions of all the pieces on the board rather than an arraylist of
// arraylists

// Use bitwise operations to generate all pseudo legal moves a piece can move to rather than looping
// through all 64 squares on the chessboard and checking if it's a pseudo-legal move for a specific
// piece

// Create attack maps for enemy pieces at any given turn to determine if king is in check rather
// than looping through every pseudo-legal move and checking if it leaves the king in check

// Utilize chess PerfT testing to ensure the correct legal moves are generated for any given
// position

// Improve move ordering function to go beyond just sorting by any capture move

// Improve evaluation function beyond simple material count and piece tables

// Use transposition tables to improve search and move ordering
