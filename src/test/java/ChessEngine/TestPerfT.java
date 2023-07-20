package ChessEngine;

import ChessEngine.AI.Search;
import ChessEngine.model.Model;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestPerfT {
  // Position 1 debugs starting board
  // Position 2 debugs possible castling errors
  // Position 3 debugs possible enPassant errors
  // Position 4 debugs possible promotion errors
  // Position 5 debugs simultaneous edge case errors
  // Position 6 debugs simultaneous edge case errors
  // Position 7 debugs king attacking king errors

  @Test
  public void testP1D1() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 1 Depth 1 Search Count: " + bot.searchCount + ", expected 20.");
    assert (bot.searchCount == 20);
  }

  @Test
  public void testP1D2() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 1 Depth 2 Search Count: " + bot.searchCount + ", expected 400.");
    assert (bot.searchCount == 400);
  }

  @Test
  public void testP1D3() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 1 Depth 3 Search Count: " + bot.searchCount + ", expected 8902.");
    assert (bot.searchCount == 8902);
  }

  @Test
  public void testP1D4() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println(
        "Position 1 Depth 4 Search Count: " + bot.searchCount + ", expected 197281.");
    assert (bot.searchCount == 197281);
  }

  @Test
  public void testP1D5() {
    Model model = new Model("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(5);
    System.out.println(
        "Position 1 Depth 5 Search Count: " + bot.searchCount + ", expected 4865609.");
    assert (bot.searchCount == 4865609);
  }

  @Test
  public void testP2D1() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 2 Depth 1 Search Count: " + bot.searchCount + ", expected 48.");
    assert (bot.searchCount == 48);
  }

  @Test
  public void testP2D2() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 2 Depth 2 Search Count: " + bot.searchCount + ", expected 2039.");
    assert (bot.searchCount == 2039);
  }

  @Test
  public void testP2D3() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 2 Depth 3 Search Count: " + bot.searchCount + ", expected 97862.");
    assert (bot.searchCount == 97862);
  }

  @Test
  public void testP2D4() {
    Model model = new Model("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - ");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println(
        "Position 2 Depth 4 Search Count: " + bot.searchCount + ", expected 4085603.");
    assert (bot.searchCount == 4085603);
  }

  @Test
  public void testP3D1() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 3 Depth 1 Search Count: " + bot.searchCount + ", expected 14.");
    assert (bot.searchCount == 14);
  }

  @Test
  public void testP3D2() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 3 Depth 2 Search Count: " + bot.searchCount + ", expected 191.");
    assert (bot.searchCount == 191);
  }

  @Test
  public void testP3D3() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 3 Depth 3 Search Count: " + bot.searchCount + ", expected 2812.");
    assert (bot.searchCount == 2812);
  }

  @Test
  public void testP3D4() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println("Position 3 Depth 4 Search Count: " + bot.searchCount + ", expected 43238.");
    assert (bot.searchCount == 43238);
  }

  @Test
  public void testP3D5() {
    Model model = new Model("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - ");
    Search bot = new Search(model);
    bot.fullSearch(5);
    System.out.println(
        "Position 3 Depth 5 Search Count: " + bot.searchCount + ", expected 674624.");
    assert (bot.searchCount == 674624);
  }

  @Test
  public void testP4D1() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 4 Depth 1 Search Count: " + bot.searchCount + ", expected 6.");
    assert (bot.searchCount == 6);
  }

  @Test
  public void testP4D2() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 4 Depth 2 Search Count: " + bot.searchCount + ", expected 264.");
    assert (bot.searchCount == 264);
  }

  @Test
  public void testP4D3() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 4 Depth 3 Search Count: " + bot.searchCount + ", expected 9467.");
    assert (bot.searchCount == 9467);
  }

  @Test
  public void testP4D4() {
    Model model = new Model("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println(
        "Position 4 Depth 4 Search Count: " + bot.searchCount + ", expected 422333.");
    assert (bot.searchCount == 422333);
  }

  @Test
  public void testP5D1() {
    Model model = new Model("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 5 Depth 1 Search Count: " + bot.searchCount + ", expected 44.");
    assert (bot.searchCount == 44);
  }

  @Test
  public void testP5D2() {
    Model model = new Model("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 5 Depth 2 Search Count: " + bot.searchCount + ", expected 1486.");
    assert (bot.searchCount == 1486);
  }

  @Test
  public void testP5D3() {
    Model model = new Model("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 5 Depth 3 Search Count: " + bot.searchCount + ", expected 62379.");
    assert (bot.searchCount == 62379);
  }

  @Test
  public void testP5D4() {
    Model model = new Model("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println(
        "Position 5 Depth 4 Search Count: " + bot.searchCount + ", expected 2103487.");
    assert (bot.searchCount == 2103487);
  }

  @Test
  public void testP6D1() {
    Model model =
        new Model("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 6 Depth 1 Search Count: " + bot.searchCount + ", expected 46.");
    assert (bot.searchCount == 46);
  }

  @Test
  public void testP6D2() {
    Model model =
        new Model("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 6 Depth 2 Search Count: " + bot.searchCount + ", expected 2079.");
    assert (bot.searchCount == 2079);
  }

  @Test
  public void testP6D3() {
    Model model =
        new Model("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 6 Depth 3 Search Count: " + bot.searchCount + ", expected 89890.");
    assert (bot.searchCount == 89890);
  }

  @Test
  public void testP6D4() {
    Model model =
        new Model("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10 ");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println(
        "Position 6 Depth 4 Search Count: " + bot.searchCount + ", expected 3894594.");
    assert (bot.searchCount == 3894594);
  }

  @Test
  public void testP7D1() {
    Model model = new Model("8/7p/3k4/8/3K4/8/P7/8 w - - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(1);
    System.out.println("Position 7 Depth 1 Search Count: " + bot.searchCount + ", expected 7.");
    assert (bot.searchCount == 7);
  }

  @Test
  public void testP7D2() {
    Model model = new Model("8/7p/3k4/8/3K4/8/P7/8 w - - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(2);
    System.out.println("Position 7 Depth 2 Search Count: " + bot.searchCount + ", expected 60.");
    assert (bot.searchCount == 60);
  }

  @Test
  public void testP7D3() {
    Model model = new Model("8/7p/3k4/8/3K4/8/P7/8 w - - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(3);
    System.out.println("Position 7 Depth 3 Search Count: " + bot.searchCount + ", expected 527.");
    assert (bot.searchCount == 527);
  }

  @Test
  public void testP7D4() {
    Model model = new Model("8/7p/3k4/8/3K4/8/P7/8 w - - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(4);
    System.out.println("Position 7 Depth 4 Search Count: " + bot.searchCount + ", expected 4761.");
    assert (bot.searchCount == 4761);
  }

  @Test
  public void testP7D5() {
    Model model = new Model("8/7p/3k4/8/3K4/8/P7/8 w - - 0 1");
    Search bot = new Search(model);
    bot.fullSearch(5);
    System.out.println("Position 7 Depth 5 Search Count: " + bot.searchCount + ", expected 42789.");
    assert (bot.searchCount == 42789);
  }
}
