package com.mycompany.app;

import org.junit.Test;
import java.util.ArrayList;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.Assert.*;

public class UtilityTest {
    
    @Test
    public void testPrintCharArray() {
        char[] board = {'X', 'O', 'X', ' ', ' ', ' ', ' ', ' ', ' '};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        Utility.print(board);
        
        assertTrue(outContent.toString().contains("X-O-X"));
        System.setOut(System.out);
    }
    
    @Test
    public void testPrintIntArray() {
        int[] board = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        Utility.print(board);
        
        assertTrue(outContent.toString().contains("1-2-3"));
        System.setOut(System.out);
    }
    
    @Test
    public void testPrintArrayList() {
        ArrayList<Integer> moves = new ArrayList<>();
        moves.add(1);
        moves.add(2);
        moves.add(3);
        
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        Utility.print(moves);
        
        assertTrue(outContent.toString().contains("1-2-3"));
        System.setOut(System.out);
    }
    
    @Test
    public void testPrintEmptyArrayList() {
        ArrayList<Integer> moves = new ArrayList<>();
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        
        Utility.print(moves);
        
        System.setOut(System.out);
    }
}