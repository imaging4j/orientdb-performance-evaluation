package com.siams.orientdb.evaluation.geom.run;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.orientechnologies.orient.object.iterator.OObjectIteratorClass;
import com.siams.orientdb.evaluation.geom.objects.BoxShape;
import com.siams.orientdb.evaluation.geom.objects.Cell;
import com.siams.orientdb.evaluation.geom.objects.Shape;
import com.siams.orientdb.evaluation.geom.utils.CellIndex;
import com.siams.orientdb.evaluation.geom.utils.SpatialIndex;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
public class GeomCellStat {

    public static void main(String[] args) {
        final Stat[] stats = {
                new Stat("shape set size", cell -> cell.getShapes().size()),
                new Stat("index x", cell -> CellIndex.indexX(cell.getIndex())),
                new Stat("index y", cell -> CellIndex.indexY(cell.getIndex())),
                new Stat("index z", cell -> CellIndex.indexZ(cell.getIndex())),
        };

        try (final OObjectDatabaseTx db = new OObjectDatabaseTx(LocalDB.toURI()).open("admin", "admin")) {
            db.getEntityManager().registerEntityClasses(Cell.class.getPackage().getName());
            final OObjectIteratorClass<Cell> cells = db.browseClass(Cell.class);

            for (final Cell cell : cells) {
                for (final Stat stat : stats) {
                    stat.add(cell);
                }
            }
        }

        for (final Stat stat : stats) {
            System.out.println(stat);
        }
    }

    private static class Stat {
        private final String name;
        private final Function<Cell, Integer> function;
        private long min = Long.MAX_VALUE;
        private long max = Long.MIN_VALUE;
        private long cnt;
        private long sum;

        Stat(String name, Function<Cell, Integer> function) {
            this.name = name;
            this.function = function;
        }

        void add(Cell cell) {
            add(function.apply(cell));
        }

        void add(long value) {
            min = Math.min(min, value);
            max = Math.max(max, value);
            cnt++;
            sum += value;
        }

        @Override
        public String toString() {
            if (cnt == 0) {
                return "name: none";
            } else {
                return String.format("%16s:  min: %4d;  max: %4d;  mid: %4d", name, min, max, sum / cnt);
            }
        }
    }

}
