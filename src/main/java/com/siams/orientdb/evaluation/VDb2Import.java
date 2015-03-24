package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.Orient;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Import from Vector DB file
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 3/23/2015.
 */
public class VDb2Import {


    private final ByteBuffer buffer;
    private int recordCount;
    private int baseId;
    private int maxId;
    private long t0;

    public VDb2Import(String fileName) throws IOException {
        final RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r");
        final FileChannel channel = randomAccessFile.getChannel();
        final long size = channel.size();
        final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_ONLY, 0, size).load();
        buffer = map.order(ByteOrder.LITTLE_ENDIAN);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: VDbImport <fileName> [repeatCount]");
            System.exit(1);
            return;
        }
        final String fileName = args[0];
        final int repeatCount = args.length > 1 ? Integer.parseInt(args[1]) : 10;

        System.out.println("fileName: " + fileName);
        System.out.println("repeatCount: " + repeatCount);


        final String url = LocalDB.toURI("vector-db-2");
        final boolean newDB = !Orient.instance().loadStorage(url).exists();
        if (newDB) {
            VDb2Create.main("index-less");
        }

        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(url).open("admin", "admin")) {
            final VDb2Import action = new VDb2Import(fileName);

            db.declareIntent(new OIntentMassiveInsert());

            System.out.println("executing...");
            for (int i = 0; i < repeatCount; i++) {
                action.recordCount = 0;
                action.t0 = System.currentTimeMillis();
                action.execute(db);
                System.out.println("step " + (i + 1) + " (" + repeatCount + ")");
                action.printTime();
            }

            db.declareIntent(null);

            if (newDB) {
                System.out.println("creating index...");
                final OClass vShape = db.getMetadata().getSchema().getClass("VShape");
                vShape.createIndex("VShape.id", OClass.INDEX_TYPE.UNIQUE, "id");
            }
            System.out.println("shutting down...");
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

    private void execute(ODatabaseDocumentTx db) throws IOException {
        maxId = baseId;

        buffer.position(0);
        buffer.limit(buffer.capacity());
        while (buffer.remaining() > 20) {
            final int type = buffer.getInt();
            if (type != 1) break;

            final int length = (int) buffer.getLong();
            buffer.limit(buffer.position() + length);

            buffer.getInt();
            final int id = buffer.getInt();
            final int parentId = buffer.getInt();
            final int parentHole = buffer.getInt();
            final int holeCount = buffer.getInt();

            final List<ODocument> pathList = new ArrayList<>();
            while (buffer.remaining() > 4) {
                final List<ODocument> path = new ArrayList<>();
                final int pathLength = buffer.getInt();
                for (int i = 0; i < pathLength; i++) {
                    final float x = buffer.getFloat();
                    final float y = buffer.getFloat();
                    path.add(db.newInstance("VSP").field("x", x).field("y", y));
                }
                pathList.add(db.newInstance("VSPL").field("path", path));
            }

            addPolygon(db, id, pathList);

            buffer.limit(buffer.capacity());
        }

        baseId = maxId + 1;
    }

    private void addPolygon(ODatabaseDocumentTx db, int id, List<ODocument> path) {
        id += baseId;
        maxId = Math.max(id, maxId);

        final ODocument vsPolygon = db.newInstance("VSPolygon");
        vsPolygon.field("id", id);
        vsPolygon.field("path", db.newInstance("VSPLL").field("path", path));
        vsPolygon.save();
        recordCount++;
    }

}
