package com.mycompany.app;

// Игра "Крестики-нолики" (поле 3x3)
// Реализация алгоритма минимакс

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

enum GameState { ACTIVE, O_WON, X_WON, TIE };

class GameParticipant {
    public char sign;
    public int chosenCell;
    public boolean isChosen;
    public boolean isWinner;
}

class TicTacGame {
    public GameState currentState;
    public GameParticipant participantA, participantB;
    public GameParticipant activePlayer;
    public int lastMove;
    public char currentSymbol;
    public static final int MAX_SCORE = 100;
    public int calculationCount;
    public char[] playingField;

    public TicTacGame() {
        participantA = new GameParticipant();
        participantB = new GameParticipant();
        participantA.sign = 'X';
        participantB.sign = 'O';
        currentState = GameState.ACTIVE;
        playingField = new char[9];
        for (int i = 0; i < 9; i++)
            playingField[i] = ' ';
    }

    // определение текущего состояния игры
    public GameState determineState(char[] field) {
        GameState state = GameState.ACTIVE;
        
        boolean winCondition = (field[0] == currentSymbol && field[1] == currentSymbol && field[2] == currentSymbol) ||
                               (field[3] == currentSymbol && field[4] == currentSymbol && field[5] == currentSymbol) ||
                               (field[6] == currentSymbol && field[7] == currentSymbol && field[8] == currentSymbol) ||
                               (field[0] == currentSymbol && field[3] == currentSymbol && field[6] == currentSymbol) ||
                               (field[1] == currentSymbol && field[4] == currentSymbol && field[7] == currentSymbol) ||
                               (field[2] == currentSymbol && field[5] == currentSymbol && field[8] == currentSymbol) ||
                               (field[0] == currentSymbol && field[4] == currentSymbol && field[8] == currentSymbol) ||
                               (field[2] == currentSymbol && field[4] == currentSymbol && field[6] == currentSymbol);
        
        if (winCondition) {
            state = (currentSymbol == 'X') ? GameState.X_WON : GameState.O_WON;
        } else {
            state = GameState.TIE;
            for (int i = 0; i < 9; i++) {
                if (field[i] == ' ') {
                    state = GameState.ACTIVE;
                    break;
                }
            }
        }
        return state;
    }

    // генерация списка доступных ходов
    void generateAvailableMoves(char[] field, ArrayList<Integer> movesList) {
        for (int i = 0; i < 9; i++) {
            if (field[i] == ' ')
                movesList.add(i);
        }
    }

    // оценка текущей позиции
    int evaluatePosition(char[] field, GameParticipant player) {
        GameState state = determineState(field);
        if (state == GameState.X_WON || state == GameState.O_WON || state == GameState.TIE) {
            if ((state == GameState.X_WON && player.sign == 'X') || (state == GameState.O_WON && player.sign == 'O'))
                return +TicTacGame.MAX_SCORE;
            else if ((state == GameState.X_WON && player.sign == 'O') || (state == GameState.O_WON && player.sign == 'X'))
                return -TicTacGame.MAX_SCORE;
            else if (state == GameState.TIE)
                return 0;
        }
        return -1;
    }

    int findBestMove(char[] field, GameParticipant player) {
        int bestValue = -TicTacGame.MAX_SCORE;
        int bestIndex = 0;
        ArrayList<Integer> availableSpots = new ArrayList<>();
        int[] bestMovesArray = new int[9];

        generateAvailableMoves(field, availableSpots);

        while (!availableSpots.isEmpty()) {
            int movePos = availableSpots.get(0);
            field[movePos] = player.sign;
            currentSymbol = player.sign;

            int moveValue = findMinMove(field, player);

            if (moveValue > bestValue) {
                bestValue = moveValue;
                bestIndex = 0;
                bestMovesArray[bestIndex] = movePos + 1;
            } else if (moveValue == bestValue) {
                bestIndex++;
                bestMovesArray[bestIndex] = movePos + 1;
            }

            System.out.printf("\nminimax: %3d(%1d) ", movePos + 1, moveValue);
            field[movePos] = ' ';
            availableSpots.remove(0);
        }

        if (bestIndex > 0) {
            Random rand = new Random();
            bestIndex = rand.nextInt(bestIndex);
        }

        System.out.printf("\nminimax best: %3d(%1d) ", bestMovesArray[bestIndex], bestValue);
        System.out.printf("Steps counted: %d", calculationCount);
        calculationCount = 0;
        return bestMovesArray[bestIndex];
    }

    int findMinMove(char[] field, GameParticipant player) {
        int positionValue = evaluatePosition(field, player);
        if (positionValue != -1)
            return positionValue;
            
        calculationCount++;
        int lowestValue = +TicTacGame.MAX_SCORE;
        ArrayList<Integer> possibleSpots = new ArrayList<>();

        generateAvailableMoves(field, possibleSpots);

        while (!possibleSpots.isEmpty()) {
            int movePos = possibleSpots.get(0);
            currentSymbol = (player.sign == 'X') ? 'O' : 'X';
            field[movePos] = currentSymbol;

            int currentValue = findMaxMove(field, player);

            if (currentValue < lowestValue) {
                lowestValue = currentValue;
            }
            field[movePos] = ' ';
            possibleSpots.remove(0);
        }
        return lowestValue;
    }

    int findMaxMove(char[] field, GameParticipant player) {
        int positionValue = evaluatePosition(field, player);
        if (positionValue != -1)
            return positionValue;
            
        calculationCount++;
        int highestValue = -TicTacGame.MAX_SCORE;
        ArrayList<Integer> freeSpots = new ArrayList<>();
        
        generateAvailableMoves(field, freeSpots);
        
        while (!freeSpots.isEmpty()) {
            int movePos = freeSpots.get(0);
            currentSymbol = (player.sign == 'X') ? 'X' : 'O';
            field[movePos] = currentSymbol;
            
            int currentValue = findMinMove(field, player);
            
            if (currentValue > highestValue) {
                highestValue = currentValue;
            }
            field[movePos] = ' ';
            freeSpots.remove(0);
        }
        return highestValue;
    }
}

public class Program {
    public static FileWriter logWriter;
    public static PrintWriter outputWriter;
    
    public static void main(String[] args) throws IOException {
        JFrame mainFrame = new JFrame("Tic-Tac-Toe Game");
        mainFrame.add(new GamePanel(new GridLayout(3, 3)));
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setBounds(50, 50, 500, 500);
        mainFrame.setVisible(true);
    }
}

class GameCell extends JButton {
    private int cellNumber;
    private int rowPosition;
    private int colPosition;
    private char cellMark;

    public GameCell(int num, int x, int y) {
        this.cellNumber = num;
        this.rowPosition = y;
        this.colPosition = x;
        this.cellMark = ' ';
        setText(Character.toString(cellMark));
        setFont(new Font("Verdana", Font.BOLD, 40));
    }
    
    public void placeMark(String mark) {
        cellMark = mark.charAt(0);
        setText(mark);
        setEnabled(false);
    }
    
    public char getMark() {
        return cellMark;
    }
    
    public int getRowPosition() {
        return rowPosition;
    }
    
    public int getColPosition() {
        return colPosition;
    }
    
    public int getCellNumber() {
        return cellNumber;
    }
}

class GameUtilities {
    public static void displayBoard(char[] board) {
        System.out.println();
        for (int i = 0; i < 9; i++)
            System.out.print(board[i] + "-");
        System.out.println();
    }
    
    public static void displayNumbers(int[] numbers) {
        System.out.println();
        for (int i = 0; i < 9; i++)
            System.out.print(numbers[i] + "-");
        System.out.println();
    }
    
    public static void displayList(ArrayList<Integer> items) {
        System.out.println();
        for (int i = 0; i < items.size(); i++)
            System.out.print(items.get(i) + "-");
        System.out.println();
    }
}

class GamePanel extends JPanel implements ActionListener {
    private TicTacGame gameLogic;
    private GameCell[] boardCells = new GameCell[9];

    private void initializeCell(int position, int column, int row) {
        boardCells[position] = new GameCell(position, column, row);
        boardCells[position].addActionListener(this);
        add(boardCells[position]);
    }

    public GamePanel(GridLayout layout) {
        super(layout);
        initializeCell(0, 0, 0);
        initializeCell(1, 1, 0);
        initializeCell(2, 2, 0);
        initializeCell(3, 0, 1);
        initializeCell(4, 1, 1);
        initializeCell(5, 2, 1);
        initializeCell(6, 0, 2);
        initializeCell(7, 1, 2);
        initializeCell(8, 2, 2);
        
        gameLogic = new TicTacGame();
        gameLogic.activePlayer = gameLogic.participantA;
    }

    public void actionPerformed(ActionEvent event) {
        gameLogic.participantA.chosenCell = -1;
        gameLogic.participantB.chosenCell = -1;

        int idx = 0;
        for (GameCell cell : boardCells) {
            if (event.getSource() == cell) {
                cell.placeMark(Character.toString(gameLogic.activePlayer.sign));
            }
            gameLogic.playingField[idx++] = cell.getMark();
        }
        
        if (gameLogic.activePlayer == gameLogic.participantA) {
            gameLogic.participantB.chosenCell = gameLogic.findBestMove(gameLogic.playingField, gameLogic.participantB);
            gameLogic.lastMove = gameLogic.participantB.chosenCell;
            gameLogic.currentSymbol = gameLogic.participantB.sign;
            gameLogic.activePlayer = gameLogic.participantB;
            
            if (gameLogic.participantB.chosenCell > 0)
                boardCells[gameLogic.participantB.chosenCell - 1].doClick();
        } else {
            gameLogic.lastMove = gameLogic.participantA.chosenCell;
            gameLogic.currentSymbol = gameLogic.participantA.sign;
            gameLogic.activePlayer = gameLogic.participantA;
        }

        gameLogic.currentState = gameLogic.determineState(gameLogic.playingField);

        if (gameLogic.currentState == GameState.X_WON) {
            JOptionPane.showMessageDialog(null, "X победили!", "Результат", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (gameLogic.currentState == GameState.O_WON) {
            JOptionPane.showMessageDialog(null, "O победили!", "Результат", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (gameLogic.currentState == GameState.TIE) {
            JOptionPane.showMessageDialog(null, "Ничья!", "Результат", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
    }
}