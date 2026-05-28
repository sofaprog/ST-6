package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ProgramTest {

    @Test
    void gameStartsWithEmptyBoardAndXPlayer() {
        Game game = new Game();

        assertEquals(State.PLAYING, game.state);
        assertEquals('X', game.player1.symbol);
        assertEquals('O', game.player2.symbol);
        assertEquals(9, game.board.length);
        assertArrayEquals(new char[] {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, game.board);
    }

    @Test
    void checkStateReturnsXWinForRowsColumnsAndDiagonals() {
        Game game = new Game();
        game.symbol = 'X';

        char[][] winningBoards = {
            {'X', 'X', 'X', ' ', ' ', ' ', ' ', ' ', ' '},
            {' ', ' ', ' ', 'X', 'X', 'X', ' ', ' ', ' '},
            {' ', ' ', ' ', ' ', ' ', ' ', 'X', 'X', 'X'},
            {'X', ' ', ' ', 'X', ' ', ' ', 'X', ' ', ' '},
            {' ', 'X', ' ', ' ', 'X', ' ', ' ', 'X', ' '},
            {' ', ' ', 'X', ' ', ' ', 'X', ' ', ' ', 'X'},
            {'X', ' ', ' ', ' ', 'X', ' ', ' ', ' ', 'X'},
            {' ', ' ', 'X', ' ', 'X', ' ', 'X', ' ', ' '}
        };

        for (char[] board : winningBoards) {
            assertEquals(State.XWIN, game.checkState(board));
        }
    }

    @Test
    void checkStateReturnsOWin() {
        Game game = new Game();
        game.symbol = 'O';
        char[] board = {'O', ' ', 'X', 'X', 'O', ' ', ' ', ' ', 'O'};

        assertEquals(State.OWIN, game.checkState(board));
    }

    @Test
    void checkStateDistinguishesPlayingAndDraw() {
        Game game = new Game();
        game.symbol = 'X';

        assertEquals(State.PLAYING, game.checkState(new char[] {
            'X', 'O', 'X',
            'O', 'X', 'O',
            'O', 'X', ' '
        }));
        assertEquals(State.DRAW, game.checkState(new char[] {
            'X', 'O', 'X',
            'X', 'O', 'O',
            'O', 'X', 'X'
        }));
    }

    @Test
    void generateMovesAddsOnlyEmptyCellIndexes() {
        Game game = new Game();
        ArrayList<Integer> moves = new ArrayList<>();

        game.generateMoves(new char[] {
            'X', ' ', 'O',
            ' ', 'X', ' ',
            'O', ' ', 'X'
        }, moves);

        assertIterableEquals(Arrays.asList(1, 3, 5, 7), moves);
    }

    @Test
    void evaluatePositionScoresWinLossDrawAndOngoingGame() {
        Game game = new Game();
        char[] xWin = {'X', 'X', 'X', 'O', 'O', ' ', ' ', ' ', ' '};
        char[] draw = {'X', 'O', 'X', 'X', 'O', 'O', 'O', 'X', 'X'};
        char[] ongoing = {'X', 'O', 'X', ' ', 'O', ' ', ' ', ' ', ' '};

        game.symbol = 'X';
        assertEquals(Game.INF, game.evaluatePosition(xWin, game.player1));
        assertEquals(-Game.INF, game.evaluatePosition(xWin, game.player2));
        assertEquals(0, game.evaluatePosition(draw, game.player1));
        assertEquals(-1, game.evaluatePosition(ongoing, game.player1));
    }

    @Test
    void minAndMaxMoveReturnTerminalScoresWithoutChangingBoard() {
        Game game = new Game();
        char[] board = {'O', 'O', 'O', 'X', 'X', ' ', ' ', ' ', ' '};
        char[] original = board.clone();

        game.symbol = 'O';

        assertEquals(Game.INF, game.MinMove(board, game.player2));
        assertEquals(Game.INF, game.MaxMove(board, game.player2));
        assertArrayEquals(original, board);
    }

    @Test
    void miniMaxChoosesImmediateWinningMoveAndRestoresBoard() {
        Game game = new Game();
        char[] board = {
            'O', 'O', ' ',
            'X', 'X', ' ',
            ' ', ' ', ' '
        };
        char[] original = board.clone();

        int move = game.MiniMax(board, game.player2);

        assertEquals(3, move);
        assertArrayEquals(original, board);
        assertEquals(0, game.q);
    }

    @Test
    void ticTacToeCellStoresCoordinatesNumberAndMarker() {
        TicTacToeCell cell = new TicTacToeCell(4, 1, 2);

        assertEquals(4, cell.getNum());
        assertEquals(1, cell.getCol());
        assertEquals(2, cell.getRow());
        assertEquals(' ', cell.getMarker());
        assertEquals(" ", cell.getText());
        assertEquals("Arial", cell.getFont().getName());
        assertEquals(Font.PLAIN, cell.getFont().getStyle());
        assertEquals(40, cell.getFont().getSize());

        cell.setMarker("X");

        assertEquals('X', cell.getMarker());
        assertEquals("X", cell.getText());
        assertFalse(cell.isEnabled());
    }

    @Test
    void ticTacToePanelCreatesNineCellsWithExpectedPositions() {
        TicTacToePanel panel = new TicTacToePanel(new GridLayout(3, 3));

        assertEquals(9, panel.getComponentCount());
        for (int i = 0; i < 9; i++) {
            TicTacToeCell cell = (TicTacToeCell) panel.getComponent(i);
            assertEquals(i, cell.getNum());
            assertEquals(i % 3, cell.getCol());
            assertEquals(i / 3, cell.getRow());
            assertEquals(' ', cell.getMarker());
        }
    }

    @Test
    void utilityPrintsCharIntAndMoveBoards() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            Utility.print(new char[] {'X', 'O', ' ', ' ', 'X', ' ', 'O', ' ', 'X'});
            Utility.print(new int[] {1, 2, 3, 4, 5, 6, 7, 8, 9});
            Utility.print(new ArrayList<Integer>(Arrays.asList(0, 4, 8)));
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(
            System.lineSeparator()
                + "X-O- - -X- -O- -X-" + System.lineSeparator()
                + System.lineSeparator()
                + "1-2-3-4-5-6-7-8-9-" + System.lineSeparator()
                + System.lineSeparator()
                + "0-4-8-" + System.lineSeparator(),
            output.toString()
        );
    }
}
