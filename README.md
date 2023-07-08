# Chess-Engine

This project is an implementation of a fully functional chess engine with a playable AI. It provides the ability to play chess against the computer.

## Features

- Play chess against the computer with a playable AI.
- Support for standard chess rules including castling, en passant, and pawn promotion.
- Interactive command-line interface for playing and controlling the game.

## Project Structure

The original attempt at creating the chess engine resides in the `com.example.chessproject` package. However, this initial implementation had limitations and could only check around 2000 nodes per second.

The current and improved implementation is located in the `ChessEngine` package. It addresses the performance issues of the original version and can now check around 50000 nodes per second.
