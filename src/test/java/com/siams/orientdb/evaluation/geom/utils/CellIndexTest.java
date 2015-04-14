package com.siams.orientdb.evaluation.geom.utils;

import org.junit.Test;

import static com.siams.orientdb.evaluation.geom.utils.CellIndex.*;
import static org.junit.Assert.*;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/15/2015.
 */
public class CellIndexTest {

    @Test
    public void testToIndex() throws Exception {
        assertEquals(toIndex(0, 0, 0, 0), 0l);
    }

    @Test(expected = AssertionError.class)
    public void testToIndexX() throws Exception {
        toIndex(-1, 0, 0, 0);
    }

    @Test(expected = AssertionError.class)
    public void testToIndexY() throws Exception {
        toIndex(0, 65536, 0, 0);
    }

    @Test(expected = AssertionError.class)
    public void testToIndexZ() throws Exception {
        toIndex(0, 0, 256, 0);
    }

    @Test
    public void testIndexX() throws Exception {
        assertEquals(indexX(toIndex(1, 2, 3, 4)), 1);
        assertEquals(indexX(toIndex(65535, 2, 3, 4)), 65535);
        assertEquals(indexX(toIndex(0, 2, 3, 4)), 0);
    }

    @Test
    public void testIndexY() throws Exception {
        assertEquals(indexY(toIndex(1, 2, 3, 4)), 2);
        assertEquals(indexY(toIndex(1, 65535, 3, 4)), 65535);
        assertEquals(indexY(toIndex(1, 0, 3, 4)), 0);
    }

    @Test
    public void testIndexZ() throws Exception {
        assertEquals(indexZ(toIndex(1, 2, 3, 4)), 3);
        assertEquals(indexZ(toIndex(1, 2, 255, 4)), 255);
        assertEquals(indexZ(toIndex(1, 2, 0, 4)), 0);
    }

    @Test
    public void testIndexLabel() throws Exception {
        assertEquals(indexLabel(toIndex(1, 2, 3, 4)), 4);
        assertEquals(indexLabel(toIndex(1, 2, 3, 65535)), 65535);
        assertEquals(indexLabel(toIndex(1, 2, 3, 0)), 0);
    }
}