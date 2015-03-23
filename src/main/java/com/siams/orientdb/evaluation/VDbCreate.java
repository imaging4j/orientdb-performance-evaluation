package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;

import java.io.IOException;

/**
 * Vector DB Test
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 3/23/2015.
 */
public class VDbCreate {
    public static void main(String[] args) throws IOException {
        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(LocalDB.toURI("vector-db")).create()) {
            System.out.println("created: " + db.getURL());

            final OSchemaProxy schema = db.getMetadata().getSchema();

            final OClass vGeometry = schema.createClass("VGeometry");
            vGeometry.createProperty("id", OType.INTEGER);

            final OClass vgPolygon = schema.createClass("VGPolygon", vGeometry);
            vgPolygon.createProperty("pathBytes", OType.LINK);
            vgPolygon.createProperty("pathCount", OType.INTEGER);
            vgPolygon.createProperty("pathSize", OType.INTEGER);

            vgPolygon.createIndex("VGPolygon.id", OClass.INDEX_TYPE.UNIQUE, "id");
        }
        System.out.println("done");
    }

}
