package com.mycompany.app;

import javax.swing.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AbstractSwingTest {
    
    private TicTacToePanel panel;
    
    @Before
    public void setUp() {
        panel = new TicTacToePanel(new GridLayout(3, 3));
    }
    
    @Test
    public void testPanelCreation() {
        assertNotNull(panel);
        assertEquals(9, panel.getComponentCount());
    }
    
    @Test
    public void testPanelLayout() {
        assertTrue(panel.getLayout() instanceof GridLayout);
        GridLayout layout = (GridLayout) panel.getLayout();
        assertEquals(3, layout.getRows());
        assertEquals(3, layout.getColumns());
    }
}