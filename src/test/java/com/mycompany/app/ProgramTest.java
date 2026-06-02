package com.mycompany.app;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Font;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProgramTest {

    private Board board;

    @BeforeEach
    void init() {
        board = new Board();
    }

    // --- initial state ---

    @Test
    void newBoardHasEmptyCellsAndCorrectSymbols() {
        assertEquals(GameState.PLAYING, board.status);
        assertEquals('X', board.first.symbol);
        assertEquals('O', board.second.symbol);
        assertEquals(9, board.cells.length);
        assertArrayEquals(new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '}, board.cells);
    }

    @Test
    void maxScoreConstantIs100() {
        assertEquals(100, Board.MAX_SCORE);
    }

    @Test
    void nodeCountStartsAtZero() {
        assertEquals(0, board.nodeCount);
    }

    // --- checkState: X wins ---

    @Test
    void xWinsOnTopRow() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'X','X','X',' ','O',' ',' ','O',' '}));
    }

    @Test
    void xWinsOnMiddleRow() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'O',' ',' ','X','X','X',' ','O',' '}));
    }

    @Test
    void xWinsOnBottomRow() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{' ','O',' ',' ','O',' ','X','X','X'}));
    }

    @Test
    void xWinsOnLeftColumn() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'X','O',' ','X','O',' ','X',' ',' '}));
    }

    @Test
    void xWinsOnMiddleColumn() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{' ','X','O',' ','X','O',' ','X',' '}));
    }

    @Test
    void xWinsOnRightColumn() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'O',' ','X','O',' ','X',' ',' ','X'}));
    }

    @Test
    void xWinsOnMainDiagonal() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'X',' ','O',' ','X','O',' ',' ','X'}));
    }

    @Test
    void xWinsOnAntiDiagonal() {
        board.activeSymbol = 'X';
        assertEquals(GameState.XWIN, board.checkState(new char[]{'O',' ','X',' ','X',' ','X',' ','O'}));
    }

    // --- checkState: O wins ---

    @Test
    void oWinsOnDiagonal() {
        board.activeSymbol = 'O';
        assertEquals(GameState.OWIN, board.checkState(new char[]{'O',' ','X','X','O',' ',' ',' ','O'}));
    }

    @Test
    void oWinsOnRow() {
        board.activeSymbol = 'O';
        assertEquals(GameState.OWIN, board.checkState(new char[]{'X','X',' ','O','O','O',' ',' ','X'}));
    }

    @Test
    void oWinsOnColumn() {
        board.activeSymbol = 'O';
        assertEquals(GameState.OWIN, board.checkState(new char[]{'O','X',' ','O','X',' ','O',' ','X'}));
    }

    // --- checkState: draw and ongoing ---

    @Test
    void emptyBoardIsPlaying() {
        board.activeSymbol = 'X';
        assertEquals(GameState.PLAYING, board.checkState(new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '}));
    }

    @Test
    void oneEmptyCellIsStillPlaying() {
        board.activeSymbol = 'X';
        assertEquals(GameState.PLAYING, board.checkState(new char[]{'X','O','X','O','X','O','O','X',' '}));
    }

    @Test
    void fullBoardWithNoWinnerIsDraw() {
        board.activeSymbol = 'X';
        assertEquals(GameState.DRAW, board.checkState(new char[]{'X','O','X','X','O','O','O','X','X'}));
    }

    @Test
    void anotherDrawBoard() {
        board.activeSymbol = 'X';
        assertEquals(GameState.DRAW, board.checkState(new char[]{'X','O','X','O','O','X','X','X','O'}));
    }

    // --- generateMoves ---

    @Test
    void generateMovesReturnsOnlyEmptyCells() {
        ArrayList<Integer> moves = new ArrayList<>();
        board.generateMoves(new char[]{'X',' ','O',' ','X',' ','O',' ','X'}, moves);
        assertIterableEquals(Arrays.asList(1, 3, 5, 7), moves);
    }

    @Test
    void generateMovesOnFullBoardReturnsEmpty() {
        ArrayList<Integer> moves = new ArrayList<>();
        board.generateMoves(new char[]{'X','O','X','O','X','O','O','X','X'}, moves);
        assertTrue(moves.isEmpty());
    }

    @Test
    void generateMovesOnEmptyBoardReturnsNine() {
        ArrayList<Integer> moves = new ArrayList<>();
        board.generateMoves(new char[]{' ',' ',' ',' ',' ',' ',' ',' ',' '}, moves);
        assertEquals(9, moves.size());
    }

    // --- evaluate ---

    @Test
    void evaluateReturnsMaxForWinner() {
        board.activeSymbol = 'X';
        char[] b = {'X','X','X','O','O',' ',' ',' ',' '};
        assertEquals(Board.MAX_SCORE, board.evaluate(b, board.first));
    }

    @Test
    void evaluateReturnsNegativeMaxForLoser() {
        board.activeSymbol = 'X';
        char[] b = {'X','X','X','O','O',' ',' ',' ',' '};
        assertEquals(-Board.MAX_SCORE, board.evaluate(b, board.second));
    }

    @Test
    void evaluateReturnsZeroForDraw() {
        board.activeSymbol = 'X';
        char[] b = {'X','O','X','X','O','O','O','X','X'};
        assertEquals(0, board.evaluate(b, board.first));
    }

    @Test
    void evaluateReturnsMinusOneForOngoingGame() {
        board.activeSymbol = 'X';
        char[] b = {'X','O','X',' ','O',' ',' ',' ',' '};
        assertEquals(-1, board.evaluate(b, board.first));
    }

    @Test
    void evaluateOWinFromBothPerspectives() {
        board.activeSymbol = 'O';
        char[] b = {'O','O','O','X','X',' ',' ',' ',' '};
        assertEquals(Board.MAX_SCORE,  board.evaluate(b, board.second));
        assertEquals(-Board.MAX_SCORE, board.evaluate(b, board.first));
    }

    // --- minimize / maximize ---

    @Test
    void minimizeReturnsMaxWhenOAlreadyWon() {
        char[] b = {'O','O','O','X','X',' ',' ',' ',' '};
        char[] snapshot = b.clone();
        board.activeSymbol = 'O';
        assertEquals(Board.MAX_SCORE, board.minimize(b, board.second, -Board.MAX_SCORE, +Board.MAX_SCORE));
        assertArrayEquals(snapshot, b);
    }

    @Test
    void maximizeReturnsMaxWhenOAlreadyWon() {
        char[] b = {'O','O','O','X','X',' ',' ',' ',' '};
        char[] snapshot = b.clone();
        board.activeSymbol = 'O';
        assertEquals(Board.MAX_SCORE, board.maximize(b, board.second, -Board.MAX_SCORE, +Board.MAX_SCORE));
        assertArrayEquals(snapshot, b);
    }

    @Test
    void minimizeReturnsNegativeMaxWhenXAlreadyWon() {
        board.activeSymbol = 'X';
        char[] b = {'X','X','X','O','O',' ',' ',' ',' '};
        assertEquals(-Board.MAX_SCORE, board.minimize(b, board.second, -Board.MAX_SCORE, +Board.MAX_SCORE));
    }

    // --- chooseMove ---

    @Test
    void chooseMovePicksImmediateWin() {
        char[] b = {'O','O',' ','X','X',' ',' ',' ',' '};
        char[] snapshot = b.clone();
        int move = board.chooseMove(b, board.second);
        assertEquals(3, move);
        assertArrayEquals(snapshot, b);
        assertEquals(0, board.nodeCount);
    }

    @Test
    void chooseMoveDoesNotModifyBoard() {
        char[] b = {' ',' ',' ',' ','X',' ',' ',' ',' '};
        char[] snapshot = b.clone();
        board.chooseMove(b, board.second);
        assertArrayEquals(snapshot, b);
    }

    @Test
    void nodeCountResetAfterChooseMove() {
        char[] b = {'O','O',' ','X','X',' ',' ',' ',' '};
        board.chooseMove(b, board.second);
        assertEquals(0, board.nodeCount);
    }

    // --- BoardCell ---

    @Test
    void boardCellStoresNumColRowAndDefaultMarker() {
        BoardCell cell = new BoardCell(5, 2, 1);
        assertEquals(5, cell.getNum());
        assertEquals(2, cell.getCol());
        assertEquals(1, cell.getRow());
        assertEquals(' ', cell.getMarker());
        assertEquals(" ", cell.getText());
    }

    @Test
    void boardCellUsesArial40PlainFont() {
        BoardCell cell = new BoardCell(0, 0, 0);
        Font f = cell.getFont();
        assertEquals("Arial", f.getName());
        assertEquals(Font.PLAIN, f.getStyle());
        assertEquals(40, f.getSize());
    }

    @Test
    void setMarkerXDisablesCell() {
        BoardCell cell = new BoardCell(3, 0, 1);
        cell.setMarker("X");
        assertEquals('X', cell.getMarker());
        assertEquals("X", cell.getText());
        assertFalse(cell.isEnabled());
    }

    @Test
    void setMarkerODisablesCell() {
        BoardCell cell = new BoardCell(7, 1, 2);
        cell.setMarker("O");
        assertEquals('O', cell.getMarker());
        assertFalse(cell.isEnabled());
    }

    // --- GamePanel ---

    @Test
    void gamePanelCreatesNineCellsWithCorrectPositions() {
        GamePanel panel = new GamePanel(new GridLayout(3, 3));
        assertEquals(9, panel.getComponentCount());
        for (int i = 0; i < 9; i++) {
            BoardCell cell = (BoardCell) panel.getComponent(i);
            assertEquals(i,     cell.getNum());
            assertEquals(i % 3, cell.getCol());
            assertEquals(i / 3, cell.getRow());
            assertEquals(' ',   cell.getMarker());
        }
    }

    // --- Printer ---

    @Test
    void printerOutputsCharArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));
        try {
            Printer.print(new char[]{'X','O',' ',' ','X',' ','O',' ','X'});
        } finally {
            System.setOut(old);
        }
        assertEquals(System.lineSeparator() + "X-O- - -X- -O- -X-" + System.lineSeparator(), out.toString());
    }

    @Test
    void printerOutputsIntArray() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));
        try {
            Printer.print(new int[]{1,2,3,4,5,6,7,8,9});
        } finally {
            System.setOut(old);
        }
        assertEquals(System.lineSeparator() + "1-2-3-4-5-6-7-8-9-" + System.lineSeparator(), out.toString());
    }

    @Test
    void printerOutputsMoveList() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));
        try {
            Printer.print(new ArrayList<>(Arrays.asList(0, 4, 8)));
        } finally {
            System.setOut(old);
        }
        assertEquals(System.lineSeparator() + "0-4-8-" + System.lineSeparator(), out.toString());
    }
}
