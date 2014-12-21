/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cagricelebi.coreutils;

import com.cagricelebi.coreutils.log.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author azureel
 */
public class HelperTest extends TestCase {

    private static final Logger logger = Logger.getLogger(HelperTest.class);

    public HelperTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of isEmpty method, of class Helper.
     */
    public void testIsEmpty_String() {
        logger.log("isEmpty");
        String str = "";
        boolean expResult = true;
        boolean result = Helper.isEmpty(str);
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class Helper.
     */
    public void testIsEmpty_Map() {
        logger.log("isEmpty");
        boolean expResult = true;
        Map<String, String> mm = new HashMap<>();
        boolean result = Helper.isEmpty(mm);
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class Helper.
     */
    public void testIsEmpty_List() {
        logger.log("isEmpty");
        boolean expResult = true;
        List<String> ll = new ArrayList<>();
        boolean result = Helper.isEmpty(ll);
        assertEquals(expResult, result);
    }

    /**
     * Test of isValidIp method, of class Helper.
     */
    public void testIsValidIp() {
        logger.log("isValidIp");
        String ipStr = Helper.generateRandomIp();
        boolean expResult = true;
        boolean result = Helper.isValidIp(ipStr);
        assertEquals(expResult, result);
    }

}
