package com.siams.orientdb.evaluation.geom.utils;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/14/2015.
 */
public class CellIndex {
    public static long toIndex(int x, int y, int z, int label) {
        assert x <= 0xffff && x >= 0;
        assert y <= 0xffff && y >= 0;
        assert z <= 0xff && z >= 0;
        assert label <= 0xffff && label >= 0;
        
        long result = 0;
        result = result | (z & 0xff);

        result = result << 16;
        result = result | (y & 0xffff);

        result = result << 16;
        result = result | (x & 0xffff);

        result = result << 16;
        result = result | (label & 0xffff);

        return result;
    }

    public static int indexX(long index) {
        return (int) ((index >> 16) & 0xffff);
    }

    public static int indexY(long index) {
        return (int) ((index >> 32) & 0xffff);
    }

    public static int indexZ(long index) {
        return (int) ((index >> 48) & 0xff);
    }

    public static int indexLabel(long index) {
        return (int) (index & 0xffff);
    }
}
