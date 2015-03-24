package com.siams.orientdb.evaluation;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OSchemaProxy;
import com.orientechnologies.orient.core.metadata.schema.OType;

import java.io.IOException;

/**
 * Vector DB2 Test
 * <p>
 * Created by alexei.vylegzhanin@gmail.com on 3/23/2015.
 */
public class VDb2Create {
    public static void main(String... args) throws IOException {
        try (final ODatabaseDocumentTx db = new ODatabaseDocumentTx(LocalDB.toURI("vector-db-2")).create()) {
            System.out.println("created: " + db.getURL());

            final OSchemaProxy schema = db.getMetadata().getSchema();

            final OClass vsp = schema.createClass("VSP");
            vsp.createProperty("x", OType.FLOAT);
            vsp.createProperty("y", OType.FLOAT);

            final OClass vspl = schema.createClass("VSPL");
            vspl.createProperty("path", OType.EMBEDDEDLIST, vsp);

            final OClass vspll = schema.createClass("VSPLL");
            vspll.createProperty("path", OType.EMBEDDEDLIST, vspl);

            final OClass vShape = schema.createClass("VShape");
            vShape.createProperty("id", OType.INTEGER);

            final OClass vsPolygon = schema.createClass("VSPolygon", vShape);
            vsPolygon.createProperty("path", OType.LINK, vspll);

            final boolean indexLess = args.length > 0 && args[0].equals("index-less");
            if (indexLess) {
                System.out.println("index-less");
            } else {
                vShape.createIndex("VShape.id", OClass.INDEX_TYPE.UNIQUE, "id");
            }
        }
        System.out.println("done");
    }

}
