# Chess-Engine

This project is an implementation of a fully functional chess engine with a playable AI. It provides the ability to play chess against the computer.

## Features

- Play chess against the computer with a playable AI.
- Support for standard chess rules including castling, en passant, and pawn promotion.
- Interactive graphical user interface for playing and controlling the game.

## Project Structure

The original attempt at creating the chess engine resides in the `com.example.chessproject` package. However, this initial implementation had limitations and could only check around 2,000 nodes per second.

In the ChessEngine package, the current and improved implementation incorporates several essential techniques to enhance search performance and playing strength:

Alpha-Beta Pruning: By effectively pruning irrelevant branches of the search tree, this optimization dramatically reduces the number of nodes explored, leading to faster and more efficient search results.

Transposition Table: Through the intelligent use of a transposition table, the engine stores previously evaluated positions, eliminating redundant evaluations and significantly speeding up the search process.

Move Ordering: The engine utilizes move ordering heuristics, prioritizing moves with higher capture values, to explore more promising variations first. This approach can lead to early alpha-beta cutoffs, further improving search efficiency.

Quiescence Search: By incorporating a quiescence search, the engine ensures that it explores positions with captures and checks more deeply. This prevents the horizon effect and produces more accurate evaluations.

With the integration of these techniques, the engine has achieved an impressive search rate of approximately 300,000 nodes per second, making it a strong competitor against bots rated 1900 on chess.com. The combination of these optimizations has significantly bolstered the engine's strategic and tactical capabilities, allowing it to confidently outperform its previous limitations and achieve remarkable results in various game scenarios.
