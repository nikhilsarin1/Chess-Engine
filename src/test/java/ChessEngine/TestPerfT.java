package ChessEngine;

import ChessEngine.AI.AI;
import ChessEngine.model.Model;
import org.junit.Test;

public class TestPerfT {
  // Position 1 debugs possible castling errors
  // Position 2 debugs possible enPassant errors
  // Position 3 debugs possible promotion errors

  @Test
  public void testDepthOne() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(1);
    System.out.println("Starting Position Depth 1 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 20);
  }

  @Test
  public void testDepthTwo() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(2);
    System.out.println("Starting Position Depth 2 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 400);
  }

  @Test
  public void testDepthThree() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(3);
    System.out.println("Starting Position Depth 3 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 8902);
  }

  @Test
  public void testDepthFour() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(4);
    System.out.println("Starting Position Depth 4 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 197281);
  }

  @Test
  public void testDepthFive() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(5);
    System.out.println("Starting Position Depth 5 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 4865609);
  }

  @Test
  public void testPositionOneDepthOne() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    AI bot = new AI(model);
    bot.fullSearch(1);
    System.out.println("Position 1 Depth 1 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 48);
  }

  @Test
  public void testPositionOneDepthTwo() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    AI bot = new AI(model);
    bot.fullSearch(2);
    System.out.println("Position 1 Depth 2 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 2039);
  }

  @Test
  public void testPositionOneDepthThree() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    AI bot = new AI(model);
    bot.fullSearch(3);
    System.out.println("Position 1 Depth 3 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 97862);
  }

  @Test
  public void testPositionTwoDepthOne() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    AI bot = new AI(model);
    bot.fullSearch(1);
    System.out.println("Position 2 Depth 1 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 14);
  }

  @Test
  public void testPositionTwoDepthTwo() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    AI bot = new AI(model);
    bot.fullSearch(2);
    System.out.println("Position 2 Depth 2 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 191);
  }

  @Test
  public void testPositionTwoDepthThree() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    AI bot = new AI(model);
    bot.fullSearch(3);
    System.out.println("Position 2 Depth 3 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 2812);
  }

  @Test
  public void testPositionTwoDepthFour() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    AI bot = new AI(model);
    bot.fullSearch(4);
    System.out.println("Position 2 Depth 4 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 43238);
  }

  @Test
  public void testPositionTwoDepthFive() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    AI bot = new AI(model);
    bot.fullSearch(5);
    System.out.println("Position 2 Depth 5 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 674624);
  }

  @Test
  public void testPositionThreeDepthOne() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(1);
    System.out.println("Position 3 Depth 1 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 6);
  }

  @Test
  public void testPositionThreeDepthTwo() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(2);
    System.out.println("Position 3 Depth 2 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 264);
  }

  @Test
  public void testPositionThreeDepthThree() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(3);
    System.out.println("Position 3 Depth 3 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 9467);
  }

  @Test
  public void testPositionThreeDepthFour() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    AI bot = new AI(model);
    bot.fullSearch(4);
    System.out.println("Position 3 Depth 4 Search Count: " + bot.searchCount);
    assert (bot.searchCount == 422333);
  }
}
