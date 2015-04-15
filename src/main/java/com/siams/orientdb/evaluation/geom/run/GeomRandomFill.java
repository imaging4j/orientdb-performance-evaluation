package com.siams.orientdb.evaluation.geom.run;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.siams.orientdb.evaluation.geom.objects.BoxShape;
import com.siams.orientdb.evaluation.geom.objects.Cell;
import com.siams.orientdb.evaluation.geom.utils.SpatialIndex;

import java.util.Random;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
public class GeomRandomFill {

    private long shapeInserts;
    private long cellUpdates;
    private long time;
    private final SpatialIndex spatialIndex = new SpatialIndex();

    public static void main(String[] args) {
        try (final OObjectDatabaseTx db = new OObjectDatabaseTx(LocalDB.toURI()).open("admin", "admin")) {
            db.getEntityManager().registerEntityClasses(Cell.class.getPackage().getName());

            System.out.println("warming up...");
            final GeomRandomFill action = new GeomRandomFill();
            action.execute(db, 10_000);

            System.out.println("executing...");
            action.execute(db, 1_000_000);

            System.out.printf("cell count:\t%d%n", db.countClass(Cell.class));
            System.out.printf("cell updates:\t%d%n", action.cellUpdates);
            System.out.printf("shape inserts:\t%d%n", action.shapeInserts);

            System.out.printf("done: %dms / %d = %s ms/shape%n",
                    action.time, action.shapeInserts,
                    ((double) action.time) / action.shapeInserts
            );

            System.out.printf("%s ms/(cellUpdates+shapeInserts)%n",
                    ((double) action.time) / (action.cellUpdates + action.shapeInserts)
            );
        }
        System.out.println("done");
    }

    private void execute(OObjectDatabaseTx db, int shapeInsertCount) {
        this.shapeInserts = 0;
        this.cellUpdates = 0;
        final Random random = new Random(157);
        final long t0 = System.currentTimeMillis();
        this.shapeInserts = shapeInsertCount;
        for (int i = 0; i < shapeInsertCount; i++) {
            this.cellUpdates += spatialIndex.insert(
                    db,
                    new BoxShape(
                            random.nextInt(200_000),
                            random.nextInt(200_000),
                            7 + random.nextInt(300),
                            7 + random.nextInt(300)),
                    0);
        }
        time = System.currentTimeMillis() - t0;
    }

}
