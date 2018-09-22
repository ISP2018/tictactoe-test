package tictactoe;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test for known issues in the TicTacToe class.
 * The tests use a JUnit parameter to vary the board size.
 *
 * Requires: JUnit 4, preferrably 4.12 or newer.
 * The JUnit included with Eclipse works.
 * 
 * @author jim
 */
@RunWith(org.junit.runners.Parameterized.class)
public class TicTacToeTest {
	
	private Player player;
	private TicTacToeGame game;
	/** boardsize is set via constructor using JUnit Parameterized class. */
	int boardsize = 3; // default value

	/** 
	 * The parameter values used in the tests.
	 * The only parameter is the game board size.
	 */
	@Parameters(name="TicTacToe(boardsize={0})")
	public static Collection<Object[]> boardsizes() {
		// JUnit requires parameters be an Iterable<Object[]>, 
		// so use Object[][] array.
		Object[][] values = {{3}, {4}};
		return java.util.Arrays.asList(values);
	}
	
	/**
	 * Create test suite with a given game board size.
	 * @param boardsize size of the TicTacToe board, of course.
	 */
	public TicTacToeTest(int boardsize) {
		this.boardsize = boardsize;
	}
	
	/** Create a new game before each test. */
	@Before
	public void setUp() throws Exception {
		game = new TicTacToeGame(boardsize);
	}

	/** Test whether a player can make a move after game is won. */
	@Test
	public void testCanMoveAfterXwins() {
		assertFalse( game.isGameOver() );
		for(int col=0; col<boardsize-1; col++) {
			makeMove(Player.X, col, 0);
			makeMove(Player.O, col, 1); // row below 'X'
		}
		// now X wins
		int col = boardsize-1;
		makeMove(Player.X, col, 0);
		assertEquals( Player.X, game.winner() );
		assertTrue( game.isGameOver() );
		assertFalse( game.canMoveTo(Player.O, col, 1) );
		// X should not be able to move either
		assertFalse( game.canMoveTo(Player.X, col, 1) );
	}

	/** Test whether a player can make a move after game is won.
	 *  This time, player.O wins and use a different direction.
	 */
	@Test
	public void testCanMoveAfterOwins() {
		for(int col=0; col<boardsize-1; col++) {
			makeMove(Player.O, col, 0);
			makeMove(Player.X, col, 1); // row below 'O'
		}
		// now O wins
		int col = boardsize-1;
		makeMove(Player.O, col, 0);
		assertEquals( Player.O, game.winner() );
		assertTrue( game.isGameOver() );
		assertFalse( game.canMoveTo(Player.X, col, 1) );
		// O should not be able to move either
		assertFalse( game.canMoveTo(Player.O, col, 1) );
	}

	/** Test if game correctly records a "win" on downward (main) diagonal. */
	@Test
	public void testWinOnDownwardDiagonal() {
		assertFalse( game.isGameOver() );
		for(int col=0; col<boardsize-1; col++) {
			int row = col;
			makeMove(Player.X, col, row);
			assertFalse( game.isGameOver() );
			// "O" stupidly moves in adjacent diagonal
			makeMove(Player.O, col, (row+1)%boardsize);
			assertFalse( game.isGameOver() );
		}
		// now X wins
		int col = boardsize-1;
		makeMove(Player.X, col, col);
		assertTrue( "X won on main diagonal, boardsize="+boardsize, game.isGameOver() );
		assertEquals( "X won on main diagonal, boardsize="+boardsize, Player.X, game.winner() );
	}

	/** Test if game correctly records a "win" on upward diagonal. */
	@Test
	public void testWinOnUpwardDiagonal() {
		game = new TicTacToeGame(boardsize);
		assertFalse( game.isGameOver() );
		for(int col=0; col<boardsize-1; col++) {
			int row = boardsize - col - 1;
			makeMove(Player.X, col, row);
			assertFalse( game.isGameOver() );
			// "O" moves on off-diagonal
			makeMove(Player.O, col, (row+1)%boardsize);
			assertFalse( game.isGameOver() );
		}
		// now X wins
		int col = boardsize-1;
		int row = 0;
		makeMove(Player.X, col, row);
		assertTrue( "X won on upward diagonal, boardsize="+boardsize, game.isGameOver() );
		assertEquals( "X won on upward diagonal, boardsize="+boardsize, Player.X, game.winner() );
	}

	/** Test if game correctly records a "win" in horizontal direction, using all rows. */
	@Test
	public void testWinOnHorizonal() {
		
		for(int row=0; row<boardsize; row++) {
			// other player moves on a different row, to avoid occupying square we want
			int opponentRow = (row+1) % boardsize;
			game = new TicTacToeGame(boardsize);
			assertFalse( game.isGameOver() );
			for(int col=0; col<boardsize-1; col++) {
				makeMove(Player.X, col, row);
				assertFalse( game.isGameOver() );
				makeMove(Player.O, col, opponentRow);
				assertFalse( game.isGameOver() );
			}
			// now X wins
			int col = boardsize-1;
			makeMove(Player.X, col, row);
			assertTrue( "X won on row "+row+", boardsize="+boardsize, game.isGameOver() );
		}
	}

	/** Test if game correctly records a "win" in vertical direction, using all rows. */
	@Test
	public void testWinOnVertical() {
		
		for(int col=0; col<boardsize; col++) {
			// other player moves on a different column, to avoid occupying square we want
			int opponentCol = (col>0)? col-1 : boardsize-1;
			game = new TicTacToeGame(boardsize);
			assertFalse( game.isGameOver() );
			for(int row=0; row<boardsize-1; row++) {
				makeMove(Player.O, col, row);
				assertFalse( game.isGameOver() );
				makeMove(Player.X, opponentCol, row);
				assertFalse( game.isGameOver() );
			}
			// now O wins
			int row = boardsize-1;
			makeMove(Player.O, col, row);
			assertTrue( "O won on column "+col+", boardsize="+boardsize, game.isGameOver() );
		}
	}

	/** Helper method to make moves on game board, with Player given. */
	public void makeMove(Player p, int col, int row) {
		Piece piece = new Piece(p, 10);
		game.moveTo(piece, col, row);
	}

	/** Helper method with player chosen automatically. */
	public void makeMove(int col, int row) {
		player = game.getNextPlayer();
		Piece piece = new Piece(player, 10);
		game.moveTo(piece, col, row);
	}
}
