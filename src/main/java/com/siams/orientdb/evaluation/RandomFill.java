package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
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
    private long t0;

    private RandomFill(ODatabaseDocumentTx db) {
        random.nextBytes(imageData);
        nextTileId = DbTools.getMaxTileId(db) + 1;
    }

    public static void main(String[] args) throws IOException {
        final String url = DbTools.getLocalDbPath(args);
        if (!Orient.instance().loadStorage(url).exists()) {
            CreateDB.main(args);
        }

        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(url)
                .open("admin", "admin")) {
            final RandomFill action = new RandomFill(db);

            db.declareIntent(new OIntentMassiveInsert());

            System.out.println("warming up...");
            action.execute(db, 100, 1000);

            System.out.println("executing...");
            action.recordCount = 0;
            action.t0 = System.currentTimeMillis();
            action.execute(db, 65536, 100);
            action.printTime();
            action.saveConfiguration(db);

            db.declareIntent(null);
            db.close();

            System.out.println("shutdown...");
            Orient.instance().shutdown();
            action.printTime();
        }
    }

    private void printTime() {
        final long t1 = System.currentTimeMillis();
        final long time = t1 - t0;
        System.out.printf("done: %dms / %d = %s ms/record%n",
                time, recordCount,
                ((double) time) / recordCount
        );
    }

    private void saveConfiguration(ODatabaseDocumentTx db) {
        final ORecordIteratorClass<ODocument> configurations = db.browseClass("Configuration");
        final ODocument configuration;
        if (configurations.hasNext()) {
            configuration = configurations.next();
        } else {
            configuration = db.newInstance("Configuration");
        }
        configuration
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
