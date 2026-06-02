package com.mycompany.app;

// Игра "Крестики-нолики" (поле 3x3)
// Алгоритм поиска оптимального хода: минимакс с альфа-бета отсечением

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

// Возможные состояния партии
enum GameState { PLAYING, OWIN, XWIN, DRAW }

// Описание участника игры
class Participant {
    public char symbol;       // 'X' или 'O'
    public int  lastMove;
    public boolean winner;
}

class Board {

    public GameState status;
    public Participant first, second;
    public Participant current;   // чья сейчас очередь
    public int  lastMoveIndex;
    public char activeSymbol;     // символ, проверяемый в данный момент
    public char[] cells;          // 9 клеток поля (индексы 0..8)

    // Бесконечность для минимакса
    public static final int MAX_SCORE = 100;

    // Счётчик обработанных узлов дерева
    public int nodeCount;

    public Board() {
        first  = new Participant();
        second = new Participant();
        first.symbol  = 'X';
        second.symbol = 'O';
        status = GameState.PLAYING;
        cells = new char[9];
        for (int i = 0; i < 9; i++)
            cells[i] = ' ';
    }

    // ─── Проверка состояния ────────────────────────────────────────────────

    public GameState checkState(char[] board) {
        // Проверяем, выиграл ли activeSymbol
        boolean won =
            (board[0] == activeSymbol && board[1] == activeSymbol && board[2] == activeSymbol) ||
            (board[3] == activeSymbol && board[4] == activeSymbol && board[5] == activeSymbol) ||
            (board[6] == activeSymbol && board[7] == activeSymbol && board[8] == activeSymbol) ||
            (board[0] == activeSymbol && board[3] == activeSymbol && board[6] == activeSymbol) ||
            (board[1] == activeSymbol && board[4] == activeSymbol && board[7] == activeSymbol) ||
            (board[2] == activeSymbol && board[5] == activeSymbol && board[8] == activeSymbol) ||
            (board[0] == activeSymbol && board[4] == activeSymbol && board[8] == activeSymbol) ||
            (board[2] == activeSymbol && board[4] == activeSymbol && board[6] == activeSymbol);

        if (won) {
            return (activeSymbol == 'X') ? GameState.XWIN : GameState.OWIN;
        }

        // Ищем свободные клетки
        for (int i = 0; i < 9; i++)
            if (board[i] == ' ')
                return GameState.PLAYING;

        return GameState.DRAW;
    }

    // ─── Генерация доступных ходов ─────────────────────────────────────────

    void generateMoves(char[] board, ArrayList<Integer> list) {
        for (int i = 0; i < 9; i++)
            if (board[i] == ' ')
                list.add(i);
    }

    // ─── Оценка терминальной позиции ───────────────────────────────────────

    int evaluate(char[] board, Participant player) {
        GameState s = checkState(board);
        if (s == GameState.XWIN || s == GameState.OWIN || s == GameState.DRAW) {
            if ((s == GameState.XWIN && player.symbol == 'X') ||
                (s == GameState.OWIN && player.symbol == 'O'))
                return +MAX_SCORE;
            if ((s == GameState.XWIN && player.symbol == 'O') ||
                (s == GameState.OWIN && player.symbol == 'X'))
                return -MAX_SCORE;
            if (s == GameState.DRAW)
                return 0;
        }
        return -1; // позиция не терминальная
    }

    // ─── Минимакс с альфа-бета отсечением ─────────────────────────────────

    /**
     * Точка входа: выбирает лучший ход для игрока.
     * Возвращает номер клетки (1..9).
     */
    int chooseMove(char[] board, Participant player) {
        int bestScore = -MAX_SCORE;
        int idx = 0;
        int[] bestMoves = new int[9];

        ArrayList<Integer> moves = new ArrayList<>();
        generateMoves(board, moves);

        for (int i = 0; i < moves.size(); i++) {
            int pos = moves.get(i);
            board[pos]   = player.symbol;
            activeSymbol = player.symbol;

            // Противник минимизирует; передаём альфа-бета окно
            int score = minimize(board, player, -MAX_SCORE, +MAX_SCORE);

            if (score > bestScore) {
                bestScore = score;
                idx = 0;
                bestMoves[idx] = pos + 1;
            } else if (score == bestScore) {
                bestMoves[++idx] = pos + 1;
            }

            System.out.printf("\nвыбор хода: клетка %2d -> оценка %d", pos + 1, score);
            board[pos] = ' '; // откат
        }

        // Случайный выбор среди равноценных ходов
        if (idx > 0)
            idx = new Random().nextInt(idx + 1);

        System.out.printf("\nлучший ход: %d (оценка %d), узлов просмотрено: %d%n",
                bestMoves[idx], bestScore, nodeCount);
        nodeCount = 0;
        return bestMoves[idx];
    }

    /**
     * Ход противника — минимизирующий игрок (с альфа-бета отсечением).
     */
    int minimize(char[] board, Participant player, int alpha, int beta) {
        int score = evaluate(board, player);
        if (score != -1) return score;

        nodeCount++;
        char oppSymbol = (player.symbol == 'X') ? 'O' : 'X';

        ArrayList<Integer> moves = new ArrayList<>();
        generateMoves(board, moves);

        int minVal = +MAX_SCORE;
        for (int i = 0; i < moves.size(); i++) {
            int pos = moves.get(i);
            activeSymbol = oppSymbol;
            board[pos]   = oppSymbol;

            int val = maximize(board, player, alpha, beta);
            board[pos] = ' '; // откат

            if (val < minVal) minVal = val;
            if (minVal < beta) beta = minVal;
            if (beta <= alpha) break; // альфа-отсечение
        }
        return minVal;
    }

    /**
     * Ход нашего игрока — максимизирующий игрок (с альфа-бета отсечением).
     */
    int maximize(char[] board, Participant player, int alpha, int beta) {
        int score = evaluate(board, player);
        if (score != -1) return score;

        nodeCount++;
        char ownSymbol = player.symbol;

        ArrayList<Integer> moves = new ArrayList<>();
        generateMoves(board, moves);

        int maxVal = -MAX_SCORE;
        for (int i = 0; i < moves.size(); i++) {
            int pos = moves.get(i);
            activeSymbol = ownSymbol;
            board[pos]   = ownSymbol;

            int val = minimize(board, player, alpha, beta);
            board[pos] = ' '; // откат

            if (val > maxVal) maxVal = val;
            if (maxVal > alpha) alpha = maxVal;
            if (beta <= alpha) break; // бета-отсечение
        }
        return maxVal;
    }
}

// ─── Вспомогательный класс для отладочного вывода ─────────────────────────

class Printer {

    public static void print(char[] board) {
        System.out.println();
        for (int j = 0; j < 9; j++)
            System.out.print(board[j] + "-");
        System.out.println();
    }

    public static void print(int[] board) {
        System.out.println();
        for (int j = 0; j < 9; j++)
            System.out.print(board[j] + "-");
        System.out.println();
    }

    public static void print(ArrayList<Integer> moves) {
        System.out.println();
        for (int j = 0; j < moves.size(); j++)
            System.out.print(moves.get(j) + "-");
        System.out.println();
    }
}

// ─── GUI: одна клетка поля ─────────────────────────────────────────────────

class BoardCell extends JButton {

    private int  num;
    private int  row;
    private int  col;
    private char marker;

    public BoardCell(int num, int col, int row) {
        this.num = num;
        this.row = row;
        this.col = col;
        marker = ' ';
        setText(Character.toString(marker));
        setFont(new Font("Arial", Font.PLAIN, 40));
    }

    public void setMarker(String m) {
        marker = m.charAt(0);
        setText(m);
        setEnabled(false);
    }

    public char getMarker() { return marker; }
    public int  getRow()    { return row;    }
    public int  getCol()    { return col;    }
    public int  getNum()    { return num;    }
}

// ─── GUI: игровая панель ───────────────────────────────────────────────────

class GamePanel extends JPanel implements ActionListener {

    private Board board;
    private BoardCell[] cells = new BoardCell[9];

    GamePanel(GridLayout layout) {
        super(layout);
        // Создаём 9 клеток (номер, столбец, строка)
        int[][] coords = {
            {0,0,0},{1,1,0},{2,2,0},
            {3,0,1},{4,1,1},{5,2,1},
            {6,0,2},{7,1,2},{8,2,2}
        };
        for (int[] c : coords)
            createCell(c[0], c[1], c[2]);

        board = new Board();
        board.current = board.first;
    }

    private void createCell(int num, int col, int row) {
        cells[num] = new BoardCell(num, col, row);
        cells[num].addActionListener(this);
        add(cells[num]);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        board.first.lastMove  = -1;
        board.second.lastMove = -1;

        // Применяем ход игрока
        int i = 0;
        for (BoardCell c : cells) {
            if (e.getSource() == c)
                c.setMarker(Character.toString(board.current.symbol));
            board.cells[i++] = c.getMarker();
        }

        if (board.current == board.first) {
            // Компьютер ходит за второго
            board.second.lastMove = board.chooseMove(board.cells, board.second);
            board.lastMoveIndex   = board.second.lastMove;
            board.activeSymbol    = board.second.symbol;
            board.current         = board.second;
            if (board.second.lastMove > 0)
                cells[board.second.lastMove - 1].doClick();
        } else {
            board.lastMoveIndex = board.first.lastMove;
            board.activeSymbol  = board.first.symbol;
            board.current       = board.first;
        }

        board.status = board.checkState(board.cells);
        checkGameOver();
    }

    private void checkGameOver() {
        String msg = null;
        if      (board.status == GameState.XWIN) msg = "Победили крестики!";
        else if (board.status == GameState.OWIN) msg = "Победили нолики!";
        else if (board.status == GameState.DRAW) msg = "Ничья!";

        if (msg != null) {
            JOptionPane.showMessageDialog(null, msg, "Результат", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}

// ─── Точка входа ───────────────────────────────────────────────────────────

public class Program {

    public static void main(String[] args) {
        JFrame window = new JFrame("Крестики-нолики");
        window.add(new GamePanel(new GridLayout(3, 3)));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setBounds(100, 100, 500, 500);
        window.setVisible(true);
    }
}
