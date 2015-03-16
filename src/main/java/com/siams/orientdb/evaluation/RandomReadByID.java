package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.ODatabaseRecordThreadLocal;
import com.orientechnologies.orient.core.db.ODatabaseThreadLocalFactory;
import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;

import java.io.IOException;
import java.util.AbstractList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Created by alexei.vylegzhanin@gmail.com on 3/12/2015.
 */
public class RandomReadByID {

    private final Random random = new Random();
    private boolean parallel;
    private boolean readImage;
    private boolean readParticleList;
    private boolean readParticleValues;
    private final int maxId;
    private final int imageSize;
    private final AtomicInteger recordCount = new AtomicInteger();


    public RandomReadByID(String[] args) {
        final String modePattern = "--mode=";
        if (args.length > 1 && args[1].startsWith(modePattern)) {
            final String modeSet = args[1].substring(modePattern.length());
            parallel = modeSet.contains("p");
            readImage = modeSet.contains("I");
            readParticleList = modeSet.contains("P");
            readParticleValues = modeSet.contains("V");
        }
        System.out.println("parallel: " + parallel);
        System.out.println("readImage: " + readImage);
        System.out.println("readParticleList: " + readParticleList);
        System.out.println("readParticleValues: " + readParticleValues);

        final ODatabaseDocument db = ODatabaseRecordThreadLocal.INSTANCE.get();
        maxId = DbTools.getMaxTileId(db);
        imageSize = db.browseClass("Configuration").next().field("imageSize", OType.INTEGER);
        System.out.println("# tile maxId: " + maxId);
        System.out.println("# tile imageSize: " + imageSize);
    }

    public static void main(String[] args) throws IOException {

        final String url = DbTools.getLocalDbPath(args);
        if (!Orient.instance().loadStorage(url).exists()) {
            RandomFill.main(args);
            Orient.instance().startup();
        }

        Orient.instance().registerThreadDatabaseFactory(new ODatabaseThreadLocalFactory() {
            final OPartitionedDatabasePool pool = new OPartitionedDatabasePool(
                    url, "admin", "admin");

            @Override
            public ODatabaseDocumentInternal getThreadDatabase() {
                System.out.println("# pool.acquire for " + Thread.currentThread());
                return pool.acquire();
            }
        });
        final RandomReadByID action = new RandomReadByID(args);

        System.out.println("warming up...");
        new IntegerList(10000).stream().forEach(action::executeStep);

        System.out.println("executing...");
        action.recordCount.set(0);
        final Stream<Integer> stream = action.parallel
                ? new IntegerList(100000).stream().parallel()
                : new IntegerList(100000).stream();
        final long t0 = System.currentTimeMillis();
        stream.forEach(action::executeStep);
        final long t1 = System.currentTimeMillis();
        final long time = t1 - t0;
        System.out.printf("done: %dms / %d = %s ms/record%n",
                time, action.recordCount.get(),
                ((double) time) / action.recordCount.get()
        );
    }

    private void executeStep(Integer step) {
        final ODatabaseDocument db = ODatabaseRecordThreadLocal.INSTANCE.get();
        try {
            final OIndex<?> tileIdIdx = db.getMetadata().getIndexManager().getIndex("TileIdIdx");
            final Integer id = random.nextInt(maxId);
            final ORecordId recordId = (ORecordId) tileIdIdx.get(id);
            final ODocument tile = recordId.getRecord();
            recordCount.getAndIncrement();
            if (readImage) {
                final ORecordBytes image = tile.field("image", OType.LINK);
                if (image.getSize() != imageSize) {
                    System.err.println("Invalid tile[" + id + "].image size: " + image.getSize());
                }
                recordCount.getAndIncrement();
            }
            if (readParticleList) {
                final List<ODocument> particles = tile.field("particles", OType.LINKLIST);
                recordCount.getAndIncrement();
                if (readParticleValues) {
                    for (final ODocument particle : particles) {
                        final Long x = particle.field("x", OType.LONG);
                        if (x == null) {
                            System.err.println("tile[" + id + "].particle[n].x == null");
                        }
                        recordCount.getAndIncrement();
                    }
                }
            }
        } finally {
        //    db.close();
        }
    }

    private static class IntegerList extends AbstractList<Integer> {
        private final int size;

        public IntegerList(int size) {
            this.size = size;
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public Integer get(int index) {
            return index;
        }
    }
}
