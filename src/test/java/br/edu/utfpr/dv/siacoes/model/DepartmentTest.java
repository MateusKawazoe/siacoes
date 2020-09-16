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
public class DepartmentTest {
    
    Department dep;
    
    @Before
    public void setUp() {
        dep = new Department();
        
        dep.setActive(true);
    }
    
    @Test
    public void isActive() {
        assertEquals(true, dep.getActive());
    }  
}
