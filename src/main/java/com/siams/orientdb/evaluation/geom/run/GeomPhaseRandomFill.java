package com.siams.orientdb.evaluation.geom.run;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import com.siams.orientdb.evaluation.geom.objects.BoxShape;
import com.siams.orientdb.evaluation.geom.objects.Cell;
import com.siams.orientdb.evaluation.geom.objects.Phase;
import com.siams.orientdb.evaluation.geom.objects.Shape;

import java.util.Random;
import java.util.Set;

/**
 *
 * Created by alexei.vylegzhanin@gmail.com on 4/16/2015.
 */
public class GeomPhaseRandomFill {

    private long time1;
    private long time2;
    private final int shapeClusterId;
    private final OObjectDatabaseTx db;
    private final long shapeCount;
    private long phaseShapesInserts;
    private final ODatabaseDocumentInternal underlying;
    private final ODocument oPhase;
    private final Set<ORecord> oPhaseShapes;

    public GeomPhaseRandomFill(OObjectDatabaseTx db, long shapeCount) {
        this.db = db;
        this.shapeCount = shapeCount;
        db.command(new OCommandSQL("delete * form Phase where name = 'abc'"));
        final Phase phase = db.newInstance(Phase.class, "abc");
        db.save(phase);
        oPhase = db.getRecordByUserObject(phase, true);
        oPhaseShapes = oPhase.field("shapes");

        shapeClusterId = db.getIdentity(db.browseClass(BoxShape.class).next()).getClusterId();
        this.underlying = db.getUnderlying();
    }

    public static void main(String[] args) {
        try (final OObjectDatabaseTx db = new OObjectDatabaseTx(LocalDB.toURI()).open("admin", "admin")) {
            db.getEntityManager().registerEntityClasses(Cell.class.getPackage().getName());

            final long shapeCount = db.countClass(Shape.class);
            final long warmingCount = Math.min(10_000, shapeCount);

            System.out.println("warming up(" + warmingCount + ")...");
            final GeomPhaseRandomFill action = new GeomPhaseRandomFill(db, shapeCount);
            action.execute((int) warmingCount);

            System.out.println("executing(" + shapeCount + ")...");
            action.execute((int) shapeCount);

            System.out.printf("phase shapes inserts:\t%d%n", action.phaseShapesInserts);

            System.out.printf("inserting: %dms / %d = %s ms/(load+add)%n",
                    action.time1, action.phaseShapesInserts,
                    ((double) action.time1) / action.phaseShapesInserts
            );

            System.out.printf("saving: %dms / %d = %s ms/(load+add)%n",
                    action.time2, action.phaseShapesInserts,
                    ((double) action.time2) / action.phaseShapesInserts
            );
        }
        System.out.println("done");
    }

    private void execute(int count) {
        this.phaseShapesInserts = 0;
        final Random random = new Random(157);
        final long t0 = System.currentTimeMillis();
        this.phaseShapesInserts = count;
        for (int i = 0; i < count; i++) {
            final ORecord oShape = underlying.getRecord(new ORecordId(shapeClusterId, random.nextInt((int) shapeCount)));
            oPhaseShapes.add(oShape);
        }
        time1 = System.currentTimeMillis() - t0;
        oPhase.save();
        time2 = System.currentTimeMillis() - t0;
    }

}
