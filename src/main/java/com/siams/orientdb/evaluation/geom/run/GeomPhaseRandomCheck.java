package com.siams.orientdb.evaluation.geom.run;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.ORecord;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
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
public class GeomPhaseRandomCheck {

    private long time1;
    private final int shapeClusterId;
    private final OObjectDatabaseTx db;
    private final long shapeCount;
    private long phaseShapesChecks;
    private final ODatabaseDocumentInternal underlying;
    private final ODocument oPhase;
    private final Set<ORecord> oPhaseShapes;

    public GeomPhaseRandomCheck(OObjectDatabaseTx db, long shapeCount) {
        this.db = db;
        this.shapeCount = shapeCount;
        final Phase phase = (Phase) db.query(new OSQLSynchQuery<>("select * from Phase where name = 'abc'")).get(0);
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
            final GeomPhaseRandomCheck action = new GeomPhaseRandomCheck(db, shapeCount);
            action.execute((int) warmingCount);

            System.out.println("executing(" + shapeCount + ")...");
            action.execute((int) shapeCount);

            System.out.printf("phase shapes checks:\t%d%n", action.phaseShapesChecks);

            System.out.printf("phase shapes checking: %dms / %d = %s ms/(load+contains)%n",
                    action.time1, action.phaseShapesChecks,
                    ((double) action.time1) / action.phaseShapesChecks
            );
        }
        System.out.println("done");
    }

    private void execute(int count) {
        this.phaseShapesChecks = 0;
        final Random random = new Random(157);
        final long t0 = System.currentTimeMillis();
        this.phaseShapesChecks = count;
        for (int i = 0; i < count; i++) {
            final ORecord oShape = underlying.getRecord(new ORecordId(shapeClusterId, random.nextInt((int) shapeCount)));
            oPhaseShapes.contains(oShape);
        }
        time1 = System.currentTimeMillis() - t0;
    }

}
