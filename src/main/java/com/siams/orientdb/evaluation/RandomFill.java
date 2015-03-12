package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alexei.vylegzhanin@gmail.com on 3/12/2015.
 */
public class RandomFill {

    private final Random random = new Random();
    private final byte[] imageData = new byte[256 * 256];
    private int recordCount;
    private int nextTileId;

    private RandomFill() {
        random.nextBytes(imageData);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1 && args[1].equals("--create")) CreateDB.main(args);

        final RandomFill action = new RandomFill();
        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(DbTools.getLocalDbPath(args))
                .open("admin", "admin")) {

            db.declareIntent(new OIntentMassiveInsert());

            System.out.println("warming up...");
            action.execute(db, 100, 1000);

            System.out.println("executing...");
            action.recordCount = 0;
            final long t0 = System.currentTimeMillis();
            action.execute(db, 2048, 100);
            final long t1 = System.currentTimeMillis();
            final long time = t1 - t0;
            System.out.printf("done: %dms / %d = %s ms/record%n",
                    time, action.recordCount,
                    ((double) time) / action.recordCount
            );
            action.saveConfiguration(db);
        }
    }

    private void saveConfiguration(ODatabaseDocumentTx db) {
        db.newInstance("Configuration")
                .field("maxTileId", nextTileId)
                .field("imageSize", imageData.length)
                .save();
    }

    private void execute(ODatabaseDocumentTx db, int tileCount, int particleCount) {
        for (int ti = 0; ti < tileCount; ti++) {
            final List<ODocument> particles = new ArrayList<>();
            for (int pi = 0; pi < particleCount; pi++) {
                final ODocument particle = db.newInstance("Particle");
                particle
                        .field("x", random.nextLong())
                        .field("y", random.nextLong());
                particle.save();
                recordCount++;
                particles.add(particle);
            }
            final ODocument tile = db.newInstance("Tile");
            tile
                    .field("id", nextTileId++)
                    .field("x", random.nextLong())
                    .field("y", random.nextLong())
                    .field("image", new ORecordBytes(imageData))
                    .field("particles", particles)
            ;
            tile.save();
            recordCount++;
        }

    }
}
