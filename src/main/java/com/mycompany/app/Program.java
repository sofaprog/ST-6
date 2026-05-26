package com.mycompany.app;

import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TicTacToePanelTest {
    
    private TicTacToePanel panel;
    
    @Before
    public void setUp() {
        panel = new TicTacToePanel(new GridLayout(3, 3));
    }
    
    @Test
    public void testPanelNotNull() {
        assertNotNull(panel);
    }
    
    @Test
    public void testCellsCount() {
        assertEquals(9, panel.getComponentCount());
    }
    
    @Test
    public void testAllCellsAreButtons() {
        for (int i = 0; i < panel.getComponentCount(); i++) {
            assertTrue(panel.getComponent(i) instanceof TicTacToeCell);
        }
    }
}