/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.utfpr.dv.siacoes.model;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Matka
 */
public class CampusTest {
    
    Campus camp;
    
    @Before
    public void setUp() {
        camp = new Campus();
        
        camp.setId(5);
    }
    
    @Test
    public void campusExists() {
        assertEquals(5, camp.getId());
    }  
}
