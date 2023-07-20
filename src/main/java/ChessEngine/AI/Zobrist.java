package ChessEngine.AI;

import ChessEngine.model.Bitboard;

import java.security.SecureRandom;

@SuppressWarnings("InstantiationOfUtilityClass")
public class Zobrist {
  public static Bitboard bitboard;
  public static long[][] board;
  public static long[] enPassant;
  public static long[] castle;
  public static long turn;
  private static Zobrist instance;

  private Zobrist(Bitboard bitboard) {
    Zobrist.bitboard = bitboard;
    board = new long[12][64];
    enPassant = new long[8];
    castle = new long[4];
    fillZobrist();
  }

  public static void getInstance(Bitboard bitboard) {
    if (instance == null) {
      instance = new Zobrist(bitboard);
    }
  }

  public static long randomNumberGenerator() {
    SecureRandom random = new SecureRandom();
    return random.nextLong();
  }

  public static void fillZobrist() {
    for (int piece = 0; piece < 12; piece++) {
      for (int square = 0; square < 64; square++) {
        board[piece][square] = randomNumberGenerator();
      }
    }

    for (int col = 0; col < 8; col++) {
      enPassant[col] = randomNumberGenerator();
    }

    for (int i = 0; i < 4; i++) {
      castle[i] = randomNumberGenerator();
    }

    turn = randomNumberGenerator();
  }

  public static long getZobristKey() {
    long key = 0;
    for (int i = 0; i < 64; i++) {
      if (bitboard.charBoard[i] == 'K') {
        key ^= board[0][i];
      } else if (bitboard.charBoard[i] == 'Q') {
        key ^= board[1][i];
      } else if (bitboard.charBoard[i] == 'R') {
        key ^= board[2][i];
      } else if (bitboard.charBoard[i] == 'B') {
        key ^= board[3][i];
      } else if (bitboard.charBoard[i] == 'N') {
        key ^= board[4][i];
      } else if (bitboard.charBoard[i] == 'P') {
        key ^= board[5][i];
      } else if (bitboard.charBoard[i] == 'k') {
        key ^= board[6][i];
      } else if (bitboard.charBoard[i] == 'q') {
        key ^= board[7][i];
      } else if (bitboard.charBoard[i] == 'r') {
        key ^= board[8][i];
      } else if (bitboard.charBoard[i] == 'b') {
        key ^= board[9][i];
      } else if (bitboard.charBoard[i] == 'n') {
        key ^= board[10][i];
      } else if (bitboard.charBoard[i] == 'p') {
        key ^= board[11][i];
      }
    }

    if (bitboard.getEnPassantSquare() != 0) {
      int position = bitboard.convertBitboardToInt(bitboard.getEnPassantSquare());
      key ^= enPassant[position % 8];
    }

    if (bitboard.whiteKingSide) {
      key ^= castle[0];
    }
    if (bitboard.whiteQueenSide) {
      key ^= castle[1];
    }
    if (bitboard.blackKingSide) {
      key ^= castle[2];
    }
    if (bitboard.blackQueenSide) {
      key ^= castle[3];
    }

    if (bitboard.currentTurn) {
      key ^= turn;
    }

    return key;
  }

  public static long getPawnHashKey() {
    long key = 0;

    for (int i = 0; i < 64; i++) {
      if (bitboard.charBoard[i] == 'P') {
        key ^= board[5][i];
      } else if (bitboard.charBoard[i] == 'p') {
        key ^= board[11][i];
      }
    }
    return key;
  }
}
