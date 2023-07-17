# Chess-Engine

This project is an implementation of a fully functional chess engine with a playable AI. It provides the ability to play chess against the computer.

## Features

- Play chess against the computer with a playable AI.
- Support for standard chess rules including castling, en passant, and pawn promotion.
- Interactive graphical-user interface for playing and controlling the game.

## Project Structure

The original attempt at creating the chess engine resides in the `com.example.chessproject` package. However, this initial implementation had limitations and could only check around 2000 nodes per second.

In the ChessEngine package, the current and improved implementation has made significant strides in addressing performance issues, achieving an impressive rate of approximately 300,000 nodes per second during its search. Thanks to these enhancements, the engine consistently outperforms bots rated 1900 on chess.com, showcasing its superior strategic and tactical capabilities.

Please note that the actual number of nodes searched per second may vary depending on the hardware and specific optimizations applied. Nevertheless, the improvements made to the engine have resulted in a substantial boost in its playing strength, allowing it to confidently compete against formidable opponents and consistently come out on top.
