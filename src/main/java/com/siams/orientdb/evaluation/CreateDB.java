package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ORecordBytes;

import java.io.IOException;

/**
 * Created by alexei.vylegzhanin@gmail.com on 3/11/2015.
 */
public class CreateDB {
    public static void main(String[] args) throws IOException {
        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(DbTools.getLocalDbPath(args)).create()) {
            System.out.println("created: " + db.getURL());

            final OSchemaProxy schema = db.getMetadata().getSchema();
            final OClass configuration = schema.createClass("Configuration");
            configuration.createProperty("maxTileId", OType.INTEGER);
            configuration.createProperty("imageSize", OType.INTEGER);


            final OClass particle = schema.createClass("Particle");
            particle.createProperty("x", OType.LONG);
            particle.createProperty("y", OType.LONG);

            final OClass tile = schema.createClass("Tile");
            tile.createProperty("id", OType.INTEGER);
            tile.createProperty("x", OType.LONG);
            tile.createProperty("y", OType.LONG);
            tile.createProperty("image", OType.LINK);
            tile.createProperty("particles", OType.LINKLIST, particle);
            tile.createIndex("TileIdIdx", OClass.INDEX_TYPE.UNIQUE, "id");
        }
        System.out.println("closed");
    }

}
